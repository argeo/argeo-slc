package org.argeo.slc.test;

public interface TestDataProvider {
	public <T extends TestData> T getTestData(Class<T> clss, String key);
}
