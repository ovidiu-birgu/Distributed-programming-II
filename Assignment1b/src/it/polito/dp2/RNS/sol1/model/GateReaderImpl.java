package it.polito.dp2.RNS.sol1.model;

import it.polito.dp2.RNS.GateReader;
import it.polito.dp2.RNS.GateType;

public class GateReaderImpl extends PlaceReaderImpl implements GateReader{
	private GateType type;
	
	public GateReaderImpl(String id, int capacity, GateType type) {
		super(id, capacity);
		this.type = type;
	}

	@Override
	public GateType getType() {
		return type;
	}
}
