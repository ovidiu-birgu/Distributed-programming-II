package it.polito.dp2.RNS.sol1.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Contains useful helper functions
 */
public class Util {


	/**
	 * Converts Calendar to XMLGregorianCalendar
	 * @param calendar
	 * @return
	 * XMLGregorianCalendar
	 */
	public static XMLGregorianCalendar covertCalendarToXMLGregorianCalendar(Calendar calendar) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(calendar.getTime());
		gc.setTimeZone(calendar.getTimeZone());
		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	}
	
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
