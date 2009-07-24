package org.argeo.slc.core.test;

import org.argeo.slc.UnsupportedException;
import org.argeo.slc.test.TestData;
import org.argeo.slc.test.TestDataProvider;

/** Utilities for dealing with test datas. */
public class TestDataUtils {
	/** Extracts the test data from the given provider. */
	public static <T extends TestData> T getFromProvider(Object obj,
			Class<T> clss, String key) {
		if (obj instanceof TestDataProvider) {
			TestDataProvider testDataProvider = (TestDataProvider) obj;
			return testDataProvider.getTestData(clss, key);
		} else {
			throw new UnsupportedException("test data provider", obj);
		}
	}

	/**
	 * Extracts the test data from the given provider using <code>null</code>
	 * as key.
	 */
	public static <T extends TestData> T getFromProvider(Object obj,
			Class<T> clss) {
		return getFromProvider(obj, clss, null);
	}

	/**
	 * Returns it self after making the proper checks. Used for test data being
	 * their own data providers.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends TestData> T getItSelf(Class<T> clss,
			TestData testDataObject) {
		if (clss.isAssignableFrom(testDataObject.getClass())) {
			return (T) testDataObject;
		} else {
			throw new UnsupportedException("test data", testDataObject);
		}

	}

	/** Makes sure this is an utility class. */
	private TestDataUtils() {

	}
}
