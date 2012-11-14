/*
 * Copyright (C) 2007-2012 Argeo GmbH
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
package org.argeo.slc.core.test;

import org.argeo.slc.SlcException;
import org.argeo.slc.test.TestStatus;

public abstract class SlcTestUtils {
	public static String statusToString(Integer status) {
		if (status.equals(TestStatus.PASSED)) {
			return TestStatus.STATUSSTR_PASSED;
		} else if (status.equals(TestStatus.FAILED)) {
			return TestStatus.STATUSSTR_FAILED;
		} else if (status.equals(TestStatus.ERROR)) {
			return TestStatus.STATUSSTR_ERROR;
		} else {
			throw new SlcException("Unrecognized status " + status);
		}
	}

	public static Integer stringToStatus(String statusStr) {
		if (statusStr.equals(TestStatus.STATUSSTR_PASSED)) {
			return TestStatus.PASSED;
		} else if (statusStr.equals(TestStatus.STATUSSTR_FAILED)) {
			return TestStatus.FAILED;
		} else if (statusStr.equals(TestStatus.STATUSSTR_ERROR)) {
			return TestStatus.ERROR;
		} else {
			throw new SlcException("Unrecognized status string " + statusStr);
		}
	}

	private SlcTestUtils() {

	}

}
