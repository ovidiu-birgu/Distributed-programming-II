package it.polito.dp2.RNS.sol1.model;

import it.polito.dp2.RNS.RoadSegmentReader;

public class RoadSegmentReaderImpl extends PlaceReaderImpl implements RoadSegmentReader {
	private String name, roadName;
	
	public RoadSegmentReaderImpl(String id, int capacity, String name, String roadName) {
		super(id, capacity);
		this.name = name;
		this.roadName = roadName;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getRoadName() {
		return roadName;
	}
}
