package it.polito.dp2.RNS.sol3.admClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import it.polito.dp2.RNS.ConnectionReader;
import it.polito.dp2.RNS.GateReader;
import it.polito.dp2.RNS.GateType;
import it.polito.dp2.RNS.ParkingAreaReader;
import it.polito.dp2.RNS.PlaceReader;
import it.polito.dp2.RNS.RoadSegmentReader;
import it.polito.dp2.RNS.VehicleReader;
import it.polito.dp2.RNS.VehicleState;
import it.polito.dp2.RNS.VehicleType;
import it.polito.dp2.RNS.lab3.AdmClient;
import it.polito.dp2.RNS.lab3.AdmClientException;
import it.polito.dp2.RNS.lab3.ServiceException;
import it.polito.dp2.RNS.sol3.admClient.jaxb.rnssystem.Places;
import it.polito.dp2.RNS.sol3.admClient.jaxb.rnssystem.Places.Webplace;
import it.polito.dp2.RNS.sol3.admClient.jaxb.rnssystem.Vehicles;
import it.polito.dp2.RNS.sol3.admClient.jaxb.rnssystem.Webvehicle;
import it.polito.dp2.RNS.sol3.admClient.model.ConnectionReaderImpl;
import it.polito.dp2.RNS.sol3.admClient.model.GateReaderImpl;
import it.polito.dp2.RNS.sol3.admClient.model.ParkingAreaReaderImpl;
import it.polito.dp2.RNS.sol3.admClient.model.RoadSegmentReaderImpl;
import it.polito.dp2.RNS.sol3.admClient.model.VehicleReaderImpl;
import it.polito.dp2.RNS.sol3.admClient.util.Util;

public class AdmClientImpl implements AdmClient {
	/**
	 * set the verbosity for debug printing
	 * 0 - no debug printing
	 * 1 - inform that vehicle client created
	 * 2 - print all information
	 */
	private int DEBUG_PRINTING_LEVEL = 0;
	private Logger logger;
	private final static String BASE_URL_RNS = "it.polito.dp2.RNS.lab3.URL";
	private final static String BASE_URL_RNS_DEFAULT = "http://localhost:8080/RnsSystem/rest";
	private final static String PATH_PLACES = "places";
	private final static String PATH_VEHICLES = "vehicles";
	private String urlRnsSystem;
	private WebTarget webTarget;
	private Set<GateReader> gates;
	private Set<ParkingAreaReader> parkingAreas;
	private Set<RoadSegmentReader> roadSegments;
	private Map<String, Object> allPlaces;
	private List<Places.Webplace> webPlaces;

	public AdmClientImpl() throws AdmClientException {
		logger = Logger.getLogger(AdmClientImpl.class.getName());
		urlRnsSystem = System.getProperty(BASE_URL_RNS);

		if(urlRnsSystem == null)
			urlRnsSystem = BASE_URL_RNS_DEFAULT;

		webTarget = null;

		// initialize local rns places and connections
		try {
			initializeRns();
		} catch (ServiceException e) {
			logger.log(Level.SEVERE, "Error, cannot load data from service!", e);
			throw new AdmClientException("Error, cannot load data from service!");
		}
		
		// DEBUG printing
		if(DEBUG_PRINTING_LEVEL >= 1) {
			logger.log(Level.INFO, "Admin client created");
		}
	}

	@Override
	public Set<ConnectionReader> getConnections() {
		Set<ConnectionReader> connections = new HashSet<>();
		Set<PlaceReader> nextPlaces = null;		
		
		for (GateReader g: gates) {
			nextPlaces = g.getNextPlaces();
			for (PlaceReader p: nextPlaces)
				connections.add(new ConnectionReaderImpl(g, p));			
		}
		for (ParkingAreaReader pa: parkingAreas) {
			nextPlaces = pa.getNextPlaces();
			for (PlaceReader p: nextPlaces)
				connections.add(new ConnectionReaderImpl(pa, p));			
		}
		for (RoadSegmentReader rs: roadSegments) {
			nextPlaces = rs.getNextPlaces();
			for (PlaceReader p: nextPlaces)
				connections.add(new ConnectionReaderImpl(rs, p));			
		}
		
		return connections;
	}

	@Override
	public Set<GateReader> getGates(GateType type) {
		if(type == null)
			return this.gates;
		
		Set<GateReader> gates = new HashSet<>();
		for (GateReader g: this.gates)
			if(g.getType().name().equals(type.name()))
				gates.add(g);
		return gates;
	}

	@Override
	public Set<ParkingAreaReader> getParkingAreas(Set<String> services) {
		if(services == null)
			return this.parkingAreas;
		
		Set<ParkingAreaReader> parkingAreas = new HashSet<>();
		for (ParkingAreaReader pa: this.parkingAreas) {
			// find if there are common elements in the two sets
			if(!Collections.disjoint(services, pa.getServices()))
				parkingAreas.add(pa);
		}
		return parkingAreas;
	}

	@Override
	public PlaceReader getPlace(String id) {
		for (GateReader g: gates)
			if(g.getId().equals(id))
				return g;
		for (ParkingAreaReader pa: parkingAreas)
			if(pa.getId().equals(id))
				return pa;
		for (RoadSegmentReader rs: roadSegments)
			if(rs.getId().equals(id))
				return rs;
		return null;
	}

	@Override
	public Set<PlaceReader> getPlaces(String idPrefix) {
		Set<PlaceReader> places = new HashSet<>();
		for (GateReader g: gates)
			if((idPrefix == null) || g.getId().contains(idPrefix))
				places.add(g);
		for (ParkingAreaReader pa: parkingAreas)
			if((idPrefix == null) || pa.getId().contains(idPrefix))
				places.add(pa);
		for (RoadSegmentReader rs: roadSegments)
			if((idPrefix == null) || rs.getId().contains(idPrefix))
				places.add(rs);
		return places;
	}

	@Override
	public Set<RoadSegmentReader> getRoadSegments(String roadName) {
		if(roadName == null)
			return this.roadSegments;
			
		Set<RoadSegmentReader> roadSegments = new HashSet<>();
		for (RoadSegmentReader rs: this.roadSegments)
			if(rs.getRoadName().equals(roadName))
				roadSegments.add(rs);
		return roadSegments;
	}

	@Override
	public VehicleReader getVehicle(String id) {
		//when getVehicle inherited from RnsReader is called, return null
		return null;
	}

	@Override
	public Set<VehicleReader> getVehicles(Calendar since, Set<VehicleType> type, VehicleState state) {
		//when getVehicles inherited from RnsReader is called, return null
		return null;
	}

	@Override
	public Set<VehicleReader> getUpdatedVehicles(String place) throws ServiceException {
		Set<VehicleReader> vehicles = new HashSet<>();
		boolean loadedWebPlaces = false;
		String urlPage = null;

		if(place == null) {
			urlPage = UriBuilder.fromUri(urlRnsSystem)
					.path(PATH_VEHICLES).build().toString();
		}
		else {
			urlPage = UriBuilder.fromUri(urlRnsSystem)
					.path(PATH_PLACES).path(place).path(PATH_VEHICLES)
					.build().toString();
		}
		
		Response responseVehicles = null;
		
		/**
		 *  get all vehicles from service
		 *  loop until all pages loaded
		 */
		do {			
			// get vehicles from response
			Vehicles jaxbVehicles = null;

			// create web target for the page URL and a client 
			Client client = createClientAndWebTarget(urlPage);

			try {
				responseVehicles = webTarget.request(MediaType.APPLICATION_XML).get();
			} catch (RuntimeException e) {
				logger.log(Level.SEVERE, "Error, cannot get vehicles entity", e);
				throw new ServiceException("Error, cannot get vehicles entity");
			}
			
			if(responseVehicles == null)
				throw new ServiceException("Error, service not available");			
			
			if(responseVehicles.getStatus()>=400)
				throw new ServiceException("Error, wrong response for vehicles GET");

			try {
				//Note: required if I want to perform more than one readEntity() operation
				responseVehicles.bufferEntity();
	
				// DEBUG printing
				if(DEBUG_PRINTING_LEVEL == 2) {
					logger.log(Level.INFO, responseVehicles.readEntity(String.class));
				}	
				// validate result
				jaxbVehicles = responseVehicles.readEntity(Vehicles.class);
			} catch (ProcessingException e) {
				logger.log(Level.SEVERE, "Error, cannot read vehicles entity", e);
				throw new ServiceException("Error, cannot read vehicles entity");
			}
			
			// check if last page 
			if(jaxbVehicles.getNext() == null) {
				loadedWebPlaces = true;
			}
			else {
				urlPage = jaxbVehicles.getNext();
			}

			List<Vehicles.Webvehicle> pageWebVehicles = jaxbVehicles.getWebvehicle();

			// now convert to VehicleReader
			for(Vehicles.Webvehicle wv: pageWebVehicles) {
				String vid = wv.getVehicle().getId();
				Calendar entryTime = Util.covertXMLGregorianCalendarToCalendar(wv.getVehicle().getEntryTime());
				VehicleType type = VehicleType.fromValue(wv.getVehicle().getType().value());
				PlaceReader destination = getPlace(wv.getVehicle().getDestination()) ;
				PlaceReader origin = getPlace(wv.getVehicle().getOrigin());
				PlaceReader position = getPlace(wv.getVehicle().getPosition());
				VehicleState state = VehicleState.fromValue(wv.getVehicle().getState().value());
				VehicleReader e = new VehicleReaderImpl(vid, entryTime, type, destination, origin, position, state);
				vehicles.add(e);
			}

			// clear memory
			responseVehicles.close();
			client.close();
		} while(!loadedWebPlaces);
		
		return vehicles;
	}

	@Override
	public VehicleReader getUpdatedVehicle(String id) throws ServiceException {
		// create web target for the base URL and a client 
		Client client = createClientAndWebTarget(urlRnsSystem);

		// get vehicle
		Webvehicle jaxbVehicle = null;

		Response responseVehicle = null;

		try {
		responseVehicle = webTarget
				.path(PATH_VEHICLES).path(id).request(MediaType.APPLICATION_XML)
				.get();
		} catch (RuntimeException e) {
			logger.log(Level.SEVERE, "Error, cannot get vehicle entity", e);
			throw new ServiceException("Error, cannot get vehicle entity");
		}
		
		if(responseVehicle == null)
			throw new ServiceException("Error, service not available");
		
		// check that the response has a code 404 
		if(responseVehicle.getStatus() == 404)
			return null;

		if(responseVehicle.getStatus() != 404 && responseVehicle.getStatus()>=400)
			throw new ServiceException("Error, wrong response for vehicle GET");

		try {
			//Note: required if I want to perform more than one readEntity() operation
			responseVehicle.bufferEntity();
	
			// DEBUG printing
			if(DEBUG_PRINTING_LEVEL == 2) {
				logger.log(Level.INFO, responseVehicle.readEntity(String.class));
			}	
			
			// validate result
			jaxbVehicle = responseVehicle.readEntity(Webvehicle.class);
		} catch (ProcessingException e) {
			logger.log(Level.SEVERE, "Error, cannot read vehicle entity", e);
			throw new ServiceException("Error, cannot read vehicle entity");
		}
		// now convert to VehicleReader
		String vid = jaxbVehicle.getVehicle().getId();
		Calendar entryTime = Util.covertXMLGregorianCalendarToCalendar(jaxbVehicle.getVehicle().getEntryTime());
		VehicleType type = VehicleType.fromValue(jaxbVehicle.getVehicle().getType().value());
		PlaceReader destination = getPlace(jaxbVehicle.getVehicle().getDestination()) ;
		PlaceReader origin = getPlace(jaxbVehicle.getVehicle().getOrigin());
		PlaceReader position = getPlace(jaxbVehicle.getVehicle().getPosition());
		VehicleState state = VehicleState.fromValue(jaxbVehicle.getVehicle().getState().value());
		VehicleReader e = new VehicleReaderImpl(vid, entryTime, type, destination, origin, position, state);

		// clear memory
		responseVehicle.close();
		client.close();

		return e;
	}

	/**
	 * Initializes the local rns
	 * get from the service the information about places and their connections
	 * @throws ServiceException 
	 */
	private void initializeRns() throws ServiceException {
		// temporary data structures
		// only used for computing next place connections
		allPlaces = new HashMap<>();
		webPlaces = new ArrayList<>();

		initializeWebPlaces();
		initializeConnectionsPlaces();

		// move data to the individual type of place set from allPlace map
		for(Object e: allPlaces.values()) {
			if(e instanceof GateReader)
				gates.add((GateReader) e);
			else if(e instanceof RoadSegmentReader)
				roadSegments.add((RoadSegmentReader) e);
			else if(e instanceof ParkingAreaReader)
				parkingAreas.add((ParkingAreaReader) e);
		}

		// DEBUG printing
		if(DEBUG_PRINTING_LEVEL == 2) {
			//Debug.printAll(gates, parkingAreas, roadSegments);
		}

		// clear memory
		webPlaces.clear();
	}

	/**
	 * Get from the rns service all the places and convert them to PlaceReader
	 * @throws ServiceException
	 */
	private void initializeWebPlaces() throws ServiceException {
		gates = new HashSet<>();
		parkingAreas = new HashSet<>();
		roadSegments = new HashSet<>();
		boolean loadedWebPlaces = false;
		String urlPage = null;

		urlPage = UriBuilder.fromUri(urlRnsSystem)
					.path(PATH_PLACES).build().toString();
		
		/**
		 *  get all webplaces from service
		 *  loop until all pages loaded
		 */
		do {
			// get Places from response
			Places jaxbPlaces = null;
			Response responsePlaces = null;
			// create web target for the page URL and a client 
			Client client = createClientAndWebTarget(urlPage);

			try {
				responsePlaces = webTarget.request(MediaType.APPLICATION_XML).get();
			} catch (RuntimeException e) {
				logger.log(Level.SEVERE, "Error, cannot get places entity", e);
				throw new ServiceException("Error, cannot get places entity");
			}
			
			if(responsePlaces == null)
				throw new ServiceException("Error, service not available");				
			
			if(responsePlaces.getStatus()>=400)
				throw new ServiceException("Error, wrong response for places GET");

			try {
				//Note: required if I want to perform more than one readEntity() operation
				responsePlaces.bufferEntity();
	
				// DEBUG printing
				if(DEBUG_PRINTING_LEVEL == 2) {
					logger.log(Level.INFO, responsePlaces.readEntity(String.class));
				}	
				
				// validate result
				jaxbPlaces = responsePlaces.readEntity(Places.class);
			} catch (ProcessingException e) {
				logger.log(Level.SEVERE, "Error, cannot read places entity", e);
				throw new ServiceException("Error, cannot read places entity");
			}
			
			// check if last page 
			if(jaxbPlaces.getNext() == null) {
				loadedWebPlaces = true;
			}
			else {
				urlPage = jaxbPlaces.getNext();
			}

			List<Webplace> pageWebPlaces = jaxbPlaces.getWebplace();
			webPlaces.addAll(pageWebPlaces);

			// now convert to GateReader or ParkingAreaReader or RoadSegmentReader
			for(Webplace wp: pageWebPlaces) {

				if(wp.getGate() != null) {
					String id = wp.getGate().getId();
					int capacity = wp.getGate().getCapacity().intValue();
					GateType type =  GateType.fromValue(wp.getGate().getType().value());
					GateReader g = new GateReaderImpl(id, capacity, type);
					allPlaces.put(id, g);
				}
				else if(wp.getRoadSegment() != null) {
					String id = wp.getRoadSegment().getId();
					int capacity = wp.getRoadSegment().getCapacity().intValue();			
					String name = wp.getRoadSegment().getName();
					String roadName = wp.getRoadSegment().getRoadName();
					RoadSegmentReader rs = new RoadSegmentReaderImpl(id, capacity, name, roadName);
					allPlaces.put(id, rs);
				}
				else if(wp.getParkingArea() != null) {
					String id = wp.getParkingArea().getId();
					int capacity = wp.getParkingArea().getCapacity().intValue();
					ParkingAreaReader pa = new ParkingAreaReaderImpl(id, capacity);
					pa.getServices().addAll(wp.getParkingArea().getService());
					allPlaces.put(id, pa);
				}
			}

			// clear memory
			responsePlaces.close();
			client.close();
		} while(!loadedWebPlaces);		
	}

	/**
	 * Add connections for places
	 */
	private void initializeConnectionsPlaces() {

		// also add the next places connections
		for (Webplace wp: webPlaces) {
			// connections for gates
			if(wp.getGate() != null) {
				String id = wp.getGate().getId();
				GateReader g = (GateReader) allPlaces.get(id);
				List<String> nextPlaces = wp.getGate().getNextPlace();
				for(String p: nextPlaces) {
					g.getNextPlaces().add((PlaceReader) allPlaces.get(p));
				}
				allPlaces.put(id, g);
			}
			// connections for RoadSegments
			else if(wp.getRoadSegment() != null) {
				String id = wp.getRoadSegment().getId();
				RoadSegmentReaderImpl rs = (RoadSegmentReaderImpl) allPlaces.get(id);
				List<String> nextPlaces = wp.getRoadSegment().getNextPlace();
				for(String p: nextPlaces) {
					rs.getNextPlaces().add((PlaceReader) allPlaces.get(p));
				}
				allPlaces.put(id, rs);
			}
			// connections for ParkingAreas
			else if(wp.getParkingArea() != null) {
				String id = wp.getParkingArea().getId();
				ParkingAreaReaderImpl pa = (ParkingAreaReaderImpl) allPlaces.get(id);
				List<String> nextPlaces = wp.getParkingArea().getNextPlace();
				for(String p: nextPlaces) {
					pa.getNextPlaces().add((PlaceReader) allPlaces.get(p));
				}
				allPlaces.put(id, pa);
			}
		}		
	}

	/**
	 * Creates a new JAX-RS client
	 * and initializes the JAX-RS WebTarget with the BASE_URL
	 * @param url
	 * @return
	 */
	private Client createClientAndWebTarget(String url) {
		Client client = ClientBuilder.newClient();
		webTarget = client.target(url);	

		return client;
	}
	
}
