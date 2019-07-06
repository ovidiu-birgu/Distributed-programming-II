package it.polito.dp2.RNS.sol2.util;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Contains useful helper functions
 */
public class Util {
	
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
}
