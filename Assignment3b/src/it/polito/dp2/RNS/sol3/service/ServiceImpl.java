package it.polito.dp2.RNS.sol3.service;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.datatype.XMLGregorianCalendar;

import it.polito.dp2.RNS.ConnectionReader;
import it.polito.dp2.RNS.FactoryConfigurationError;
import it.polito.dp2.RNS.GateReader;
import it.polito.dp2.RNS.GateType;
import it.polito.dp2.RNS.ParkingAreaReader;
import it.polito.dp2.RNS.PlaceReader;
import it.polito.dp2.RNS.RnsReader;
import it.polito.dp2.RNS.RnsReaderException;
import it.polito.dp2.RNS.RnsReaderFactory;
import it.polito.dp2.RNS.RoadSegmentReader;
import it.polito.dp2.RNS.sol3.service.exception.BadStateException;
import it.polito.dp2.RNS.sol3.service.exception.ModelException;
import it.polito.dp2.RNS.sol3.service.exception.ServiceException;
import it.polito.dp2.RNS.sol3.service.exception.UnknownIdException;
import it.polito.dp2.RNS.sol3.service.jaxb.rnsneo4j.Neo4JNode;
import it.polito.dp2.RNS.sol3.service.jaxb.rnsneo4j.Neo4JNodeResponse;
import it.polito.dp2.RNS.sol3.service.jaxb.rnsneo4j.Neo4JPath;
import it.polito.dp2.RNS.sol3.service.jaxb.rnsneo4j.Neo4JPath.Relationships;
import it.polito.dp2.RNS.sol3.service.jaxb.rnsneo4j.Neo4JPathResponse;
import it.polito.dp2.RNS.sol3.service.jaxb.rnsneo4j.Neo4JRelationship;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.Connection;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.Connections;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.Gate;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.ParkingArea;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.Places;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.RoadSegment;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.Vehicle;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.VehiclePath;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.VehiclePosition;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.VehicleState;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.VehicleTrack;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.Vehicles;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.Webplace;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.Webrns;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.Webvehicle;
import it.polito.dp2.RNS.sol3.service.resource.ResponseService;
import it.polito.dp2.RNS.sol3.service.resource.ResponseStatus;
import it.polito.dp2.RNS.sol3.service.storage.RnsDB;
import it.polito.dp2.RNS.sol3.service.util.ConnectionComparator;
import it.polito.dp2.RNS.sol3.service.util.ConnectionReaderComparator;
import it.polito.dp2.RNS.sol3.service.util.PlaceComparator;
import it.polito.dp2.RNS.sol3.service.util.Util;
import it.polito.dp2.RNS.sol3.service.util.WebPlaceComparator;
import it.polito.dp2.RNS.sol3.service.util.WebVehicleComparator;

public class ServiceImpl {
	/**
	 * set the verbosity for debug printing
	 * 0 - no debug printing
	 * 1 - print only the list of shortest paths
	 * 2 - print all information
	 */
	private int DEBUG_PRINTING_LEVEL = 0;
	private Logger logger;
	private final String BASE_URL_NEO4J = "it.polito.dp2.RNS.lab3.Neo4JURL";
	private final String BASE_URL_NEO4J_DEFAULT = "http://localhost:7474/db";
	private final String SERVICE_ROOT = "data";
	private final String NODE_ROOT = "node";
	private final String NODE_RELATIONSHIP = "relationship";
	private final String NODE_RELATIONSHIPS = "relationships";
	private final String NODE_PATHS = "paths";
	private final String PATH_VEHICLES = "vehicles";
	private final String PATH_CONNECTIONS = "connections";
	private final String RELATIONSHIP_TYPE = "ConnectedTo";
	private final String RELATIONSHIP_DIRECTION = "out";
	private final String PATH_ALGORITHM = "shortestPath";
	private final String PATH_PLACES = "places";
	private final String PATH_PARKING_AREA = "parkingarea";
	private final String PATH_GATE = "gate";
	private final String PATH_ROAD_SEGMENT = "roadsegment";
	private final String PATH_PAGE = "page";
	private final String PATH_TYPE = "type";
	private final int PAGE_SIZE = 15;
	private String url_neo4j;
	private RnsReader monitor;
	private RnsDB db;
	private WebTarget webTarget;
	private boolean modelLoaded;
	
	private ServiceImpl() {
		logger = Logger.getLogger(ServiceImpl.class.getName());
		url_neo4j = System.getProperty(BASE_URL_NEO4J);
		
		if(url_neo4j == null)
			url_neo4j = BASE_URL_NEO4J_DEFAULT;

		monitor = null;
		db =  new RnsDB();

		modelLoaded = false;
		
		try {
			reloadModel();
			
			if(monitor == null)
				throw new ServiceException("model null");
		} catch (ServiceException | ModelException e) {
			logger.log(Level.SEVERE, "Cannot reload model", e);
			// service must not terminate
			monitor = null;
			//throw new InternalServerErrorException();
		}
	}
		
	/**
	 * static inner class that contains the singleton instance
	 */
	private static class ServiceImplInstanceHolder {
		// Singleton implementation of the RNS service using Bill Pugh pattern
		private static final ServiceImpl INSTANCE = new ServiceImpl();
	}
	
	/**
	 * Get the singleton instance for this service
	 * @return
	 * the singleton service instance
	 */
	public static ServiceImpl getInstance() {
		return ServiceImplInstanceHolder.INSTANCE;
	}
	
	/**
	 * Checks the current state
	 * @return true if the current state is the operating state (model loaded)
	 */
	private boolean isModelLoaded() {
		return modelLoaded;
	}
	
	/**
	 * Loads the current version of the model so that, if the operation is successful,
	 * after the operation the PathFinder is in the operating state (model loaded) and
	 * it can compute shortest paths on the loaded model.
	 * @throws ServiceException if the operation cannot be completed because the remote service is not available or fails
	 * @throws ModelException if the operation cannot be completed because the current model cannot be read or is wrong (the problem is not related to the remote service)
	 */
	private void reloadModel() throws ServiceException, ModelException {

		try {
			// create new rns system
			monitor = RnsReaderFactory.newInstance().newRnsReader();
			if(monitor == null)
				throw new ServiceException("Error, cannot instantiate RnsReader!");
		} catch (RnsReaderException | FactoryConfigurationError e) {
			logger.log(Level.SEVERE, "Error, cannot instantiate PathFinder!", e);
			throw new ServiceException("Error, cannot instantiate PathFinder!");
		}
		
		// create a client using neo4j base url
		Client client = createNeo4jClientAndWebTarget();
		
		// second time I call reloadModel
		if(isModelLoaded()) {
			// reset flag
			modelLoaded = false;
			// delete old model
			deleteNeo4jGraph();
			
			db.clearMapPlaceReader();
			db.clearMapPlaceCapacity();
			db.clearMapWebVehicles();
		}
		
		// fill PlaceReader database
		Set<GateReader> gates = getGates(null);
		for(GateReader g: gates) {
			db.addPlaceReader(g.getId(), g);
		}
		Set<ParkingAreaReader> parkingAreas = getParkingAreas(null);
		for(ParkingAreaReader pa: parkingAreas) {
			db.addPlaceReader(pa.getId(), pa);
		}
		Set<RoadSegmentReader> roadSegments = getRoadSegments(null);
		for(RoadSegmentReader rs: roadSegments) {
			db.addPlaceReader(rs.getId(), rs);
		}
		
		Set<PlaceReader> places = getPlaces(null);
		
		// create a graph node for each place
		for(PlaceReader place: places) {
			db.addPlaceCapacity(place.getId(), place.getCapacity());
			
			createGraphNode(place);

			// also create graph node for each next place
			// and create graph relationships
			Set<PlaceReader> nextPlaces = place.getNextPlaces();
			for(PlaceReader nextPlace: nextPlaces) {
				createGraphNode(nextPlace);

				createGraphRelationship(place, nextPlace);
			}
		}

		// clear memory
		client.close();
		
		// all nodes and relationships were uploaded to neo4j db
		// update flag
		modelLoaded = true;
	}
	
	/**
	 * Looks for the shortest paths connecting a source place to a destination place
	 * Each path is returned as a list of place identifiers, where the first place in the list is the source
	 * and the last place is the destination.
	 * @param source The id of the source of the paths to be found
	 * @param destination The id of the destination of the paths to be found
	 * @param maxlength The maximum length of the paths to be found (0 or negative means no bound on the length)
	 * @return the set of the shortest paths connecting source to destination
	 * @throws UnknownIdException if source or destination is not a known place identifier
	 * @throws BadStatedException if the operation is called when in the initial state (no model loaded)
	 * @throws ServiceException if the operation cannot be completed because the remote service is not available or fails
	 */
	private Set<List<String>> findShortestPaths(String source, String destination, int maxlength)
			throws UnknownIdException, BadStateException, ServiceException {

		// flags used to see if source and destination are present in the set of places
		boolean validSource = false, validDestination = false;
		int maxDepth;

		// check that model was loaded
		if(!isModelLoaded())
			throw new BadStateException("Error, the model is not loaded!");

		Set<PlaceReader> places = getPlaces(null);

		// check that source and destination are valid place ids
		for(PlaceReader place: places) {
			if(place.getId().equals(source)) {
				// found source
				validSource = true;
			}
			if(place.getId().equals(destination)) {
				// found destination
				validDestination = true;
			}
		}

		if(!validSource || !validDestination)
			throw new UnknownIdException("Error, source and destination must be valid place ids!");	

		// if maxlength < 1, then assume the maxDepth to be the number of nodes-1
		// e.g. for n+2 nodes and path: Gate1->RS1->...->RSn->Gate2 maxlength is n+1
		if(maxlength < 1)
			maxDepth = 	places.size() - 1;
		else maxDepth = maxlength;

		// create web target for the base URL and a client 
		Client client = ClientBuilder.newClient();
		// create new webtarget each time and do not use global variable
		// to avoid concurrency issues
		WebTarget webTarget = client.target(url_neo4j).path(SERVICE_ROOT);

		Set<List<String>> paths = new HashSet<>();

		// get the list of paths from neo4j
		Neo4JPath neo4jPath = new Neo4JPath();

		Relationships neo4jRelationships = new Relationships();
		neo4jRelationships.setType(RELATIONSHIP_TYPE);
		neo4jRelationships.setDirection(RELATIONSHIP_DIRECTION);

		// link to destination node for search
		URI to = UriBuilder.fromUri(url_neo4j)
				.path(SERVICE_ROOT)
				.path(NODE_ROOT)
				.path(db.getNode(destination).toString())
				.build();
		neo4jPath.setTo(to.toString());
		neo4jPath.setMaxDepth(BigInteger.valueOf(maxDepth));
		neo4jPath.setRelationships(neo4jRelationships);
		neo4jPath.setAlgorithm(PATH_ALGORITHM);

		Response response = null;
		try {
			response = webTarget.path(NODE_ROOT)
					.path(db.getNode(source).toString())
					.path(NODE_PATHS)
					.request(MediaType.APPLICATION_JSON)
					.post(Entity.entity(neo4jPath, MediaType.APPLICATION_JSON));
		} catch (RuntimeException e) {
			logger.log(Level.SEVERE, "Error, cannot post graph path entity", e);
			throw new ServiceException("Error, cannot post graph path entity");
		}		

		// check that the response has a code 200 and a body 
		if(!isValidResponse(response) || !response.hasEntity())
			throw new ServiceException("Error, wrong response for path POST");

		List<Neo4JPathResponse> neo4jPathResponses = null;
		try {
			//Note: required if I want to perform more than one readEntity() operation
			response.bufferEntity();
	
			// DEBUG printing
			if(DEBUG_PRINTING_LEVEL == 2) {
				logger.log(Level.INFO, response.readEntity(String.class));
			}
	
			// validate result
			// get the list of paths from POST response
			// Note: I have to wrap inside GenericType<T> instead of using Class<T> 
			// because Java compiler erases parameterized type information
			neo4jPathResponses = response.readEntity(new GenericType<List<Neo4JPathResponse>>(){});
		} catch (ProcessingException e) {
			logger.log(Level.SEVERE, "Error, cannot read graph path entity", e);
			throw new ServiceException("Error, cannot read graph path entity");
		}
		
		// convert the paths to the correct format: Set<List<String>>
		for(Neo4JPathResponse neo4jPathResponse: neo4jPathResponses) {
			List<String> path = new ArrayList<>();
			for(String p: neo4jPathResponse.getNodes()) {
				String e = db.gePlaceCorrespondingNode(parseStringToInteger(getLastParamURL(p)));
				if(e == null)
					throw new ServiceException("Error, cannot find place corresponding to node");
				path.add(e);
			}

			paths.add(path);
		}

		// DEBUG printing
		if(DEBUG_PRINTING_LEVEL > 0) {
			StringBuilder sb = new StringBuilder();
			for(List<String> p: paths) {
				sb.append("Path from ").append(source).append(" to ").append(destination).append(": ").append(System.getProperty("line.separator"));
				for(String s: p)
					sb.append(s).append(" ");
				sb.append(System.getProperty("line.separator"));
			}
			logger.log(Level.INFO, sb.toString());
		}

		// clear memory
		response.close();			
		client.close();

		return paths;
	}	
	
	/**
	 * Creates a new JAX-RS client
	 * and initializes the JAX-RS WebTarget with the BASE_URL
	 * @return
	 */
	private Client createNeo4jClientAndWebTarget() {
		Client client = ClientBuilder.newClient();
		webTarget = client.target(url_neo4j).path(SERVICE_ROOT);	

		return client;
	}

	/**
	 * Creates a new Neo4j graph node from a place
	 * @throws ServiceException 
	 */
	private void createGraphNode(PlaceReader place) throws ServiceException {
		// create node if it does not exist
		if(!db.containsNode(place.getId())) {
			
			Neo4JNode neo4jNode = new Neo4JNode();
			neo4jNode.setId(place.getId());

			Response responseNode = null;
			try {
				responseNode = webTarget.path(NODE_ROOT).request(MediaType.APPLICATION_JSON)
						.post(Entity.entity(neo4jNode, MediaType.APPLICATION_JSON));
			} catch (RuntimeException e) {
				logger.log(Level.SEVERE, "Error, cannot post graph node entity", e);
				throw new ServiceException("Error, cannot post graph node entity");
			}			

			// check that the response has a code 200 and a body 
			if(!isValidResponse(responseNode) || !responseNode.hasEntity())
				throw new ServiceException("Error, wrong response for node POST");

			Neo4JNodeResponse neo4jNodeResponse = null;
			try {
				//Note: required if I want to perform more than one readEntity() operation
				responseNode.bufferEntity();
	
				// DEBUG printing
				if(DEBUG_PRINTING_LEVEL == 2) {
					logger.log(Level.INFO, responseNode.readEntity(String.class));
				}	
	
				// validate result
				neo4jNodeResponse = responseNode.readEntity(Neo4JNodeResponse.class);
			} catch (ProcessingException e) {
				logger.log(Level.SEVERE, "Error, cannot read graph node entity", e);
				throw new ServiceException("Error, cannot read graph node entity");
			}
			
			// save the association [place-id, neo4jNode-id]
			db.addNode(place.getId(), parseStringToInteger(getLastParamURL(neo4jNodeResponse.getSelf())));

			// clear memory
			responseNode.close();
		}		
	}	

	/**
	 * Creates a new Neo4j graph relationship
	 * @throws ServiceException 
	 */
	private void createGraphRelationship(PlaceReader placeFrom, PlaceReader placeTo) throws ServiceException {
		Neo4JRelationship neo4jRelationship = new Neo4JRelationship();

		URI to = UriBuilder.fromUri(url_neo4j)
				.path(SERVICE_ROOT)
				.path(NODE_ROOT)
				.path(db.getNode(placeTo.getId()).toString())
				.build();
		neo4jRelationship.setTo(to.toString());
		neo4jRelationship.setType(RELATIONSHIP_TYPE);

		Response responseRelationship = null;
		try {
			responseRelationship = webTarget.path(NODE_ROOT)
					.path(db.getNode(placeFrom.getId()).toString())
					.path(NODE_RELATIONSHIPS)
					.request(MediaType.APPLICATION_JSON)
					.post(Entity.entity(neo4jRelationship, MediaType.APPLICATION_JSON));
		} catch (RuntimeException e) {
			logger.log(Level.SEVERE, "Error, cannot post graph relationship entity", e);
			throw new ServiceException("Error, cannot post graph relationship entity");
		}

		// check that the response has a code 200 and a body 
		if(!isValidResponse(responseRelationship) || !responseRelationship.hasEntity())
			throw new ServiceException("Error, wrong response for relationship POST");

		Neo4JNodeResponse neo4jRelationshipResponse = null;
		try {
			//Note: required if I want to perform more than one readEntity() operation
			responseRelationship.bufferEntity();
	
			// DEBUG printing
			if(DEBUG_PRINTING_LEVEL == 2) {
				logger.log(Level.INFO, responseRelationship.readEntity(String.class));
			}
			
			// validate result
			neo4jRelationshipResponse = responseRelationship.readEntity(Neo4JNodeResponse.class);
		} catch (ProcessingException e) {
			logger.log(Level.SEVERE, "Error, cannot read graph relationship entity", e);
			throw new ServiceException("Error, cannot read graph relationship entity");
		}
		
		// save the association [place-id, neo4jNode-id]
		db.addRelationship(parseStringToInteger(getLastParamURL(neo4jRelationshipResponse.getSelf())));

		// clear memory
		responseRelationship.close();			
	}
	
	/**
	 * Deletes the current Neo4j graph nodes and relationships from the database
	 * and
	 * @throws ServiceException 
	 */
	private void deleteNeo4jGraph() throws ServiceException {	
		// delete graph relationships
		for(Integer r: db.getSetRelationships()) { 
			Response responseRelationship = null;
			try {
				responseRelationship = webTarget.path(NODE_RELATIONSHIP)
						.path(r.toString())
						.request(MediaType.APPLICATION_JSON)
						.delete();
			} catch (RuntimeException e) {
				logger.log(Level.SEVERE, "Error, cannot delete graph relationship entity", e);
				throw new ServiceException("Error, cannot delete graph relationship entity");
			}			

			// check that the response has a code 200 
			if(!isValidResponse(responseRelationship))
				throw new ServiceException("Error, wrong response for relationship DELETE");	

			// clear memory
			responseRelationship.close();			

		}
		db.clearSetRelationships();

		// delete graph nodes
		for(Entry<String, Integer> n: db.getMapNodes().entrySet()) {
			Response responseNode = null;
			try {
				responseNode = webTarget.path(NODE_ROOT)
						.path(n.getValue().toString())
						.request(MediaType.APPLICATION_JSON)
						.delete();
			} catch (RuntimeException e) {
				logger.log(Level.SEVERE, "Error, cannot delete graph node entity", e);
				throw new ServiceException("Error, cannot delete graph node entity");
			}

			// check that the response has a code 200 
			if(!isValidResponse(responseNode))
				throw new ServiceException("Error, wrong response for node DELETE");

			// clear memory
			responseNode.close();			
		}
		db.clearMapNodes();
	}	
	
	/**
	 * check if the response has a result code 200
	 * @param response
	 * @return
	 */
	private boolean isValidResponse(Response response) {
		if(response == null || response.getStatus() >= 400)
			return false;
		return true;
	}

	/**
	 * Gets the last parameter of an url and returns it
	 * e.g for: https://localhost:7474/db/node/node/33 it returns 33
	 * @param url
	 * @return
	 * @throws ServiceException
	 */
	private String getLastParamURL(String url) throws ServiceException {
		URI uri;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			logger.log(Level.SEVERE, "Error, invalid url", e);
			throw new ServiceException("Error, invalid url!");		
		}
		return uri.getPath().substring(uri.getPath().lastIndexOf('/') + 1);
	}

	/**
	 * parses a string to an Integer Object
	 * @param s
	 * @return
	 * @throws ServiceException 
	 */
	private Integer parseStringToInteger(String s) throws ServiceException {
		Integer integer;
		try {
			integer = new Integer(s);
		} catch (NumberFormatException e) {
			logger.log(Level.SEVERE, "Error, cannot parse string to integer", e);
			throw new ServiceException("Error, cannot parse string to integer!");		
		}		

		return integer;
	}
	
	/**
	 * Creates a Webplace given the id of a place
	 * @param id
	 * @param baseURI 
	 * @return a Webplace or null
	 */
	private Webplace createWebPlace(String id, String baseURI) {
		
		if(id == null || !containsPlace(id))
			return null;
		
		Webplace wp = new Webplace();
		
		Object p = db.getPlaceReader(id);
		
		if(p instanceof GateReader) {
			wp.setGate(createJAXBGate((GateReader)p));
		}
		else if(p instanceof RoadSegmentReader) {
			wp.setRoadSegment(createJAXBRoadSegment((RoadSegmentReader)p));
		}
		else if(p instanceof ParkingAreaReader) {
			wp.setParkingArea(createJAXBParkingArea((ParkingAreaReader)p));
		}
		else {
			return null;
		}

		URI self = UriBuilder.fromUri(baseURI)
				.path(PATH_PLACES).path(id).build();
		wp.setSelf(self.toString());
		
		URI vehiclesURI = UriBuilder.fromUri(baseURI)
				.path(PATH_PLACES).path(id).path(PATH_VEHICLES).build();
		wp.setVehicles(vehiclesURI.toString());
		
		return wp;
	}
	
	/**
	 * Creates a Connection given a 'from' and 'to' place
	 * @param from
	 * @param to
	 * @param baseURI
	 * @return
	 */
	private Connection createConnection(String from, String to, String baseURI) {
		
		if(from == null || from == null || !containsPlace(from) || !containsPlace(from))
			return null;
		
		Connection c = new Connection();
		
		URI fromURI = UriBuilder.fromUri(baseURI)
				.path(PATH_PLACES).path(from).build();
		URI toURI = UriBuilder.fromUri(baseURI)
				.path(PATH_PLACES).path(to).build();
		c.setFrom(fromURI.toString());
		c.setTo(toURI.toString());
		return c;
	}
	
	/**
	 * Try to add a vehicle in the rns system
	 * @param vehicleTrack
	 * @return
	 */
	public synchronized ResponseService trackVehicle(VehicleTrack vehicleTrack, String baseURI) {
		if(monitor == null) {
			ResponseStatus responseStatus = new ResponseStatus(500, "service not available");
			return new ResponseService(responseStatus, null);
		}
		
		// no input
		if(vehicleTrack == null) {
			ResponseStatus responseStatus = new ResponseStatus(403, "permission to enter is not granted because no input was provided");
			return new ResponseService(responseStatus, null);
		}
		
		// vehicle is already tracked
		if(db.containsWebvehicle(vehicleTrack.getId())) {
			ResponseStatus responseStatus = new ResponseStatus(403, "permission to enter is not granted because vehicle is already tracked");
			return new ResponseService(responseStatus, null);
		}
		
		// source or the destination is not a known place
		if(!db.containsPlaceReader(vehicleTrack.getOrigin()) ||
				!db.containsPlaceReader(vehicleTrack.getDestination())) {
			ResponseStatus responseStatus = new ResponseStatus(404, "source or the destination is not a known place");
			return new ResponseService(responseStatus, null);
		}
		
		// entrance place is not a gate
		if(!(db.getPlaceReader(vehicleTrack.getOrigin()) instanceof GateReader)) {
			ResponseStatus responseStatus = new ResponseStatus(400, "permission to enter is not granted because entrance place is not a gate");
			return new ResponseService(responseStatus, null);
		}
		
		// place is a gatereader
		GateReader g = (GateReader) db.getPlaceReader(vehicleTrack.getOrigin());
		// inGate is not the id of an IN or INOUT gate
		if(g.getType().equals(GateType.OUT)) {
			ResponseStatus responseStatus = new ResponseStatus(400, "permission to enter is not granted because inGate is not the id of an IN or INOUT gate");
			return new ResponseService(responseStatus, null);
		}
		
		Webvehicle wv = new Webvehicle();
		
		Vehicle v = new Vehicle();
		v.setDestination(vehicleTrack.getDestination());
		v.setOrigin(vehicleTrack.getOrigin());
		v.setPosition(vehicleTrack.getOrigin());
		v.setId(vehicleTrack.getId());
		v.setType(vehicleTrack.getType());
		v.setState(VehicleState.IN_TRANSIT);
		XMLGregorianCalendar date = Util.getCurrentDateTime();
		if(date == null) {
			// internal server error
			ResponseStatus responseStatus = new ResponseStatus(500, "Cannot get current date");
			return new ResponseService(responseStatus, null);
		}	
		v.setEntryTime(date);
		wv.setVehicle(v);
		
		URI self = UriBuilder.fromUri(baseURI)
				.path(PATH_VEHICLES).path(v.getId()).build();
		wv.setSelf(self.toString());
		
		// create a random path for the vehicle
		List<String> path = generatePath(wv.getVehicle().getPosition(), wv.getVehicle().getDestination());
		if(path == null) {
			// internal server error
			ResponseStatus responseStatus = new ResponseStatus(500, "Cannot create neo4j path");
			return new ResponseService(responseStatus, null);
		}

		VehiclePath vehiclePath = new VehiclePath();
		vehiclePath.getPlace().addAll(path);
		wv.setVehiclePath(vehiclePath);
		
		// update place availability
		// place availability exceeded
		if(db.decrementPlaceCapacity(vehicleTrack.getOrigin()) == null) {
			ResponseStatus responseStatus = new ResponseStatus(403, "permission to enter is not granted because vehicle place availability was exceeded or place does not exist");
			return new ResponseService(responseStatus, null);
		}
		
		db.addWebvehicle(vehicleTrack.getId(), wv);
		
		ResponseStatus responseStatus = new ResponseStatus(200, "Started tracking vehicle");
		return new ResponseService(responseStatus, vehiclePath);
	}
	
	/**
	 * Generates and returns one of the shortest paths connecting a source place to a destination place
	 * @param source
	 * @param destination
	 * @return
	 */
	private List<String> generatePath(String source, String destination) {
		// create path for vehicle
		Set<List<String>> shortestPaths = null;
		
		try {
			shortestPaths = findShortestPaths(source, destination, 0);
		} catch (UnknownIdException | BadStateException | ServiceException e) {
			logger.log(Level.SEVERE, "Cannot generate shortest path", e);
			// internal server error
			return null;
		}
		
		// select a random path
		List<String> path = getRandomPath(shortestPaths);
		return path;
	}
	
	/**
	 * Get the path for the vehicle identified by id
	 * @param plateId
	 * @param type
	 * @param inGate
	 * @param destination
	 * @return
	 */
	private List<String> getPathVehicle(String id) {
		Webvehicle wv = db.getWebvehicle(id);
		
		if(wv == null)
			return null;
		
		List<String> path = wv.getVehiclePath().getPlace();
		
		return path;
	}

	/**
	 * Get a random path from the set of paths
	 * @param shortestPaths
	 * @return
	 */
	private List<String> getRandomPath(Set<List<String>> shortestPaths) {
		int indexPath = Util.getRandomNumber(shortestPaths.size());
		int i=0;
		
		for(List<String> p: shortestPaths) {
			if(i == indexPath)
				return p;
			i++;
		}
		return new ArrayList<String>();
	}

	/**
	 * Removes a vehicle from the rns system
	 * @param id
	 * @return
	 */
	public synchronized ResponseService deleteVehicle(String id) {
		if(monitor == null) {
			ResponseStatus responseStatus = new ResponseStatus(500, "service not available");
			return new ResponseService(responseStatus, null);
		}
		
		// no input
		if(id == null) {
			ResponseStatus responseStatus = new ResponseStatus(404, "Vehicle does not exist");
			return new ResponseService(responseStatus, null);
		}
				
		Webvehicle wv = db.getWebvehicle(id);
		
		if(wv != null) {
			// update place availability
			// place availability exceeded
			if(db.incrementPlaceCapacity(wv.getVehicle().getPosition()) == null) {
				ResponseStatus responseStatus = new ResponseStatus(404, "cannot remove vehicle because available seats exceeds place capacity or place does not exist");
				return new ResponseService(responseStatus, null);
			}
			db.removeWebvehicle(id);
			
			ResponseStatus responseStatus = new ResponseStatus(204, "DELETE vehicle resource ok");
			return new ResponseService(responseStatus, null);
        }
		else {
			// Vehicle does not exist
			ResponseStatus responseStatus = new ResponseStatus(404, "Vehicle does not exist");
			return new ResponseService(responseStatus, null);
		}
	}	
	
	/**
	 * Untrack a vehicle from the rns system
	 * this method is like moveVehicle(), but instead of returning a path,
	 * I remove vehicle from system
	 * @param id
	 * @param pid
	 * @return
	 */
	public synchronized ResponseService untrackVehicle(String id, String pid) {
		if(monitor == null) {
			ResponseStatus responseStatus = new ResponseStatus(500, "service not available");
			return new ResponseService(responseStatus, null);
		}
		
		// no input
		if(id == null || pid == null) {
			ResponseStatus responseStatus = new ResponseStatus(400, "cannot untrack vehicle because no input was provided");
			return new ResponseService(responseStatus, null);			
		}
		
		if(db.containsWebvehicle(id)) {
			
			if(!db.containsPlaceReader(pid)) {
				ResponseStatus responseStatus = new ResponseStatus(404, "outGate is not the id of a known place");
				return new ResponseService(responseStatus, null);
			}
			
			// pid is not a gate
			if(!(db.getPlaceReader(pid) instanceof GateReader)) {
				ResponseStatus responseStatus = new ResponseStatus(400, "Place exit is not a gate");
				return new ResponseService(responseStatus, null);
			}
			
			// place is a gatereader
			GateReader g = (GateReader) db.getPlaceReader(pid);
			// outGate is not the id of an OUT or INOUT gate
			if(g.getType().equals(GateType.IN)) {
				ResponseStatus responseStatus = new ResponseStatus(400, "outGate is not the id of an OUT or INOUT gate");
				return new ResponseService(responseStatus, null);
			}
			
			Webvehicle wv = db.getWebvehicle(id);
			
			//check if gate is reachable from previous position of the vehicle
			// create a tmp path for the vehicle
			List<String> tmpPath = generatePath(wv.getVehicle().getPosition(), pid);
			
			// server error while computing path
			if(tmpPath == null)	{
				ResponseStatus responseStatus = new ResponseStatus(500, "Service not available");
				return new ResponseService(responseStatus, null);
			}
			//destination gate not reachable from the previous position
			if(tmpPath.size() == 0)	{
				ResponseStatus responseStatus = new ResponseStatus(400, "outGate is not reachable from the previous position of the vehicle");
				return new ResponseService(responseStatus, null);
			}
			
			/** update place availability of last position of vehicle
			 * note: a vehicle can exit even if there are no more seats,
			 * because it is not parked
			 * place availability exceeded
			 */
			if(db.incrementPlaceCapacity(wv.getVehicle().getPosition()) == null) {
				ResponseStatus responseStatus = new ResponseStatus(400, "cannot untrack vehicle because available seats exceeds place capacity");
				return new ResponseService(responseStatus, null);
			}
			
			// exit gate has availability, so remove vehicle from system
        	db.removeWebvehicle(id);
        	
    		ResponseStatus responseStatus = new ResponseStatus(204, "Untracked vehicle");
    		return new ResponseService(responseStatus, null);
        }
		else {
			// Vehicle does not exist
			ResponseStatus responseStatus = new ResponseStatus(404, "Vehicle does not exist");
			return new ResponseService(responseStatus, null);
		}
	}

	/**
	 * Update the state of the vehicle identified by id
	 * @param id
	 * @param vehicleState
	 * @return
	 */
	public synchronized ResponseService changeStateVehicle(String id, VehicleState vehicleState) {
		if(monitor == null) {
			ResponseStatus responseStatus = new ResponseStatus(500, "service not available");
			return new ResponseService(responseStatus, null);
		}
		
		// no input
		if(id == null || vehicleState == null) {
			ResponseStatus responseStatus = new ResponseStatus(400, "cannot change state of vehicle because no input was provided");
			return new ResponseService(responseStatus, null);
		}
		
		// vehicle is not tracked
		if(!db.containsWebvehicle(id)) {
			ResponseStatus responseStatus = new ResponseStatus(404, "Vehicle does not exist");
			return new ResponseService(responseStatus, null);
		}	
		
		Webvehicle wv = db.getWebvehicle(id);
		wv.getVehicle().setState(vehicleState);
				
    	db.updateWebvehicle(id, wv);
		ResponseStatus responseStatus = new ResponseStatus(204, "Vehicle state changed");
		return new ResponseService(responseStatus, null);
	}

	/**
	 * Try to change the position of the vehicle identified by id
	 * @param id
	 * @param vehiclePosition
	 * @return
	 */
	public synchronized ResponseService moveVehicle(String id, VehiclePosition vehiclePosition) {
		if(monitor == null) {
			ResponseStatus responseStatus = new ResponseStatus(500, "service not available");
			return new ResponseService(responseStatus, null);
		}
		
		// no input
		if(vehiclePosition == null || id == null) {
			ResponseStatus responseStatus = new ResponseStatus(400, "cannot move vehicle because no input was provided");
			return new ResponseService(responseStatus, null);
		}
			
		// Vehicle does not exist
		if(!db.containsWebvehicle(id)) {
			ResponseStatus responseStatus = new ResponseStatus(404, "Vehicle does not exist");
			return new ResponseService(responseStatus, null);
		}
		
		// Place does not exist
		if(!db.containsPlaceReader(vehiclePosition.getPlace())) {
			ResponseStatus responseStatus = new ResponseStatus(404, "Place does not exist");
			return new ResponseService(responseStatus, null);
		}
		
		Webvehicle wv = db.getWebvehicle(id);
		String oldPosition = wv.getVehicle().getPosition();
		
		// Vehicle is not IN_TRANSIT
		if(wv.getVehicle().getState().equals(VehicleState.PARKED)) {
			ResponseStatus responseStatus = new ResponseStatus(400, "permission to move is not granted vehicle is not IN TRANSIT");
			return new ResponseService(responseStatus, null);
		}
		
		// vehicle is moving to the same place
		if(oldPosition.equals(vehiclePosition.getPlace())) {
			ResponseStatus responseStatus = new ResponseStatus(403, "permission to move is not granted vehicle is already in that place");
			return new ResponseService(responseStatus, null);
		}
		
		// update place availability of future position
		// place capacity exceeded
		if(db.decrementPlaceCapacity(vehiclePosition.getPlace()) == null) {
			ResponseStatus responseStatus = new ResponseStatus(403, "permission to move is not granted because vehicle place availability was exceeded");
			return new ResponseService(responseStatus, null);
		}
		
		// check if new position is on the path suggested by system for that vehicle
		List<String> path = getPathVehicle(id);
		boolean onPath = false;
		
		for(String p: path) {
			if(p.equals(vehiclePosition.getPlace())) {
				onPath = true;
				wv.getVehicle().setPosition(vehiclePosition.getPlace());
				break;
			}
		}
		
		if(!onPath) {
			
			// check if place reachable from previous current position
			// create a new temporary path
			List<String> tmpPath = generatePath(wv.getVehicle().getPosition(), vehiclePosition.getPlace());
			
			// service cannot compute path
			if(tmpPath == null)	{
				wv.getVehicle().setPosition(oldPosition);
				
				// restore future position capacity
				if(db.incrementPlaceCapacity(vehiclePosition.getPlace()) == null) {
					ResponseStatus responseStatus = new ResponseStatus(403, "permission to move is not granted because vehicle place availability was exceeded");
					return new ResponseService(responseStatus, null);
				}
				
				ResponseStatus responseStatus = new ResponseStatus(500, "service not available");
				return new ResponseService(responseStatus, null);
			}
			// place not reachable from the previous current position
			if(tmpPath.size() == 0)	{
				wv.getVehicle().setPosition(oldPosition);
				
				// restore future position capacity
				if(db.incrementPlaceCapacity(vehiclePosition.getPlace()) == null) {
					ResponseStatus responseStatus = new ResponseStatus(403, "permission to move is not granted because vehicle place availability was exceeded");
					return new ResponseService(responseStatus, null);
				}
				ResponseStatus responseStatus = new ResponseStatus(403, "place not reachable from the previous current position");
				return new ResponseService(responseStatus, null);
			}
			// place reachable from previous current position
			wv.getVehicle().setPosition(vehiclePosition.getPlace());			
		}
		
		// update place availability of previous position
		// place capacity exceeded
		if(db.incrementPlaceCapacity(oldPosition) == null) {
			wv.getVehicle().setPosition(oldPosition);
			
			// restore future position capacity
			if(db.incrementPlaceCapacity(vehiclePosition.getPlace()) == null) {
				ResponseStatus responseStatus = new ResponseStatus(403, "permission to move is not granted because vehicle place availability was exceeded");
				return new ResponseService(responseStatus, null);
			}
			ResponseStatus responseStatus = new ResponseStatus(403, "permission to move is not granted because available seats exceeds place capacity");
			return new ResponseService(responseStatus, null);
		}

		// create a new path for the vehicle
		List<String> newPath = generatePath(wv.getVehicle().getPosition(), wv.getVehicle().getDestination());
		if(newPath == null)	{
			wv.getVehicle().setPosition(oldPosition);
			
			// restore future position capacity
			if(db.incrementPlaceCapacity(vehiclePosition.getPlace()) == null) {
				ResponseStatus responseStatus = new ResponseStatus(403, "permission to move is not granted because vehicle place availability was exceeded");
				return new ResponseService(responseStatus, null);
			}
			// restore previous place capacity
			if(db.decrementPlaceCapacity(oldPosition) == null) {
				ResponseStatus responseStatus = new ResponseStatus(403, "permission to move is not granted because available seats exceeds place capacity");
				return new ResponseService(responseStatus, null);
			}
			ResponseStatus responseStatus = new ResponseStatus(500, "Cannot create neo4j path");
			return new ResponseService(responseStatus, null);
		}		
		
		VehiclePath vp = new VehiclePath();
		vp.getPlace().addAll(newPath);
		wv.setVehiclePath(vp);
				
		db.updateWebvehicle(id, wv);
		ResponseStatus responseStatus = new ResponseStatus(200, "Vehicle moved");
		return new ResponseService(responseStatus, vp);
	}
	
	/**
	 * Gets readers for all the connections available in the RNS system.
	 * @return a set of interfaces for reading all available connections.
	 */
	private Set<ConnectionReader> getConnections() {
		return monitor.getConnections();
	}
	
	/**
	 * Gets readers for all the gates available in the RNS system with the given type
	 * @param - the required gate type or null to get readers for all gates
	 * @return a set of interfaces for reading the selected gates
	 */
	private Set<GateReader> getGates(GateType type) {
		return monitor.getGates(type);
	}
	
	/**
	 * Gets readers for all the parking areas available in the RNS system having the specified services
	 * @param services - the set of services, or null to get all parking areas
	 * @return a set of interfaces for reading the selected parking areas
	 */
	private Set<ParkingAreaReader> getParkingAreas(Set<String> services) {
		return monitor.getParkingAreas(services);
	}
	
	/**
	 * Gets readers for all the places available in the RNS system whose ids have the specified prefix.
	 * @param idPrefix - the id prefix for selecting places or null to get places with all ids.
	 * @return a set of interfaces for reading the selected places.
	 */
	private Set<PlaceReader> getPlaces(String idPrefix) {
		return monitor.getPlaces(idPrefix);
	}
	
	/**
	 * Gets readers for all the road segments available in the RNS system belonging to the road with the given name
	 * @param roadName - the name of the road, or null to get readers for all the road segments of all roads
	 * @return a set of interfaces for reading the selected road segments
	 */
	private Set<RoadSegmentReader> getRoadSegments(String roadName) {
		return monitor.getRoadSegments(roadName);
	}
	
	/**
	 * checks if the database contains the placereader identified by id
	 * @param id
	 * @return
	 */
	private boolean containsPlace(String id) {
		return db.containsPlaceReader(id);
	}
	
	/**
	 * gets the WebPlace object identified by id
	 * @param id
	 * @param baseURI
	 * @return
	 */
	public ResponseService getWebPlace(String id, String baseURI) {
		if(monitor == null) {
			ResponseStatus responseStatus = new ResponseStatus(500, "service not available");
			return new ResponseService(responseStatus, null);
		}
		
		Webplace place = createWebPlace(id, baseURI);
		if(place == null){
			ResponseStatus responseStatus = new ResponseStatus(404, "Place not found");
			return new ResponseService(responseStatus, null);			
		}
		
		ResponseStatus responseStatus = new ResponseStatus(200, "Get place resource ok");
		return new ResponseService(responseStatus, place); 
	}

	/**
	 * gets the Places resource containing all the filtered webplaces
	 * @param baseURI
	 * @param page
	 * @param type
	 * @return
	 */
	public ResponseService getFilteredPlaces(String baseURI, Long page, String type) {
		if(monitor == null) {
			ResponseStatus responseStatus = new ResponseStatus(500, "service not available");
			return new ResponseService(responseStatus, null);
		}
		
		Places places = new Places();
		// webplaces for this page
		Set<Webplace> webPlaces = new TreeSet<>(new WebPlaceComparator());
		Long pageNum = null;
		String placeType = null;
		
		// if page does not exist, use first page
		if(page == null || page < 1)
			pageNum = Long.valueOf(1);
		else
			pageNum = page;
		
		// if type is not gate/parkingarea/roadsegment, then ignore filter
		if(type == null)
			placeType = "all";
		else if(type.equals(PATH_GATE) || type.equals(PATH_PARKING_AREA) 
				|| type.equals(PATH_ROAD_SEGMENT))
			placeType = type;
		else
			placeType = "all";
		
		Collection<Object> allPlaces = db.getMapPlaceReader().values();		
		Set<Object> filteredPlaces = new TreeSet<>(new PlaceComparator());
		
		// filter the place by type
		switch (placeType) {
		case PATH_GATE:
			for(Object e: allPlaces) {
				if(e instanceof GateReader)
					filteredPlaces.add(e);
			}
			break;
		case PATH_PARKING_AREA:
			for(Object e: allPlaces) {
				if(e instanceof ParkingAreaReader)
					filteredPlaces.add(e);
			}
			break;
		case PATH_ROAD_SEGMENT:
			for(Object e: allPlaces) {
				if(e instanceof RoadSegmentReader)
					filteredPlaces.add(e);
			}
			break;
		default:
			// if type does not exist, add all places
			filteredPlaces.addAll(allPlaces);
			break;
		}
		
		// do pagination
		int totalPages = (int) Math.ceil(filteredPlaces.size()/((double)PAGE_SIZE));
		
		if(pageNum <= totalPages) {
			// set next page only if not last page
			if(pageNum != totalPages) {
				URI next = null;
				// type is not gate/parkingarea/roadsegment, then ignore filter
				if(placeType.equals("all"))
					next = UriBuilder.fromUri(baseURI).path(PATH_PLACES).queryParam(PATH_PAGE, pageNum+1).build();
				else
					next = UriBuilder.fromUri(baseURI).path(PATH_PLACES).queryParam(PATH_TYPE, placeType)
								.queryParam(PATH_PAGE, pageNum+1).build();
			
				places.setNext(next.toString());				
			}
		
			// get only places for this page
			int i = 0;
			for(Object e: filteredPlaces) {
				// check interval borders
				if(i >= PAGE_SIZE * (pageNum-1) && i < PAGE_SIZE * pageNum) {
					Webplace wp = createWebPlace(((PlaceReader)e).getId(), baseURI);
					webPlaces.add(wp);
				}
				i++;
			}
			places.getWebplace().addAll(webPlaces);
		}
		
		places.setPage(BigInteger.valueOf(pageNum));
		places.setTotalPages(BigInteger.valueOf(totalPages));
		
		ResponseStatus responseStatus = new ResponseStatus(200, "Get places resource ok");
		return new ResponseService(responseStatus, places);
	}

	/**
	 * Converts a GateReader object to Gate and returns it
	 * @param e
	 * @return
	 */
	private Gate createJAXBGate(GateReader e) {
		Gate g = new Gate();
		g.setCapacity(BigInteger.valueOf(e.getCapacity()));
		g.setId(e.getId());
		
		Set<PlaceReader> nextPlaces = e.getNextPlaces();
		for(PlaceReader npr: nextPlaces) {
			g.getNextPlace().add(npr.getId());
		}
		g.setType(it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.GateType
				.fromValue(e.getType().value()));
		
		return g;
	}

	/**
	 * Converts a ParkingAreaReader object to ParkingArea and returns it
	 * @param e
	 * @return
	 */
	private ParkingArea createJAXBParkingArea(ParkingAreaReader e) {
		ParkingArea pa = new ParkingArea();
		pa.setCapacity(BigInteger.valueOf(e.getCapacity()));
		pa.setId(e.getId());
		
		Set<PlaceReader> nextPlaces = e.getNextPlaces();
		for(PlaceReader npr: nextPlaces) {
			pa.getNextPlace().add(npr.getId());
		}
		pa.getService().addAll(e.getServices());
		
		return pa;
	}

	/**
	 * Converts a RoadSegmentReader object to RoadSegment and returns it
	 * @param e
	 * @return
	 */
	private RoadSegment createJAXBRoadSegment(RoadSegmentReader e) {
		RoadSegment rs = new RoadSegment();
		rs.setCapacity(BigInteger.valueOf(e.getCapacity()));
		rs.setId(e.getId());
		
		Set<PlaceReader> nextPlaces = e.getNextPlaces();
		for(PlaceReader npr: nextPlaces) {
			rs.getNextPlace().add(npr.getId());
		}
		rs.setName(e.getName());
		rs.setRoadName(e.getRoadName());
		
		return rs;
	}

	/**
	 * Get vehicles that are in place identified by id
	 * @param baseURI
	 * @param page 
	 * @param id 
	 * @return
	 */
	public ResponseService getVehiclesInPlace(String baseURI, Long page , String id) {
		if(monitor == null) {
			ResponseStatus responseStatus = new ResponseStatus(500, "service not available");
			return new ResponseService(responseStatus, null);
		}	
		
		// if no place id is specified, then return all vehicles
		if(id == null) {
			Vehicles vs = new Vehicles();
			Long pageNum = null;
			
			// if page does not exist, use first page
			if(page == null || page < 1)
				pageNum = Long.valueOf(1);
			else
				pageNum = page;
			
			Set<Webvehicle> wvs = getWebVehicles();
			
			// do pagination
			int totalPages = (int) Math.ceil(wvs.size()/((double)PAGE_SIZE));

			URI next = null;
			
			// set next page only if not last page
			if(pageNum <= totalPages) {
				next = UriBuilder.fromUri(baseURI)
						.queryParam(PATH_PAGE, pageNum+1).build();
				vs.setNext(next.toString());
			}
			vs.setPage(BigInteger.valueOf(pageNum));
			vs.setTotalPages(BigInteger.valueOf(totalPages));
			vs.getWebvehicle().addAll(wvs);
			ResponseStatus responseStatus = new ResponseStatus(200, "Get vehicles resource ok");
			return new ResponseService(responseStatus, vs);
		}
		
		// check if id is a valid place
		if(!containsPlace(id)) {
			ResponseStatus responseStatus = new ResponseStatus(404, "Place does not exist");
			return new ResponseService(responseStatus, null);
		}
		
		Vehicles vs = new Vehicles();
		Long pageNum = null;
		
		// if page does not exist, use first page
		if(page == null || page < 1)
			pageNum = Long.valueOf(1);
		else
			pageNum = page;
		
		// filter out bad vehicles
		Set<Webvehicle> allWvs = getWebVehicles();
		Set<Webvehicle> filteredWvs = new TreeSet<>(new WebVehicleComparator());
		// webvehicles for this page
		Set<Webvehicle> webVehicles = new TreeSet<>(new WebVehicleComparator());
		
		for(Webvehicle wv: allWvs) {
			if(wv.getVehicle().getPosition().equals(id))
				filteredWvs.add(wv);
		}
				
		// do pagination
		int totalPages = (int) Math.ceil(filteredWvs.size()/((double)PAGE_SIZE));
		
		if(pageNum <= totalPages) {
			// set next page only if not last page
			if(pageNum != totalPages) {
				URI next = UriBuilder.fromUri(baseURI)
						.path(PATH_PLACES).path(id).path(PATH_VEHICLES)
						.queryParam(PATH_PAGE, pageNum+1).build();
				vs.setNext(next.toString());			
			}
		
			// get only vehicles for this page
			int i = 0;
			for(Webvehicle wv: filteredWvs) {
				// check interval borders
				if(i >= PAGE_SIZE * (pageNum-1) && i < PAGE_SIZE * pageNum) {
					webVehicles.add(wv);
				}
				i++;
			}
			vs.getWebvehicle().addAll(webVehicles);
		}	

		vs.setPage(BigInteger.valueOf(pageNum));
		vs.setTotalPages(BigInteger.valueOf(totalPages));
		ResponseStatus responseStatus = new ResponseStatus(200, "Get vehicles in place resource ok");
		return new ResponseService(responseStatus, vs);
	}

	/**
	 * Get all the set of webvehicles that are currently tracked in the system
	 * @return
	 */
	private Set<Webvehicle> getWebVehicles() {
		Set<Webvehicle> wvs = new TreeSet<>(new WebVehicleComparator());
		wvs.addAll(db.getMapWebvehicles().values());
				
		return wvs;
	}
	
	/**
	 * Get vehicles resource container
	 * @return
	 */
	public ResponseService getVehicles(String baseURI, Long page) {
		if(monitor == null) {
			ResponseStatus responseStatus = new ResponseStatus(500, "service not available");
			return new ResponseService(responseStatus, null);
		}
		
		Vehicles vs = new Vehicles();
		Long pageNum = null;
		
		// if page does not exist, use first page
		if(page == null || page < 1)
			pageNum = Long.valueOf(1);
		else
			pageNum = page;
		
		Set<Webvehicle> wvs = getWebVehicles();
		// webvehicles for this page
		Set<Webvehicle> webVehicles = new TreeSet<>(new WebVehicleComparator());
		
		// do pagination
		int totalPages = (int) Math.ceil(wvs.size()/((double)PAGE_SIZE));
		
		// set next page only if not last page
		if(pageNum <= totalPages) {
			// set next page only if not last page
			if(pageNum != totalPages) {
				URI next = UriBuilder.fromUri(baseURI).path(PATH_VEHICLES)
						.queryParam(PATH_PAGE, pageNum+1).build();
				vs.setNext(next.toString());			
			}
			
			// get only vehicles for this page
			int i = 0;
			for(Webvehicle wv: wvs) {
				// check interval borders
				if(i >= PAGE_SIZE * (pageNum-1) && i < PAGE_SIZE * pageNum) {
					webVehicles.add(wv);
				}
				i++;
			}
			vs.getWebvehicle().addAll(webVehicles);
		}
		vs.setPage(BigInteger.valueOf(pageNum));
		vs.setTotalPages(BigInteger.valueOf(totalPages));
		ResponseStatus responseStatus = new ResponseStatus(200, "Get vehicles resource ok");
		return new ResponseService(responseStatus, vs);
	}	

	/**
	 * Get the vehicle identified by id
	 * @param id
	 * @return
	 */
	public ResponseService getWebVehicle(String id) {
		if(monitor == null) {
			ResponseStatus responseStatus = new ResponseStatus(500, "service not available");
			return new ResponseService(responseStatus, null);
		}
		
		// if no id specified or vehicle does not exist, return null
		if(id == null) {
			ResponseStatus responseStatus = new ResponseStatus(404, "Vehicle does not exist");
			return new ResponseService(responseStatus, null);
		}
		Webvehicle wv = db.getWebvehicle(id);
		if(wv == null){
			ResponseStatus responseStatus = new ResponseStatus(404, "Vehicle does not exist");
			return new ResponseService(responseStatus, null);			
		}
		
		ResponseStatus responseStatus = new ResponseStatus(200, "Get rns main resource ok");
		return new ResponseService(responseStatus, wv);
	}
	
	/**
	 * Get connections resource container
	 * @param string
	 * @param page
	 * @return
	 */
	public ResponseService getConnections(String baseURI, Long page) {
		if(monitor == null) {
			ResponseStatus responseStatus = new ResponseStatus(500, "service not available");
			return new ResponseService(responseStatus, null);
		}
		
		Connections connections = new Connections();
		Long pageNum = null;
		
		// if page does not exist, use first page
		if(page == null || page < 1)
			pageNum = Long.valueOf(1);
		else
			pageNum = page;
		
		// convert connections from ConnectionReader to Connection
		Set<ConnectionReader> crs = new TreeSet<>(new ConnectionReaderComparator());
		crs.addAll(getConnections());
		
		// connections for this page
		Set<Connection> cs = new TreeSet<>(new ConnectionComparator());
		
		// do pagination
		int totalPages = (int) Math.ceil(crs.size()/((double)PAGE_SIZE));
		
		// set next page only if not last page
		if(pageNum <= totalPages) {
			// set next page only if not last page
			if(pageNum != totalPages) {
				URI next = UriBuilder.fromUri(baseURI).path(PATH_CONNECTIONS)
						.queryParam(PATH_PAGE, pageNum+1).build();
				connections.setNext(next.toString());			
			}
			
			// get only connections for this page
			int i = 0;
			for(ConnectionReader cr: crs) {
				// check interval borders
				if(i >= PAGE_SIZE * (pageNum-1) && i < PAGE_SIZE * pageNum) {
					Connection c = createConnection(cr.getFrom().getId(), cr.getTo().getId(), baseURI);
					if(c == null) {
						ResponseStatus responseStatus = new ResponseStatus(500, "service not available");
						return new ResponseService(responseStatus, null);
					}
					cs.add(c);
				}
				i++;
			}
			connections.getConnection().addAll(cs);
		}
		connections.setPage(BigInteger.valueOf(pageNum));
		connections.setTotalPages(BigInteger.valueOf(totalPages));
		ResponseStatus responseStatus = new ResponseStatus(200, "Get connections resource ok");
		return new ResponseService(responseStatus, connections);		
	}

	/**
	 * get the main resource containing all the links to the other resources
	 * @param baseURI
	 * @return
	 */
	public ResponseService getRnsSystem(String baseURI) {
		if(monitor == null) {
			ResponseStatus responseStatus = new ResponseStatus(500, "service not available");
			return new ResponseService(responseStatus, null);
		}
		
		Webrns webrns = new Webrns();

    	// set the links for all the available resources
    	URI urlSelf = UriBuilder.fromUri(baseURI).build();
		URI urlPlaces = UriBuilder.fromUri(baseURI).path(PATH_PLACES).build();
		URI urlGates = UriBuilder.fromUri(baseURI).path(PATH_PLACES).queryParam(PATH_TYPE, PATH_GATE).build();
		URI urlRoadSegments = UriBuilder.fromUri(baseURI).path(PATH_PLACES).queryParam(PATH_TYPE, PATH_ROAD_SEGMENT).build();
		URI urlParkingAreas = UriBuilder.fromUri(baseURI).path(PATH_PLACES).queryParam(PATH_TYPE, PATH_PARKING_AREA).build();
		URI urlConnections = UriBuilder.fromUri(baseURI).path(PATH_CONNECTIONS).build();		
		URI urlVehicles = UriBuilder.fromUri(baseURI).path(PATH_VEHICLES).build();
		    	
    	webrns.setSelf(urlSelf.toString());
    	webrns.setPlaces(urlPlaces.toString());
    	webrns.setGates(urlGates.toString());
    	webrns.setRoadSegments(urlRoadSegments.toString());
    	webrns.setParkingAreas(urlParkingAreas.toString());
    	webrns.setConnections(urlConnections.toString());
    	webrns.setVehicles(urlVehicles.toString());
    	
		ResponseStatus responseStatus = new ResponseStatus(200, "Get rns main resource ok");
		return new ResponseService(responseStatus, webrns);
	}

}