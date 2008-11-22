package org.argeo.slc.core.test.context;

import org.argeo.slc.core.test.TestDataUtils;
import org.argeo.slc.test.TestData;
import org.argeo.slc.test.TestDataProvider;

public class DefaultContextTestData extends SimpleContextAware implements
		TestData, TestDataProvider {

	public <T extends TestData> T getTestData(Class<T> clss, String key) {
		return TestDataUtils.getItSelf(clss, this);
	}

}
