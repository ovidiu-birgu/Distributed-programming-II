package it.polito.dp2.RNS.sol1.model;

import java.util.Calendar;

import it.polito.dp2.RNS.PlaceReader;
import it.polito.dp2.RNS.VehicleReader;
import it.polito.dp2.RNS.VehicleState;
import it.polito.dp2.RNS.VehicleType;

public class VehicleReaderImpl extends IdentifiedEntityReaderImpl implements VehicleReader {
	private VehicleType type;
	private Calendar entryTime;
	private PlaceReader destination, origin, position;
	private VehicleState state;
	

	public VehicleReaderImpl(String id, Calendar entryTime, VehicleType type, 
			PlaceReader destination, PlaceReader origin, PlaceReader position, VehicleState state) {
		super(id);
		this.type = type;
		this.entryTime = entryTime;
		this.destination = destination;
		this.origin = origin;
		this.position = position;
		this.state = state;
	}

	@Override
	public PlaceReader getDestination() {
		return destination;
	}

	@Override
	public Calendar getEntryTime() {
		return entryTime;
	}

	@Override
	public PlaceReader getOrigin() {
		return origin;
	}

	@Override
	public PlaceReader getPosition() {
		return position;
	}

	@Override
	public VehicleState getState() {
		return state;
	}

	@Override
	public VehicleType getType() {
		return type;
	}
}
