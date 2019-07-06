package it.polito.dp2.RNS.sol1;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import it.polito.dp2.RNS.ConnectionReader;
import it.polito.dp2.RNS.GateReader;
import it.polito.dp2.RNS.GateType;
import it.polito.dp2.RNS.ParkingAreaReader;
import it.polito.dp2.RNS.PlaceReader;
import it.polito.dp2.RNS.RnsReader;
import it.polito.dp2.RNS.RoadSegmentReader;
import it.polito.dp2.RNS.VehicleReader;
import it.polito.dp2.RNS.VehicleState;
import it.polito.dp2.RNS.VehicleType;
import it.polito.dp2.RNS.sol1.model.ConnectionReaderImpl;

public class RnsReaderImpl implements RnsReader {
	private Set<GateReader> gates;
	private Set<ParkingAreaReader> parkingAreas;
	private Set<RoadSegmentReader> roadSegments;
	private Set<VehicleReader> vehicles;
	
	public RnsReaderImpl() {
		super();
		gates = new HashSet<>();
		parkingAreas = new HashSet<>();
		roadSegments = new HashSet<>();
		vehicles = new HashSet<>();
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
		for (VehicleReader v: vehicles)
			if(v.getId().equals(id))
				return v;
		return null;
	}

	@Override
	public Set<VehicleReader> getVehicles(Calendar since, Set<VehicleType> types, VehicleState state) {
		if((since == null) && (types == null) && (state == null))
			return this.vehicles;
		
		Set<VehicleReader> vehicles = new HashSet<>();

		// filter by date
		for (VehicleReader v: this.vehicles)
			if((since == null) || v.getEntryTime().before(since))
				vehicles.add(v);
		
		// filter by type
		if(types != null) {
			for (VehicleReader v: vehicles)
				if(!(types.contains(v.getType())))	
					vehicles.remove(v);	
		}
		
		// filter by state
		if(state != null) {
			for (VehicleReader v: vehicles)
				if(!(v.getState().equals(state)))
					vehicles.remove(v);
		}
		
		return vehicles;
	}
}
