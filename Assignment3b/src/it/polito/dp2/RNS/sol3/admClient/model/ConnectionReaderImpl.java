package it.polito.dp2.RNS.sol3.admClient.model;

import it.polito.dp2.RNS.ConnectionReader;
import it.polito.dp2.RNS.PlaceReader;

public class ConnectionReaderImpl implements ConnectionReader {
	private PlaceReader from, to;
	
	public ConnectionReaderImpl(PlaceReader from, PlaceReader to) {
		super();
		this.from = from;
		this.to = to;
	}

	@Override
	public PlaceReader getFrom() {
		return from;
	}

	@Override
	public PlaceReader getTo() {
		return to;
	}

}
