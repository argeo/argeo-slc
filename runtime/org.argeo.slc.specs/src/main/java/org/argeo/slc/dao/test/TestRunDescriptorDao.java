package org.argeo.slc.dao.test;

import org.argeo.slc.core.test.TestRunDescriptor;

public interface TestRunDescriptorDao {
	public TestRunDescriptor getTestRunDescriptor(String id);

	public void saveOrUpdate(TestRunDescriptor testRunDescriptor);
}
