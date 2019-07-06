package it.polito.dp2.RNS.sol3.service.util;

import java.util.Comparator;

import it.polito.dp2.RNS.PlaceReader;

public class PlaceComparator implements Comparator<Object>{

	@Override
	public int compare(Object place1, Object place2) {
		String idPlace1 = ((PlaceReader) place1).getId();
		String idPlace2 = ((PlaceReader) place2).getId();
		return idPlace1.compareTo(idPlace2);
	}

}
