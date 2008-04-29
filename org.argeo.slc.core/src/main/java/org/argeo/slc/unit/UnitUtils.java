package org.argeo.slc.unit;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static junit.framework.Assert.assertEquals;

public abstract class UnitUtils {
	public static void assertDateSec(Date expected, Date reached) {
		Calendar expectedCal = new GregorianCalendar();
		expectedCal.setTime(expected);
		Calendar reachedCal = new GregorianCalendar();
		reachedCal.setTime(reached);
		assertEquals(expectedCal.get(Calendar.YEAR), reachedCal
				.get(Calendar.YEAR));
		assertEquals(expectedCal.get(Calendar.MONTH), reachedCal
				.get(Calendar.MONTH));
		assertEquals(expectedCal.get(Calendar.DATE), reachedCal
				.get(Calendar.DATE));
		assertEquals(expectedCal.get(Calendar.HOUR_OF_DAY), reachedCal
				.get(Calendar.HOUR_OF_DAY));
		assertEquals(expectedCal.get(Calendar.MINUTE), reachedCal
				.get(Calendar.MINUTE));
		assertEquals(expectedCal.get(Calendar.SECOND), reachedCal
				.get(Calendar.SECOND));
	}

	private UnitUtils() {

	}

}
