package org.argeo.slc.runtime.test;

import org.argeo.api.slc.SlcException;
import org.argeo.api.slc.test.TestStatus;

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
