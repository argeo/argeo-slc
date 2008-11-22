package org.argeo.slc.services.test.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.test.tree.TreeTestResultCollection;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.dao.test.TestRunDescriptorDao;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.services.test.TestManagerService;
import org.argeo.slc.test.TestRunDescriptor;

public class TestManagerServiceImpl implements TestManagerService {
	private Log log = LogFactory.getLog(getClass());

	private final TreeTestResultDao treeTestResultDao;
	private final TestRunDescriptorDao testRunDescriptorDao;
	private final SlcExecutionDao slcExecutionDao;
	private final TreeTestResultCollectionDao treeTestResultCollectionDao;

	public TestManagerServiceImpl(TreeTestResultDao treeTestResultDao,
			TestRunDescriptorDao testRunDescriptorDao,
			SlcExecutionDao slcExecutionDao,
			TreeTestResultCollectionDao treeTestResultCollectionDao) {
		this.treeTestResultDao = treeTestResultDao;
		this.testRunDescriptorDao = testRunDescriptorDao;
		this.slcExecutionDao = slcExecutionDao;
		this.treeTestResultCollectionDao = treeTestResultCollectionDao;
	}

	public void registerTestRunDescriptor(TestRunDescriptor testRunDescriptor) {
		if (testRunDescriptor != null) {
			testRunDescriptorDao.saveOrUpdate(testRunDescriptor);

			// Update tree test result collection
			// TODO: optimize

			if (testRunDescriptor.getSlcExecutionUuid() != null) {
				SlcExecution slcExecution = slcExecutionDao
						.getSlcExecution(testRunDescriptor
								.getSlcExecutionUuid());
				if (slcExecution != null) {
					String collectionId = slcExecution.getUser() != null ? slcExecution
							.getUser()
							: "default";
					addResultToCollection(collectionId, testRunDescriptor
							.getTestResultUuid());
				}
			}
		}
	}

	public void addResultToCollection(String collectionId, String resultUuid) {
		TreeTestResultCollection ttrc = treeTestResultCollectionDao
				.getTestResultCollection(collectionId);
		if (ttrc == null) {
			ttrc = new TreeTestResultCollection(collectionId);
			treeTestResultCollectionDao.create(ttrc);
		}
		treeTestResultCollectionDao.addResultToCollection(ttrc, resultUuid);
	}

	public void removeResultFromCollection(String collectionId,
			String resultUuid) {
		TreeTestResultCollection ttrc = treeTestResultCollectionDao
				.getTestResultCollection(collectionId);
		if (ttrc != null) {
			treeTestResultCollectionDao.removeResultFromCollection(ttrc,
					resultUuid);
		}
	}

}
