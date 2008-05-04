package org.argeo.slc.core.test.tree;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.test.TestResultListener;
import org.argeo.slc.core.test.TestResultPart;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;

/**
 * Listener persisting tree-based results.
 * 
 * @see TreeTestResult
 */
public class TreeTestResultPersister implements
		TestResultListener<TreeTestResult> {
	private static Log log = LogFactory.getLog(TreeTestResultPersister.class);

	private TreeTestResultDao testResultDao;

	public void resultPartAdded(TreeTestResult testResult,
			TestResultPart testResultPart) {
		try {
			TreeTestResult persistedResult = testResultDao
					.getTestResult(testResult.getUuid());

			if (persistedResult == null) {
				testResultDao.create(testResult);
			} else {
				testResultDao.update(testResult);
			}
		} catch (Exception e) {
			log.error("Could not persist result part " + testResultPart
					+ " for result " + testResult.getUuid());
		}
	}

	public void close(TreeTestResult testResult) {
		TreeTestResult persistedResult = (TreeTestResult) testResultDao
				.getTestResult(testResult.getUuid());

		if (persistedResult != null) {
			persistedResult.setCloseDate(testResult.getCloseDate());
			testResultDao.update(persistedResult);
		}
		if (log.isDebugEnabled())
			log.debug("Closed result persister for result "
					+ testResult.getUuid());
	}

	/** Sets the DAO to use in order to persist the results. */
	public void setTestResultDao(TreeTestResultDao testResultDao) {
		this.testResultDao = testResultDao;
	}
}
