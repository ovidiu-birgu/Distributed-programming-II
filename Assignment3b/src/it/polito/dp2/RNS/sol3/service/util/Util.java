package it.polito.dp2.RNS.sol3.service.util;

import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Contains useful helper functions
 */
public class Util {

	/**
	 * Return the random number in the interval [0, max]
	 * @param max
	 * @return
	 */
	public static int getRandomNumber (int max) {
		Random rand = new Random(System.currentTimeMillis());
		
		if(max <= 0)
			return 0;
		int r = rand.nextInt(max);

		return r;
	}
	
	/**
	 * Find a key by value in a map
	 * @param map
	 * @param value
	 * @return
	 */
	public static String getKeyByValue(Map<String, Integer> map, Integer value) {
		for (Entry<String, Integer> entry : map.entrySet()) {
			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Get the current date time in XMLGregorianCalendar format
	 * @return
	 */
	public static XMLGregorianCalendar getCurrentDateTime(){
		GregorianCalendar gc = new GregorianCalendar();
		DatatypeFactory dtf=null;
		try {
			dtf = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			return null;
		}
		return dtf.newXMLGregorianCalendar(gc);
	}	
}
