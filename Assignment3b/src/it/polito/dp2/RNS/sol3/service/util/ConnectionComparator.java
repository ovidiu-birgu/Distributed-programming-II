package it.polito.dp2.RNS.sol3.service.util;

import java.util.Comparator;

import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.Connection;

public class ConnectionComparator implements Comparator<Connection>{

	@Override
	public int compare(Connection o1, Connection o2) {
		String idO1 = o1.getFrom()+o1.getTo();
		String idO2 = o2.getFrom()+o2.getTo();

		return idO1.compareTo(idO2);
	}

}
