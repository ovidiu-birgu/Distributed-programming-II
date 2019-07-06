package it.polito.dp2.RNS.sol3.admClient.util;

import java.util.Set;

import it.polito.dp2.RNS.GateReader;
import it.polito.dp2.RNS.ParkingAreaReader;
import it.polito.dp2.RNS.RoadSegmentReader;

/**
 * Debug printing 
 */
public class Debug {
	
	public static void printAll(Set<GateReader> gates, 
			Set<ParkingAreaReader> parkingAreas, 
			Set<RoadSegmentReader> roadSegments) {
		
		printHeader("*** STARTED DEBUG PRINTING ***");
		printLine(' ');
		printGates(gates);
		printRoadSegments(roadSegments);
		printParkingAreas(parkingAreas);
		printHeader("*** FINISHED DEBUG PRINTING ***");
	}

	private static void printGates(Set<GateReader> set) {
		
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

	private static void printRoadSegments(Set<RoadSegmentReader> set) {
		
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
	
	private static void printParkingAreas(Set<ParkingAreaReader> set) {
		
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
