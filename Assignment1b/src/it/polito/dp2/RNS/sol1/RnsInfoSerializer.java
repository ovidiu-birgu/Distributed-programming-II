package it.polito.dp2.RNS.sol1;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import it.polito.dp2.RNS.GateReader;
import it.polito.dp2.RNS.ParkingAreaReader;
import it.polito.dp2.RNS.PlaceReader;
import it.polito.dp2.RNS.RnsReader;
import it.polito.dp2.RNS.RnsReaderException;
import it.polito.dp2.RNS.RnsReaderFactory;
import it.polito.dp2.RNS.RoadSegmentReader;
import it.polito.dp2.RNS.VehicleReader;
import it.polito.dp2.RNS.sol1.jaxb.Gate;
import it.polito.dp2.RNS.sol1.jaxb.GateType;
import it.polito.dp2.RNS.sol1.jaxb.ParkingArea;
import it.polito.dp2.RNS.sol1.jaxb.Rns;
import it.polito.dp2.RNS.sol1.jaxb.Road;
import it.polito.dp2.RNS.sol1.jaxb.RoadSegment;
import it.polito.dp2.RNS.sol1.jaxb.Service;
import it.polito.dp2.RNS.sol1.jaxb.Vehicle;
import it.polito.dp2.RNS.sol1.jaxb.VehicleState;
import it.polito.dp2.RNS.sol1.jaxb.VehicleType;
import it.polito.dp2.RNS.sol1.util.Util;

public class RnsInfoSerializer {
	private RnsReader monitor;
	private DateFormat dateFormat;
	private final String PACKAGE_JAXB_SOL1 = "it.polito.dp2.RNS.sol1.jaxb";
	private final String SCHEMA_FILE = "xsd/rnsInfo.xsd";
	
	/**
	 * Default constructor
	 * @throws RnsReaderException
	 */
	public RnsInfoSerializer() throws RnsReaderException {
		RnsReaderFactory factory = RnsReaderFactory.newInstance();
		monitor = factory.newRnsReader();
		dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
	}
	
	public RnsInfoSerializer(RnsReader monitor) {
		super();
		this.monitor = monitor;
		dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if(args.length != 1) {
			System.out.println("Error, invalid input, insert the name of the output xml file");
			System.exit(2);
		}
		
		RnsInfoSerializer wf;
		
		try {
			wf = new RnsInfoSerializer();
			// do marshalling
			wf.marshal(args[0]);
		} catch (RnsReaderException e) {
			System.err.println("Could not instantiate data generator.");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private void marshal(String fileName) {
		try {
			// initialize JAXBContext and create marshaller
			JAXBContext jc = JAXBContext.newInstance(PACKAGE_JAXB_SOL1);
			Marshaller m = jc.createMarshaller();
			// makes the generated xml human readable, by adding new line
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			// set validation schema using default validation handler 
            SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(new File(SCHEMA_FILE));
            m.setSchema(schema);
            
            // create JAXB object
            Rns root = new Rns();
  
            // contains the association [place id, capacity]
            // it is required because the check regarding capacity cannot be done in xml schema
            Map<String, Integer> mapPlaceCapacity = new HashMap<>();
            
    		// Get the list of Gates and add them to JAXB element
    		Set<GateReader> setGates = monitor.getGates(null);
    		
    		for (GateReader gate: setGates) {
    			Gate e = new Gate();
    			e.setId(gate.getId());
    			e.setCapacity(BigInteger.valueOf(gate.getCapacity()));
    			e.setType(GateType.fromValue(gate.getType().name()));
    			Set<PlaceReader> nextPlaces = gate.getNextPlaces();
    			for(PlaceReader place: nextPlaces) {
    				e.getNextPlace().add(place.getId());
    			}
    			
    			// add gate to live list
    			root.getGate().add(e);
    			
    			mapPlaceCapacity.put(e.getId(), e.getCapacity().intValue());
    		}

    		// Get the list of RoadSegments and add them to JAXB element
    		Set<RoadSegmentReader> setRoadSegments = monitor.getRoadSegments(null);
    		Set<String> roads = new HashSet<>();
    		
    		for (RoadSegmentReader roadSegment: setRoadSegments) {
    			RoadSegment e = new RoadSegment();
    			e.setId(roadSegment.getId());
    			e.setCapacity(BigInteger.valueOf(roadSegment.getCapacity()));
    			e.setName(roadSegment.getName());
    			e.setRoadName(roadSegment.getRoadName());
    			Set<PlaceReader> nextPlaces = roadSegment.getNextPlaces();
    			for(PlaceReader place: nextPlaces) {
    				e.getNextPlace().add(place.getId());
    			}
    			// add road segment to live list
    			root.getRoadSegment().add(e);
    			// for the list of roads in the xml file
    			roads.add(roadSegment.getRoadName());
    			
    			mapPlaceCapacity.put(e.getId(), e.getCapacity().intValue());
    		}
    		
    		// add list of roads to JAXB element
    		for (String road: roads) {
    			Road e = new Road();
    			e.setName(road);
    			// add road to live list
    			root.getRoad().add(e);
    		}
    		
    		// Get the list of ParkingAreas and add them to JAXB element
    		Set<ParkingAreaReader> setParkingAreas = monitor.getParkingAreas(null);
    		// all the services in the xml file
    		Set<String> services = new HashSet<>();

    		for (ParkingAreaReader parkingArea: setParkingAreas) {
    			ParkingArea e = new ParkingArea();
    			e.setId(parkingArea.getId());
    			e.setCapacity(BigInteger.valueOf(parkingArea.getCapacity()));
    			Set<PlaceReader> nextPlaces = parkingArea.getNextPlaces();
    			for(PlaceReader place: nextPlaces) {
    				e.getNextPlace().add(place.getId());
    			}
				
				// add services to parking area
				e.getService().addAll(parkingArea.getServices());
				
				// for the list of services in the xml file
				services.addAll(parkingArea.getServices());
				
				// add parking area to live list
				root.getParkingArea().add(e);	
    			
    			mapPlaceCapacity.put(e.getId(), e.getCapacity().intValue());
    		}  
    		
    		// add list of services to JAXB element
    		for (String service: services) {
    			Service e = new Service();
    			e.setName(service);
    			// add service to live list
    			root.getService().add(e);
    		}
    		
    		// Get the list of Vehicles and add them to JAXB element
    		Set<VehicleReader> setVehicles = monitor.getVehicles(null,null,null);
    		
    		for (VehicleReader vehicle: setVehicles) {
    			Vehicle e = new Vehicle();
    			e.setId(vehicle.getId());
    			e.setType(VehicleType.fromValue(vehicle.getType().name()));
    			dateFormat.format(vehicle.getEntryTime().getTime());
    			XMLGregorianCalendar entryTime = Util.covertCalendarToXMLGregorianCalendar(dateFormat.getCalendar());
    			if(entryTime == null)
    				throw new NullPointerException("Error, cannot create calendar");
				e.setEntryTime(entryTime );
    			e.setDestination(vehicle.getDestination().getId());
    			e.setOrigin(vehicle.getOrigin().getId());
    			e.setPosition(vehicle.getPosition().getId());
    			e.setState(VehicleState.fromValue(vehicle.getState().name()));
    			
				// update the vehicle capacity for the place
    			if(!mapPlaceCapacity.containsKey(e.getPosition())) {
    				throw new SAXException("Error, vehicle position place does not exist");
    			}
    			
    			mapPlaceCapacity.put(e.getPosition(), mapPlaceCapacity.get(e.getPosition())-1);
    			// check if the place vehicle capacity was exceeded    			
    			if(mapPlaceCapacity.get(e.getPosition()) < 0) {
    				throw new SAXException("Error, place vehicle capacity was exceeded");
    			}
    			
				// also check if the origin is a IN or INOUT Gate
    			GateReader vehicleOrigin = (GateReader) vehicle.getOrigin();
				if(vehicleOrigin.getType().equals(it.polito.dp2.RNS.GateType.OUT)) {
					throw new SAXException("Error, origin must be an IN/INOUT gate");
				}
				
				// vehicle is ok, add it to live list
				root.getVehicle().add(e);
    		}
            
            // do marshalling
    		//m.marshal(root, System.out);
    		m.marshal(root, new FileOutputStream(fileName));
            
		} catch (JAXBException e) {
			e.printStackTrace();
			System.exit(3);
		} catch (SAXException e) {
			e.printStackTrace();
			System.exit(4);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(5);
		}	
		catch (NullPointerException e) {
			e.printStackTrace();
			System.exit(6);
		}
	}
}
