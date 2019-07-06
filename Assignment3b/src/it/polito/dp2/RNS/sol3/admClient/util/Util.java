package it.polito.dp2.RNS.sol3.admClient.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Contains useful helper functions
 */
public class Util {
	
	/**
	 * Converts XMLGregorianCalendar to Calendar
	 * @param xmlGregorianCalendar
	 * @return
	 * Calendar
	 */
	public static Calendar covertXMLGregorianCalendarToCalendar(XMLGregorianCalendar xmlGregorianCalendar) {
		// convert XMLGregorianCalendar to GregorianCalendar
		GregorianCalendar gc = xmlGregorianCalendar.toGregorianCalendar();
		// convert GregorianCalendar to Date
		Date date = gc.getTime();
		// convert Date to Calendar
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar;
	}	
}
