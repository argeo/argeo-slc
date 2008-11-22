package org.argeo.slc.services.test;

import org.argeo.slc.test.TestRunDescriptor;

public interface TestManagerService {
	public void registerTestRunDescriptor(TestRunDescriptor testRunDescriptor);

	public void addResultToCollection(String collectionId, String resultUuid);

	public void removeResultFromCollection(String collectionId,
			String resultUuid);
}
