/*
 * Copyright (C) 2007-2012 Mathieu Baudier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.dao.test.tree;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.test.TestResultListener;
import org.argeo.slc.test.TestResultPart;

/**
 * Listener persisting tree-based results.
 * 
 * @see TreeTestResult
 */
@Deprecated
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
