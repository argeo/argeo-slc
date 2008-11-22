package org.argeo.slc.core.test.context;

import org.argeo.slc.core.test.TestData;
import org.argeo.slc.core.test.TestDataProvider;
import org.argeo.slc.core.test.TestDataUtils;

public class DefaultContextTestData extends SimpleContextAware implements
		TestData, TestDataProvider {

	public <T extends TestData> T getTestData(Class<T> clss, String key) {
		return TestDataUtils.getItSelf(clss, this);
	}

}
