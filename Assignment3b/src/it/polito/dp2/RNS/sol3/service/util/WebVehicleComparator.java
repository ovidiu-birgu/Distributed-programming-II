package it.polito.dp2.RNS.sol3.service.util;

import java.util.Comparator;

import it.polito.dp2.RNS.sol3.service.jaxb.rnssystem.Webvehicle;

public class WebVehicleComparator implements Comparator<Webvehicle>{

	@Override
	public int compare(Webvehicle wv1, Webvehicle wv2) {
		String idwv1 = wv1.getSelf();
		String idwv2 = wv2.getSelf();
		return idwv1.compareTo(idwv2);
	}

}
