package org.argeo.slc.services.impl.test;

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
import org.argeo.slc.services.test.TestManagerService;
import org.argeo.slc.test.TestRunDescriptor;

/** Implementation of complex operations impacting the underlying data. */
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
					String collectionId = slcExecution.getUser() != null ? slcExecution
							.getUser()
							: "default";
					addResultToCollection(collectionId, testRunDescriptor
							.getTestResultUuid());
				}
			} else {
				log
						.trace("ResultUUID="
								+ testRunDescriptor.getTestResultUuid());
				addResultToCollection("default", testRunDescriptor
						.getTestResultUuid());
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

}
