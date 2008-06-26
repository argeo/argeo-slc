package org.argeo.slc.services.test.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.core.test.TestRunDescriptor;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultCollection;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.dao.test.TestRunDescriptorDao;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.services.test.TestManagerService;

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
			if (log.isDebugEnabled())
				log.debug("Updating test run descriptor with id "
						+ testRunDescriptor.getTestRunUuid());

			testRunDescriptorDao.saveOrUpdate(testRunDescriptor);

			// Update tree test result collection
			// TODO: optimize

			if (testRunDescriptor.getSlcExecutionUuid() != null) {
				SlcExecution slcExecution = slcExecutionDao
						.getSlcExecution(testRunDescriptor
								.getSlcExecutionUuid());
				if (slcExecution != null) {
					addResultToCollection(slcExecution.getUser(),
							testRunDescriptor.getTestResultUuid());
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
		TreeTestResult ttr = treeTestResultDao.getTestResult(resultUuid);
		ttrc.getResults().add(ttr);
		treeTestResultCollectionDao.update(ttrc);
	}

	public void removeResultFromCollection(String collectionId,
			String resultUuid) {
		TreeTestResultCollection ttrc = treeTestResultCollectionDao
				.getTestResultCollection(collectionId);
		if (ttrc != null) {
			TreeTestResult ttr = treeTestResultDao.getTestResult(resultUuid);
			if (ttrc.getResults().remove(ttr)) {
				treeTestResultCollectionDao.update(ttrc);
			}
		}
	}

}
