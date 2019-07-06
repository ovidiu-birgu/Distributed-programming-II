package it.polito.dp2.RNS.debug;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Set;

import it.polito.dp2.RNS.ConnectionReader;
import it.polito.dp2.RNS.GateReader;
import it.polito.dp2.RNS.ParkingAreaReader;
import it.polito.dp2.RNS.PlaceReader;
import it.polito.dp2.RNS.RnsReader;
import it.polito.dp2.RNS.RoadSegmentReader;
import it.polito.dp2.RNS.VehicleReader;

public class Debug {
	// debug printing
	private static DateFormat referenceDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
	private static DateFormat testDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
	
	public static void printAll(RnsReader referenceRnsReader, RnsReader testRnsReader) {
		printHeader("***REFERENCE READER***");
		printLine(' ');
		printGates(referenceRnsReader);
		printRoadSegments(referenceRnsReader);
		printParkingAreas(referenceRnsReader);
		printConnections(referenceRnsReader);
		printVehicles(referenceRnsReader, referenceDateFormat);
		
		printHeader("***TEST READER***");
		printLine(' ');
		printGates(testRnsReader);
		printRoadSegments(testRnsReader);
		printParkingAreas(testRnsReader);
		printConnections(testRnsReader);
		printVehicles(testRnsReader, testDateFormat);
		
		printHeader("***TEST READER DETAILED***");
		printLine(' ');
		printGatesDetailed(testRnsReader);
		printRoadSegmentsDetailed(testRnsReader);
		printParkingAreasDetailed(testRnsReader);
		printConnectionsDetailed(testRnsReader);
		printVehiclesDetailed(testRnsReader, testDateFormat);
	}

	private static void printGatesDetailed(RnsReader rnsReader) {
		// Get the list of Gates
		Set<GateReader> set = rnsReader.getGates(null);
		
		/* Print the header of the table */
		printHeader('#',"#Information about GATES");
		printHeader("#Number of Gates: "+set.size());
		printHeader("#List of Gates:");
		printHeader("Id"+"\tCapacity"+"\tType",'-');
		
		// For each Gate print related data
		for (GateReader gate: set) {
			printHeader(gate.getId()+"\t"+gate.getCapacity()+"\t"+gate.getType().name());
			// print next places for this gate
			printNextPlaces(gate.getNextPlaces());
		}
		printBlankLine();		
	}	
	
	private static void printRoadSegmentsDetailed(RnsReader rnsReader) {
		// Get the list of Road Segments
		Set<RoadSegmentReader> set = rnsReader.getRoadSegments(null);
		
		/* Print the header of the table */
		printHeader('#',"#Information about ROAD SEGMENTS");
		printHeader("#Number of Road Segments: "+set.size());
		printHeader("#List of Road Segments:");
		printHeader("Id"+"\tCapacity"+"\tName"+"\tRoad name",'-');
		
		// For each Road segment print related data
		for (RoadSegmentReader seg: set) {
			printHeader(seg.getId()+"\t"+seg.getCapacity()+"\t"+seg.getName()+"\t"+seg.getRoadName());
			// print next places for this gate
			printNextPlaces(seg.getNextPlaces());
		}
		printBlankLine();		
	}	
	
	private static void printParkingAreasDetailed(RnsReader rnsReader) {
		// Get the list of Parking Areas
		Set<ParkingAreaReader> set = rnsReader.getParkingAreas(null);
		
		/* Print the header of the table */
		printHeader('#',"#Information about PARKING AREAS");
		printHeader("#Number of Parking Areas: "+set.size());
		printHeader("#List of Parking Areas:");
		printHeader("Id"+"\tCapacity"+"\tServices",'-');
		
		// For each Parking Area print related data
		for (ParkingAreaReader pa: set) {
			String services = "";
			for (String s:pa.getServices())
				services += s+" ";
			printHeader(pa.getId()+"\t"+pa.getCapacity()+"\t"+services);
			// print next places for this gate
			printNextPlaces(pa.getNextPlaces());
		}
		printBlankLine();		
	}

	private static void printConnectionsDetailed(RnsReader rnsReader) {
		// Get the list of Connections
		Set<ConnectionReader> set = rnsReader.getConnections();
		
		/* Print the header of the table */
		printHeader('#',"#Information about CONNECTIONS");
		printHeader("#Number of Connections: "+set.size());
		printHeader("#List of Connections:");
		printHeader("From (Id)"+"\tTo (Id)",'-');
		
		// For each connection, print related data
		for (ConnectionReader conn: set) {
			printHeader(conn.getFrom().getId()+"\t"+conn.getTo().getId());
			// print next places of from
			printHeader("#"+conn.getFrom().getId());
			printNextPlaces(conn.getFrom().getNextPlaces());
			// print next places of to
			printHeader("#"+conn.getTo().getId());
			printNextPlaces(conn.getTo().getNextPlaces());
		}
		printBlankLine();
	}	
	
	private static void printVehiclesDetailed(RnsReader rnsReader, DateFormat dateFormat) {
		// Get the list of Vehicles
		Set<VehicleReader> set = rnsReader.getVehicles(null,null,null);
		
		/* Print the header of the table */
		printHeader('#',"#Information about VEHICLES");
		printHeader("#Number of Vehicles: "+set.size());
		printHeader("#List of Vehicles:");
		printHeader("Id"+"\t\tType"+"\tEntry time"+"\tDestination"+"\tOrigin"+"\tPosition"+"\tState",'-');
		
		// For each Road segment print related data
		for (VehicleReader v: set) {
			printHeader(v.getId()+"\t"+v.getType().name()+"\t"+dateFormat.format(v.getEntryTime().getTime())+"\t"+
					v.getDestination().getId()+"\t"+v.getOrigin().getId()+"\t"+v.getPosition().getId()+"\t"+v.getState().name());
			printHeader("#"+v.getDestination().getId());
			printNextPlaces(v.getDestination().getNextPlaces());
			printHeader("#"+v.getOrigin().getId());
			printNextPlaces(v.getOrigin().getNextPlaces());
			printHeader("#"+v.getPosition().getId());
			printNextPlaces(v.getPosition().getNextPlaces());			
		}
		printBlankLine();
	}

	private static void printNextPlaces(Set<PlaceReader> nextPlaces) {

		printHeader("#Number of Next places: "+nextPlaces.size());
		printHeader("#List of Next places:");
		printHeader("Id"+"\tCapacity");
		
		// For each next place, print related data
		for(PlaceReader p: nextPlaces) {
			printHeader(p.getId()+"\t"+p.getCapacity());
		}
		printBlankLine();		
	}

	private static void printGates(RnsReader rnsReader) {
		// Get the list of Gates
		Set<GateReader> set = rnsReader.getGates(null);
		
		/* Print the header of the table */
		printHeader('#',"#Information about GATES");
		printHeader("#Number of Gates: "+set.size());
		printHeader("#List of Gates:");
		printHeader("Id"+"\tCapacity"+"\tType",'-');
		
		// For each Gate print related data
		for (GateReader gate: set) {
			printHeader(gate.getId()+"\t"+gate.getCapacity()+"\t"+gate.getType().name());
		}
		printBlankLine();
	}

	private static void printRoadSegments(RnsReader rnsReader) {
		// Get the list of Road Segments
		Set<RoadSegmentReader> set = rnsReader.getRoadSegments(null);
		
		/* Print the header of the table */
		printHeader('#',"#Information about ROAD SEGMENTS");
		printHeader("#Number of Road Segments: "+set.size());
		printHeader("#List of Road Segments:");
		printHeader("Id"+"\tCapacity"+"\tName"+"\tRoad name",'-');
		
		// For each Road segment print related data
		for (RoadSegmentReader seg: set) {
			printHeader(seg.getId()+"\t"+seg.getCapacity()+"\t"+seg.getName()+"\t"+seg.getRoadName());
		}
		printBlankLine();
	}
	
	private static void printParkingAreas(RnsReader rnsReader) {
		// Get the list of Parking Areas
		Set<ParkingAreaReader> set = rnsReader.getParkingAreas(null);
		
		/* Print the header of the table */
		printHeader('#',"#Information about PARKING AREAS");
		printHeader("#Number of Parking Areas: "+set.size());
		printHeader("#List of Parking Areas:");
		printHeader("Id"+"\tCapacity"+"\tServices",'-');
		
		// For each Parking Area print related data
		for (ParkingAreaReader pa: set) {
			String services = "";
			for (String s:pa.getServices())
				services += s+" ";
			printHeader(pa.getId()+"\t"+pa.getCapacity()+"\t"+services);
		}
		printBlankLine();
		
	}

	private static void printConnections(RnsReader rnsReader) {
		// Get the list of Connections
		Set<ConnectionReader> set = rnsReader.getConnections();
		
		/* Print the header of the table */
		printHeader('#',"#Information about CONNECTIONS");
		printHeader("#Number of Connections: "+set.size());
		printHeader("#List of Connections:");
		printHeader("From (Id)"+"\tTo (Id)",'-');
		
		// For each connection, print related data
		for (ConnectionReader conn: set) {
			printHeader(conn.getFrom().getId()+"\t"+conn.getTo().getId());
		}
		printBlankLine();
		
	}

	private static void printVehicles(RnsReader rnsReader, DateFormat dateFormat) {
		// Get the list of Vehicles
		Set<VehicleReader> set = rnsReader.getVehicles(null,null,null);
		
		/* Print the header of the table */
		printHeader('#',"#Information about VEHICLES");
		printHeader("#Number of Vehicles: "+set.size());
		printHeader("#List of Vehicles:");
		printHeader("Id"+"\t\tType"+"\tEntry time"+"\tDestination"+"\tOrigin"+"\tPosition"+"\tState",'-');
		
		// For each Road segment print related data
		for (VehicleReader v: set) {
			printHeader(v.getId()+"\t"+v.getType().name()+"\t"+dateFormat.format(v.getEntryTime().getTime())+"\t"+
					v.getDestination().getId()+"\t"+v.getOrigin().getId()+"\t"+v.getPosition().getId()+"\t"+v.getState().name());
		}
		printBlankLine();
		
	}

	private static void printBlankLine() {
		System.out.println(" ");
	}

	private static void printLine(char c) {
		System.out.println(makeLine(c));
	}

	private static void printHeader(String header) {
		System.out.println(header);
	}

	private static void printHeader(String header, char c) {		
		System.out.println(header);
		printLine(c);	
	}
	
	private static void printHeader(char c, String header) {		
		printLine(c);	
		System.out.println(header);
	}
	
	private static StringBuffer makeLine(char c) {
		StringBuffer line = new StringBuffer(132);
		
		for (int i = 0; i < 132; ++i) {
			line.append(c);
		}
		return line;
	}
}
