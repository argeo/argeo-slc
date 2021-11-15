package org.argeo.slc.spring.unit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public abstract class UnitUtils {
	public static void assertDateSec(Date expected, Date reached) {
		if (expected == null) {
			assertNull(reached);
			return;
		} else {
			assertNotNull(reached);
		}

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
