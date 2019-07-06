package it.polito.dp2.RNS.sol3.admClient.model;

import it.polito.dp2.RNS.IdentifiedEntityReader;

public class IdentifiedEntityReaderImpl implements IdentifiedEntityReader {
	private String id;
	
	public IdentifiedEntityReaderImpl(String id) {
		super();
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}
}
