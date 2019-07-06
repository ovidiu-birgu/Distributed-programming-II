package it.polito.dp2.RNS.sol3.service.util;

import java.util.Comparator;

import it.polito.dp2.RNS.ConnectionReader;

public class ConnectionReaderComparator implements Comparator<ConnectionReader>{

	@Override
	public int compare(ConnectionReader o1, ConnectionReader o2) {
		String idO1 = o1.getFrom().getId()+o1.getTo().getId();
		String idO2 = o2.getFrom().getId()+o2.getTo().getId();

		return idO1.compareTo(idO2);
	}

}
