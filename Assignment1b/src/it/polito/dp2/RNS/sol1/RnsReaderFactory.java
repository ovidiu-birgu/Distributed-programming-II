package it.polito.dp2.RNS.sol1;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import it.polito.dp2.RNS.GateReader;
import it.polito.dp2.RNS.GateType;
import it.polito.dp2.RNS.PlaceReader;
import it.polito.dp2.RNS.RnsReader;
import it.polito.dp2.RNS.RnsReaderException;
import it.polito.dp2.RNS.VehicleState;
import it.polito.dp2.RNS.VehicleType;
import it.polito.dp2.RNS.sol1.jaxb.Gate;
import it.polito.dp2.RNS.sol1.jaxb.ParkingArea;
import it.polito.dp2.RNS.sol1.jaxb.Rns;
import it.polito.dp2.RNS.sol1.jaxb.RoadSegment;
import it.polito.dp2.RNS.sol1.jaxb.Vehicle;
import it.polito.dp2.RNS.sol1.model.GateReaderImpl;
import it.polito.dp2.RNS.sol1.model.ParkingAreaReaderImpl;
import it.polito.dp2.RNS.sol1.model.RoadSegmentReaderImpl;
import it.polito.dp2.RNS.sol1.model.VehicleReaderImpl;
import it.polito.dp2.RNS.sol1.util.Util;

public class RnsReaderFactory extends it.polito.dp2.RNS.RnsReaderFactory {
	private final String PACKAGE_JAXB_SOL1 = "it.polito.dp2.RNS.sol1.jaxb";
	private final String SCHEMA_FILE = "xsd/rnsInfo.xsd";
	private final String INPUT_XML_FILE = "it.polito.dp2.RNS.sol1.RnsInfo.file";
	private String inputXMLFile;
	
	@Override
	public RnsReader newRnsReader() throws RnsReaderException {

		inputXMLFile = System.getProperty(INPUT_XML_FILE);
		if(inputXMLFile == null)
			throw new RnsReaderException("Error, input xml file property is not set");
		
		RnsReader jaxbMonitor = unmarshal();
		if(jaxbMonitor == null) {
			throw new RnsReaderException("Error unmarshalling");
		}
		
		return jaxbMonitor;
	}

	private RnsReaderImpl unmarshal() {
		try {
			Object root = null;
			RnsReaderImpl jaxbMonitor = new RnsReaderImpl();
			Rns rootElement = null;

			// initialize JAXBContext and create unmarshaller
			JAXBContext jc = JAXBContext.newInstance(PACKAGE_JAXB_SOL1);
			Unmarshaller u = jc.createUnmarshaller();

			// set validation schema using default validation handler 
			SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
			Schema schema = sf.newSchema(new File(SCHEMA_FILE));
			u.setSchema(schema);
			// note: I do not set an event handler for the unmarshaller because
			// I do not want to continue if there are errors
			root = u.unmarshal(new File(inputXMLFile));

			/** check if structure is valid
			 * NOT required with this implementation, because there is only one root element,
			 * but I left it in case I add other global elements in future
			 */
			if(root == null || !(root instanceof Rns)) {
				return null;
			}
			rootElement = (Rns) root;

			// contains the association [place id, capacity]
			// it is required because the check regarding capacity cannot be done in xml schema
			Map<String, Integer> mapPlaceCapacity = new HashMap<>();

			// fill JAXBRnsReader object

			Map<String, Object> allPlaces = new HashMap<>();

			// add the list of Gates to the JAXB element
			List<Gate> jaxbGates = rootElement.getGate();
			for (Gate gate: jaxbGates) {
				String id = gate.getId();
				int capacity = gate.getCapacity().intValue();
				it.polito.dp2.RNS.GateType type = it.polito.dp2.RNS.GateType.fromValue(gate.getType().name());
				GateReaderImpl g = new GateReaderImpl(id, capacity, type);

				allPlaces.put(id, g);

				mapPlaceCapacity.put(id, capacity);
			}

			// add the list of RoadSegments to the JAXB element
			List<RoadSegment> jaxbRoadSegments = rootElement.getRoadSegment();
			for (RoadSegment roadSegment: jaxbRoadSegments) {
				String id = roadSegment.getId();
				int capacity = roadSegment.getCapacity().intValue();
				String name = roadSegment.getName();
				String roadName = roadSegment.getRoadName();
				RoadSegmentReaderImpl rs = new RoadSegmentReaderImpl(id, capacity, name, roadName);

				allPlaces.put(id, rs);

				mapPlaceCapacity.put(id, capacity);
			}

			// add the list of ParkingAreas to the JAXB element
			List<ParkingArea> jaxbParkingAreas = rootElement.getParkingArea();
			for (ParkingArea parkingArea: jaxbParkingAreas) {
				String id = parkingArea.getId();
				int capacity = parkingArea.getCapacity().intValue();
				ParkingAreaReaderImpl pa = new ParkingAreaReaderImpl(id, capacity);
				pa.getServices().addAll(parkingArea.getService());

				allPlaces.put(id, pa);

				mapPlaceCapacity.put(id, capacity);
			}

			// also add the next places connections

			// connections for gates
			for (Gate gate: jaxbGates) {
				String id = gate.getId();
				GateReaderImpl g = (GateReaderImpl) allPlaces.get(id);
				List<String> nextPlaces = gate.getNextPlace();
				for(String p: nextPlaces) {
					g.getNextPlaces().add((PlaceReader) allPlaces.get(p));
				}
				// update element in map
				allPlaces.replace(id, g);
				// add element to live set
				jaxbMonitor.getGates(null).add(g);
			}

			// connections for RoadSegments
			for (RoadSegment roadSegment: jaxbRoadSegments) {
				String id = roadSegment.getId();
				RoadSegmentReaderImpl rs = (RoadSegmentReaderImpl) allPlaces.get(id);
				List<String> nextPlaces = roadSegment.getNextPlace();
				for(String p: nextPlaces) {
					rs.getNextPlaces().add((PlaceReader) allPlaces.get(p));
				}
				// update element in map
				allPlaces.replace(id, rs);
				// add element to live set
				jaxbMonitor.getRoadSegments(null).add(rs);	
			}

			// connections for ParkingAreas
			for (ParkingArea parkingArea: jaxbParkingAreas) {
				String id = parkingArea.getId();
				ParkingAreaReaderImpl pa = (ParkingAreaReaderImpl) allPlaces.get(id);
				List<String> nextPlaces = parkingArea.getNextPlace();
				for(String p: nextPlaces) {
					pa.getNextPlaces().add((PlaceReader) allPlaces.get(p));
				}
				// update element in map
				allPlaces.replace(id, pa);
				// add element to live set
				jaxbMonitor.getParkingAreas(null).add(pa);
			}

			// add the list of Vehicles to the JAXB element
			List<Vehicle> jaxbVehicles = rootElement.getVehicle();
			for (Vehicle vehicle: jaxbVehicles) {
				String id = vehicle.getId();
				Calendar calendar = Util.covertXMLGregorianCalendarToCalendar(vehicle.getEntryTime());    			
				VehicleType type = VehicleType.fromValue(vehicle.getType().name());
				PlaceReader destination = (PlaceReader) allPlaces.get(vehicle.getDestination());
				GateReader origin = (GateReader) allPlaces.get(vehicle.getOrigin());
				PlaceReader position = (PlaceReader) allPlaces.get(vehicle.getPosition());
				VehicleState state = VehicleState.fromValue(vehicle.getState().name());

				// update the vehicle capacity for the place
				if(!mapPlaceCapacity.containsKey(position.getId())) {
					throw new SAXException("Error, vehicle position place does not exist");
				}				

				mapPlaceCapacity.put(position.getId(), mapPlaceCapacity.get(position.getId())-1);
				// check if the place vehicle capacity was exceeded
				if(mapPlaceCapacity.get(position.getId()) <  0) {
					throw new SAXException("Error, place vehicle capacity was exceeded");
				}
				
				// also check if the origin is a IN or INOUT Gate
				if(origin.getType().equals(GateType.OUT)) {
					throw new SAXException("Error, origin must be an IN/INOUT gate");
				}
				
				VehicleReaderImpl v = new VehicleReaderImpl(id, calendar, type, destination, origin, position, state);
				// add element to live set
				jaxbMonitor.getVehicles(null, null, null).add(v);
			}

			return jaxbMonitor;

		} catch (JAXBException | SAXException | NullPointerException e) {
			e.printStackTrace();
			return null;
		}
	}
}
