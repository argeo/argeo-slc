package org.argeo.slc.core.test;

public interface TestDataProvider {
	public <T extends TestData> T getTestData(Class<T> clss, String key);
}
