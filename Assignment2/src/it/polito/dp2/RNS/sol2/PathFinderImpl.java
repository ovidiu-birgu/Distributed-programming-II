package it.polito.dp2.RNS.sol2;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import it.polito.dp2.RNS.FactoryConfigurationError;
import it.polito.dp2.RNS.PlaceReader;
import it.polito.dp2.RNS.RnsReader;
import it.polito.dp2.RNS.RnsReaderException;
import it.polito.dp2.RNS.RnsReaderFactory;
import it.polito.dp2.RNS.lab2.BadStateException;
import it.polito.dp2.RNS.lab2.ModelException;
import it.polito.dp2.RNS.lab2.PathFinder;
import it.polito.dp2.RNS.lab2.PathFinderException;
import it.polito.dp2.RNS.lab2.ServiceException;
import it.polito.dp2.RNS.lab2.UnknownIdException;
import it.polito.dp2.RNS.sol2.jaxb.Neo4JNode;
import it.polito.dp2.RNS.sol2.jaxb.Neo4JNodeResponse;
import it.polito.dp2.RNS.sol2.jaxb.Neo4JPath;
import it.polito.dp2.RNS.sol2.jaxb.Neo4JPath.Relationships;
import it.polito.dp2.RNS.sol2.jaxb.Neo4JPathResponse;
import it.polito.dp2.RNS.sol2.jaxb.Neo4JRelationship;
import it.polito.dp2.RNS.sol2.util.Util;

public class PathFinderImpl implements PathFinder{
	/**
	 * set the verbosity for debug printing
	 * 0 - no debug printing
	 * 1 - print only the list of shortest paths
	 * 2 - print all information
	 */
	private int DEBUG_PRINTING_LEVEL = 0;
	private final String BASE_URL = "it.polito.dp2.RNS.lab2.URL";
	private final String SERVICE_ROOT = "data";
	private final String NODE_ROOT = "node";
	private final String NODE_RELATIONSHIP = "relationship";
	private final String NODE_RELATIONSHIPS = "relationships";
	private final String NODE_PATHS = "paths";
	private final String RELATIONSHIP_TYPE = "ConnectedTo";
	private final String RELATIONSHIP_DIRECTION = "out";
	private final String PATH_ALGORITHM = "shortestPath";
	private String baseURL;
	private boolean modelLoaded;
	private WebTarget webTarget;
	private RnsReader monitor;
	private Map<String, Integer> mapNodes;
	private Set<Integer> setRelationships;

	public PathFinderImpl() throws PathFinderException {
		super();
		modelLoaded = false;

		mapNodes = new HashMap<>();
		setRelationships = new HashSet<>();
		webTarget = null;

		baseURL = System.getProperty(BASE_URL);
		if(baseURL == null)
			throw new PathFinderException("Error, invalid base url!");
	}	

	@Override
	public boolean isModelLoaded() {
		return modelLoaded;
	}

	@Override
	public void reloadModel() throws ServiceException, ModelException {

		try {
			// create new rns system
			monitor = RnsReaderFactory.newInstance().newRnsReader();
			if(monitor == null)
				throw new ModelException("Error, cannot instantiate RnsReader!");
		} catch (RnsReaderException | FactoryConfigurationError e) {
			e.printStackTrace();
			throw new ModelException("Error, cannot instantiate PathFinder!");
		}

		// create web target for the base URL and a client 
		Client client = createClientAndWebTarget();

		if(isModelLoaded()) {
			// reset flag
			modelLoaded = false;
			// delete old model
			deleteNeo4jGraph();
		}

		Set<PlaceReader> places = monitor.getPlaces(null);

		// create a graph node for each place
		for(PlaceReader place: places) {
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

	@Override
	public Set<List<String>> findShortestPaths(String source, String destination, int maxlength)
			throws UnknownIdException, BadStateException, ServiceException {

		// flags used to see if source and destination are present in the set of places
		boolean validSource = false, validDestination = false;
		int maxDepth;
		// check that model was loaded
		if(!isModelLoaded())
			throw new BadStateException("Error, the model is not loaded!");

		Set<PlaceReader> places = monitor.getPlaces(null);

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
		Client client = createClientAndWebTarget();

		Set<List<String>> paths = new HashSet<>();

		// get the list of paths from neo4j
		Neo4JPath neo4jPath = new Neo4JPath();

		Relationships neo4jRelationships = new Relationships();
		neo4jRelationships.setType(RELATIONSHIP_TYPE);
		neo4jRelationships.setDirection(RELATIONSHIP_DIRECTION);

		// link to destination node for search
		URI to = UriBuilder.fromUri(baseURL)
				.path(SERVICE_ROOT)
				.path(NODE_ROOT)
				.path(mapNodes.get(destination).toString())
				.build();
		neo4jPath.setTo(to.toString());
		neo4jPath.setMaxDepth(BigInteger.valueOf(maxDepth));
		neo4jPath.setRelationships(neo4jRelationships);
		neo4jPath.setAlgorithm(PATH_ALGORITHM);
		Response response = null;
		try {
			response = webTarget.path(NODE_ROOT)
					.path(mapNodes.get(source).toString())
					.path(NODE_PATHS)
					.request(MediaType.APPLICATION_JSON)
					.post(Entity.entity(neo4jPath, MediaType.APPLICATION_JSON));	
		} catch (RuntimeException e) {
			e.printStackTrace();
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
				System.out.println(response.readEntity(String.class));
			}
	
			// validate result
			// get the list of paths from POST response
			// Note: I have to wrap inside GenericType<T> instead of using Class<T> 
			// because Java compiler erases parameterized type information
			neo4jPathResponses = response.readEntity(new GenericType<List<Neo4JPathResponse>>(){});				
		} catch (ProcessingException e) {
			e.printStackTrace();
			throw new ServiceException("Error, cannot read graph path entity");
		}
		
		// convert the paths to the correct format: Set<List<String>>
		for(Neo4JPathResponse neo4jPathResponse: neo4jPathResponses) {
			List<String> path = new ArrayList<>();
			List<String> responseNodes = neo4jPathResponse.getNodes();
			for(String p: responseNodes) {
				String e = Util.getKeyByValue(mapNodes, parseStringToInteger(getLastParamURL(p)));
				if(e == null)
					throw new ServiceException("Error, cannot find place corresponding to node");
				path.add(e);
			}

			paths.add(path);
		}

		// DEBUG printing
		if(DEBUG_PRINTING_LEVEL > 0) {
			for(List<String> p: paths) {
				System.out.print("Path from "+source+" to "+destination+": ");
				for(String s: p)
					System.out.print(s + " ");
				System.out.println();
			}
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
	private Client createClientAndWebTarget() {
		Client client = ClientBuilder.newClient();
		webTarget = client.target(baseURL).path(SERVICE_ROOT);	

		return client;
	}

	/**
	 * Creates a new Neo4j graph node from a place
	 * @throws ServiceException 
	 */
	private void createGraphNode(PlaceReader place) throws ServiceException {
		// create node if it does not exist
		if(!mapNodes.containsKey(place.getId())) {
			Neo4JNode neo4jNode = new Neo4JNode();
			neo4jNode.setId(place.getId());

			Response responseNode = null;
			try {
				responseNode = webTarget.path(NODE_ROOT).request(MediaType.APPLICATION_JSON)
						.post(Entity.entity(neo4jNode, MediaType.APPLICATION_JSON));	
			} catch (RuntimeException e) {
				e.printStackTrace();
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
					System.out.println(responseNode.readEntity(String.class));
				}	
	
				// validate result
				neo4jNodeResponse = responseNode.readEntity(Neo4JNodeResponse.class);				
			} catch (ProcessingException e) {
				e.printStackTrace();
				throw new ServiceException("Error, cannot read graph node entity");
			}

			// save the association [place-id, neo4jNode-id]
			mapNodes.put(place.getId(), parseStringToInteger(getLastParamURL(neo4jNodeResponse.getSelf())));

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

		URI to = UriBuilder.fromUri(baseURL)
				.path(SERVICE_ROOT)
				.path(NODE_ROOT)
				.path(mapNodes.get(placeTo.getId()).toString())
				.build();
		neo4jRelationship.setTo(to.toString());
		neo4jRelationship.setType(RELATIONSHIP_TYPE);

		Response responseRelationship = null;
		try {
			responseRelationship = webTarget.path(NODE_ROOT)
					.path(mapNodes.get(placeFrom.getId()).toString())
					.path(NODE_RELATIONSHIPS)
					.request(MediaType.APPLICATION_JSON)
					.post(Entity.entity(neo4jRelationship, MediaType.APPLICATION_JSON));	
		} catch (RuntimeException e) {
			e.printStackTrace();
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
				System.out.println(responseRelationship.readEntity(String.class));
			}
			
			// validate result
			neo4jRelationshipResponse = responseRelationship.readEntity(Neo4JNodeResponse.class);				
		} catch (ProcessingException e) {
			throw new ServiceException("Error, cannot read graph relationship entity");
		}		

		// save the association [place-id, neo4jNode-id]
		setRelationships.add(parseStringToInteger(getLastParamURL(neo4jRelationshipResponse.getSelf())));

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
		for(Integer r: setRelationships) {
			Response responseRelationship = null;
			try {
				responseRelationship = webTarget.path(NODE_RELATIONSHIP)
						.path(r.toString())
						.request(MediaType.APPLICATION_JSON)
						.delete();	
			} catch (RuntimeException e) {
				e.printStackTrace();
				throw new ServiceException("Error, cannot delete graph relationship entity");
			}

			// check that the response has a code 200 
			if(!isValidResponse(responseRelationship))
				throw new ServiceException("Error, wrong response for relationship DELETE");

			// clear memory
			responseRelationship.close();			

		}
		setRelationships.clear();

		// delete graph nodes
		for(Entry<String, Integer> n: mapNodes.entrySet()) {
			Response responseNode = null;
			try {
				responseNode = webTarget.path(NODE_ROOT)
						.path(n.getValue().toString())
						.request(MediaType.APPLICATION_JSON)
						.delete();	
			} catch (RuntimeException e) {
				e.printStackTrace();
				throw new ServiceException("Error, cannot delete graph node entity");
			}

			// check that the response has a code 200 
			if(!isValidResponse(responseNode))
				throw new ServiceException("Error, wrong response for node DELETE");

			// clear memory
			responseNode.close();			
		}
		mapNodes.clear();
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
			e.printStackTrace();
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
			e.printStackTrace();
			throw new ServiceException("Error, cannot parse string to integer!");		
		}		

		return integer;
	}
}
