package it.polito.dp2.RNS.sol3.admClient.model;

import java.util.HashSet;
import java.util.Set;

import it.polito.dp2.RNS.ParkingAreaReader;

public class ParkingAreaReaderImpl extends PlaceReaderImpl implements ParkingAreaReader {
	private Set<String> services;

	public ParkingAreaReaderImpl(String id, int capacity) {
		super(id, capacity);
		services = new HashSet<>();
	}

	@Override
	public Set<String> getServices() {
		return services;
	}
}
