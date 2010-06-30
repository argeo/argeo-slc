/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.argeo.slc.unit;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertNotNull;

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
