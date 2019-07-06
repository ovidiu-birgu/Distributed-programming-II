package it.polito.dp2.RNS.sol3.vehClient;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import it.polito.dp2.RNS.VehicleState;
import it.polito.dp2.RNS.VehicleType;
import it.polito.dp2.RNS.lab3.EntranceRefusedException;
import it.polito.dp2.RNS.lab3.ServiceException;
import it.polito.dp2.RNS.lab3.UnknownPlaceException;
import it.polito.dp2.RNS.lab3.VehClient;
import it.polito.dp2.RNS.lab3.WrongPlaceException;
import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.VehState;
import it.polito.dp2.RNS.sol3.vehClient.jaxb.rnssystem.VehiclePath;
import it.polito.dp2.RNS.sol3.vehClient.jaxb.rnssystem.VehiclePosition;
import it.polito.dp2.RNS.sol3.vehClient.jaxb.rnssystem.VehicleTrack;

public class VehClientImpl implements VehClient {
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
	private final static String PATH_VEHICLES = "vehicles";
	private final static String PATH_MOVE = "move";
	private static final String PATH_STATE = "state";
	private static final String PATH_EXIT = "exit";
	private String urlRnsSystem;
	private WebTarget webTarget;
	private List<String> path;
	//vehicle information
	private String plateId;
	private VehicleType type;
	private String inGate;
	private String destination;
	
	public VehClientImpl() {
		logger = Logger.getLogger(VehClientImpl.class.getName());
		urlRnsSystem = System.getProperty(BASE_URL_RNS);
		
		if(urlRnsSystem == null)
			urlRnsSystem = BASE_URL_RNS_DEFAULT;
		
		webTarget = null;
		
		// DEBUG printing
		if(DEBUG_PRINTING_LEVEL >= 1) {
			logger.log(Level.INFO, "Vehicle client created");
		}	
	}
	
	@Override
	public List<String> enter(String plateId, VehicleType type, String inGate, String destination)
			throws ServiceException, UnknownPlaceException, WrongPlaceException, EntranceRefusedException {
		
		this.plateId = plateId;
		this.type = type;
		this.inGate = inGate;
		this.destination = destination;
		
		// create web target for the base URL and a client 
		Client client = createClientAndWebTarget();
		
		// enter
		VehiclePath jaxbVehiclePath = null;

		Response response = null;
		
		VehicleTrack vehicleTrack = new VehicleTrack();
		vehicleTrack.setDestination(destination);
		vehicleTrack.setOrigin(inGate);
		vehicleTrack.setId(plateId);
		vehicleTrack.setType(it.polito.dp2.RNS.sol3.vehClient.jaxb.rnssystem.VehicleType.fromValue(type.value()));
		
		try {
			response = webTarget
					.path(PATH_VEHICLES).request(MediaType.APPLICATION_XML)
					.post(Entity.entity(vehicleTrack, MediaType.APPLICATION_XML));
		} catch (RuntimeException e) {
			logger.log(Level.SEVERE, "Error, cannot post vehicle entity", e);
			throw new ServiceException("Error, cannot post vehicle entity");
		}
		
		if(response == null)
			throw new ServiceException("Error, service not available");
		
		// check that the response codes
		if(response.getStatus() == 400)
			throw new WrongPlaceException("Error, wrong place");
		
		if(response.getStatus() == 403)
			throw new EntranceRefusedException("Error, vehicle refused entry");
		
		if(response.getStatus() == 404)
			throw new UnknownPlaceException("Error, wrong inGate or destination");

		if(response.getStatus() >= 500)
			throw new WrongPlaceException("Error, service not available");
		
		try {
			//Note: required if I want to perform more than one readEntity() operation
			response.bufferEntity();
	
			// DEBUG printing
			if(DEBUG_PRINTING_LEVEL == 2) {
				logger.log(Level.INFO, response.readEntity(String.class));
			}
	
			// validate result
			jaxbVehiclePath = response.readEntity(VehiclePath.class);
		} catch (ProcessingException e) {
			logger.log(Level.SEVERE, "Error, cannot read vehicle path entity", e);
			throw new ServiceException("Error, cannot read vehicle path entity");
		}
		
		// now convert to List<String>
		path = jaxbVehiclePath.getPlace();
		
		// clear memory
		response.close();
		client.close();
		
		return path;
	}

	@Override
	public List<String> move(String newPlace) throws ServiceException, UnknownPlaceException, WrongPlaceException {
		
		// create web target for the base URL and a client 
		Client client = createClientAndWebTarget();
				
		// move
		VehiclePath jaxbVehiclePath = null;

		VehiclePosition vp = new VehiclePosition();
		vp.setPlace(newPlace);
		
		Response response = null;
		try {
			response = webTarget
					.path(PATH_VEHICLES).path(plateId)
					.path(PATH_MOVE).request(MediaType.APPLICATION_XML)
					.put(Entity.entity(vp, MediaType.APPLICATION_XML));
		} catch (RuntimeException e) {
			logger.log(Level.SEVERE, "Error, cannot put vehicle entity", e);
			throw new ServiceException("Error, cannot put vehicle entity");
		}
		
		if(response == null)
			throw new ServiceException("Error, service not available");
		
		// check that the response codes
		if(response.getStatus() == 400)
			throw new WrongPlaceException("Error, wrong newPlace");
		
		if(response.getStatus() == 404)
			throw new UnknownPlaceException("Error, newPlace does not exist");

		if(response.getStatus() >= 500)
			throw new WrongPlaceException("Error, service not available");
			
		try {
			//Note: required if I want to perform more than one readEntity() operation
			response.bufferEntity();
	
			// DEBUG printing
			if(DEBUG_PRINTING_LEVEL == 2) {
				logger.log(Level.INFO, response.readEntity(String.class));
			}	
	
			// validate result
			jaxbVehiclePath = response.readEntity(VehiclePath.class);
		} catch (ProcessingException e) {
			logger.log(Level.SEVERE, "Error, cannot read vehicle path entity", e);
			throw new ServiceException("Error, cannot read vehicle path entity");
		}
		
		// now convert to List<String>
		List<String> newPath = jaxbVehiclePath.getPlace();

		// if path has not changed, return null
		if(!pathChanged(newPath))
			return null;
		else 
			path = newPath;
		
		// clear memory
		response.close();
		client.close();
		
		return path;
	}

	private boolean pathChanged(List<String> newPath) {
		// check if new path has the same elements in the same order
		return path.equals(newPath);
	}

	@Override
	public void changeState(VehicleState newState) throws ServiceException {
		
		// create web target for the base URL and a client 
		Client client = createClientAndWebTarget();
				
		// change state
		VehState vehState = new VehState();
		it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.VehicleState state = 
				it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.VehicleState.fromValue(newState.value());
		vehState.setState(state );
		
		Response response = null;

		try {
			response = webTarget
					.path(PATH_VEHICLES).path(plateId)
					.path(PATH_STATE).request(MediaType.APPLICATION_XML)
					.put(Entity.entity(vehState, MediaType.APPLICATION_XML));
		} catch (RuntimeException e) {
			logger.log(Level.SEVERE, "Error, cannot put vehicle state entity", e);
			throw new ServiceException("Error, cannot put vehicle state entity");
		}
		
		if(response == null)
			throw new ServiceException("Error, service not available");
		
		// check that the response codes
		if(response.getStatus() == 400)
			throw new ServiceException("Error, wrong place");
		
		if(response.getStatus() == 404)
			throw new ServiceException("Error, vehicle does not exist");

		if(response.getStatus() >= 500)
			throw new ServiceException("Error, service not available");		
		
		// clear memory
		response.close();
		client.close();
	}

	@Override
	public void exit(String outGate) throws ServiceException, UnknownPlaceException, WrongPlaceException {
		
		// create web target for the base URL and a client 
		Client client = createClientAndWebTarget();
				
		// exit
		Response response = null;
		
		VehiclePosition vp = new VehiclePosition();
		vp.setPlace(outGate);
		
		try {
			response = webTarget
					.path(PATH_VEHICLES).path(plateId).path(PATH_EXIT)
					.request(MediaType.APPLICATION_XML)
					.put(Entity.entity(vp, MediaType.APPLICATION_XML));
		} catch (RuntimeException e) {
			logger.log(Level.SEVERE, "Error, cannot put vehicle exit entity", e);
			throw new ServiceException("Error, cannot put vehicle exit entity");
		}
			
		if(response == null)
			throw new ServiceException("Error, service not available");
		
		// check that the response codes
		if(response.getStatus() == 400)
			throw new WrongPlaceException("Error, wrong place");
		
		if(response.getStatus() == 404)
			throw new UnknownPlaceException("Error, vehicle does not exist");

		if(response.getStatus() >= 500)
			throw new ServiceException("Error, service not available");
		
		// clear memory
		response.close();
		client.close();
	}
	
	/**
	 * Creates a new JAX-RS client
	 * and initializes the JAX-RS WebTarget with the BASE_URL
	 * @return
	 */
	private Client createClientAndWebTarget() {
		Client client = ClientBuilder.newClient();
		webTarget = client.target(urlRnsSystem);	

		return client;
	}
	
}
