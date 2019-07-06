package it.polito.dp2.RNS.sol3.service.util;

import java.util.Comparator;

import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.Webplace;

public class WebPlaceComparator implements Comparator<Webplace>{

	@Override
	public int compare(Webplace place1, Webplace place2) {
		String idPlace1 = place1.getSelf();
		String idPlace2 = place2.getSelf();
		return idPlace1.compareTo(idPlace2);
	}

}
