package it.polito.dp2.RNS.sol3.admClient.model;

import java.util.HashSet;
import java.util.Set;

import it.polito.dp2.RNS.PlaceReader;

public class PlaceReaderImpl extends IdentifiedEntityReaderImpl implements PlaceReader {
	private int capacity;
	private Set<PlaceReader>  nextPlaces;
	
	public PlaceReaderImpl(String id) {
		super(id);
		capacity = 0;
		nextPlaces = new HashSet<>();
	}
	
	public PlaceReaderImpl(String id, int capacity) {
		super(id);
		this.capacity = capacity;
		nextPlaces = new HashSet<>();
	}

	@Override
	public int getCapacity() {
		return capacity;
	}

	@Override
	public Set<PlaceReader> getNextPlaces() {
		return nextPlaces;
	}
}
