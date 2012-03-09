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
package org.argeo.slc.services.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultCollection;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.dao.test.TestRunDescriptorDao;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.msg.test.tree.AddTreeTestResultAttachmentRequest;
import org.argeo.slc.msg.test.tree.CloseTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.CreateTreeTestResultRequest;
import org.argeo.slc.msg.test.tree.ResultPartRequest;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.services.TestManagerService;
import org.argeo.slc.test.TestRunDescriptor;

/**
 * Implementation of complex operations impacting the underlying data.
 */
public class TestManagerServiceImpl implements TestManagerService {
	private Log log = LogFactory.getLog(getClass());

	private final TreeTestResultDao treeTestResultDao;
	private final TestRunDescriptorDao testRunDescriptorDao;
	private final SlcExecutionDao slcExecutionDao;
	private final TreeTestResultCollectionDao treeTestResultCollectionDao;

	private String defaultCollectionId = "default";
	
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
			if (log.isTraceEnabled())
				log.trace("Registering test run descriptor #"
						+ testRunDescriptor.getTestRunUuid());
			testRunDescriptorDao.saveOrUpdate(testRunDescriptor);

			// Update tree test result collection
			// TODO: optimize

			if (testRunDescriptor.getSlcExecutionUuid() != null) {
				SlcExecution slcExecution = slcExecutionDao
						.getSlcExecution(testRunDescriptor
								.getSlcExecutionUuid());
				if (slcExecution != null) {
					// Use Host as collection ID if host is available
					String collectionId = slcExecution.getHost() != null ? slcExecution
							.getHost()
							: defaultCollectionId;
					addResultToCollection(collectionId, testRunDescriptor
							.getTestResultUuid());
				}
			} else {
				if (log.isTraceEnabled())
					log.trace("ResultUUID="
							+ testRunDescriptor.getTestResultUuid());
				addResultToCollection(defaultCollectionId, testRunDescriptor
						.getTestResultUuid());
			}
		}
	}

	public void addResultToCollection(String collectionId, String resultUuid) {
		// TODO: improve collections
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

		// Delete collection if empty
		// see https://www.argeo.org/bugzilla/show_bug.cgi?id=74
		if (ttrc.getResults().size() == 0) {
			treeTestResultCollectionDao.delete(ttrc);
		}
	}

	public void createTreeTestResult(CreateTreeTestResultRequest msg) {
		TreeTestResult treeTestResult = msg.getTreeTestResult();

		if (log.isTraceEnabled())
			log.trace("Creating result #" + treeTestResult.getUuid());
		treeTestResultDao.create(treeTestResult);

		registerTestRunDescriptor(msg.getTestRunDescriptor());

		// FIXME: temporary hack before better collection management is found
		if (msg.getTestRunDescriptor() == null) {
			addResultToCollection("default", treeTestResult.getUuid());
		}
	}

	public void addResultPart(ResultPartRequest msg) {
		// registerTestRunDescriptor(msg.getTestRunDescriptor());

		if (log.isTraceEnabled())
			log.trace("Adding result part to test result #"
					+ msg.getResultUuid());

		treeTestResultDao.addResultPart(msg.getResultUuid(), msg.getPath(), msg
				.getResultPart(), msg.getRelatedElements());
		// treeTestResultDao.updateAttributes(msg.getResultUuid(), msg
		// .getAttributes());
	}

	public void closeTreeTestResult(CloseTreeTestResultRequest msg) {
		if (log.isTraceEnabled())
			log.trace("Closing result #" + msg.getResultUuid() + " at date "
					+ msg.getCloseDate());

		treeTestResultDao.close(msg.getResultUuid(), msg.getCloseDate());
	}

	public void addAttachment(AddTreeTestResultAttachmentRequest msg) {
		if (log.isTraceEnabled())
			log.trace("Adding attachment " + msg.getAttachment()
					+ " to result #" + msg.getResultUuid());
		treeTestResultDao.addAttachment(msg.getResultUuid(), msg
				.getAttachment());

	}

	public void setDefaultCollectionId(String defaultCollectionId) {
		this.defaultCollectionId = defaultCollectionId;
	}

}
