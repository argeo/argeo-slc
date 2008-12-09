package org.argeo.slc.services.test;

import org.argeo.slc.test.TestRunDescriptor;

/** Performs complex business operations. */
public interface TestManagerService {
	/** Performs operations related to the addition of a new test run. */
	public void registerTestRunDescriptor(TestRunDescriptor testRunDescriptor);

	/** Adds a result in a collection based on their ids. */
	public void addResultToCollection(String collectionId, String resultUuid);

	/** Removes a result from a collection based on their ids. */
	public void removeResultFromCollection(String collectionId,
			String resultUuid);
}
