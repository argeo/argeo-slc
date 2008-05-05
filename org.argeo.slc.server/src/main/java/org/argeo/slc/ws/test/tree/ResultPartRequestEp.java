package org.argeo.slc.ws.test.tree;

import javax.swing.plaf.basic.BasicTreeUI.TreeTraverseAction;

import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.core.test.TestRunDescriptor;
import org.argeo.slc.core.test.tree.PartSubList;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultCollection;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.dao.test.TestRunDescriptorDao;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.msg.test.tree.ResultPartRequest;
import org.argeo.slc.services.test.TestManagerService;
import org.argeo.slc.services.test.impl.TestManagerServiceImpl;

public class ResultPartRequestEp extends AbstractMarshallingPayloadEndpoint {

	private Log log = LogFactory.getLog(getClass());

	private final TreeTestResultDao treeTestResultDao;
	private final TestManagerService testManagerService;

	public ResultPartRequestEp(TreeTestResultDao treeTestResultDao,
			TestManagerService testManagerService) {
		this.treeTestResultDao = treeTestResultDao;
		this.testManagerService = testManagerService;
	}

	@Override
	protected Object invokeInternal(Object requestObject) throws Exception {
		ResultPartRequest msg = (ResultPartRequest) requestObject;

		testManagerService
				.registerTestRunDescriptor(msg.getTestRunDescriptor());
		// if (testRunDescriptor != null) {
		// if (log.isDebugEnabled())
		// log.debug("Updating test run descriptor with id "
		// + testRunDescriptor.getTestRunUuid());
		//
		// testRunDescriptorDao.saveOrUpdate(testRunDescriptor);
		//
		// // Update tree test result collection
		// // TODO: put it in dedicated service and optimize
		// SlcExecution slcExecution = slcExecutionDao
		// .getSlcExecution(testRunDescriptor.getSlcExecutionUuid());
		// if (slcExecution != null) {
		// TreeTestResultCollection ttrc = treeTestResultCollectionDao
		// .getTestResultCollection(slcExecution.getUser());
		// if (ttrc == null) {
		// ttrc = new TreeTestResultCollection(slcExecution.getUser());
		// treeTestResultCollectionDao.create(ttrc);
		// }
		// TreeTestResult ttr = treeTestResultDao
		// .getTestResult(testRunDescriptor.getTestResultUuid());
		// ttrc.getResults().add(ttr);
		// treeTestResultCollectionDao.update(ttrc);
		// }
		// }

		TreeTestResult treeTestResult = treeTestResultDao.getTestResult(msg
				.getResultUuid());
		if (treeTestResult == null) {
			throw new SlcException("No result with id " + msg.getResultUuid());
		}

		PartSubList lst = treeTestResult.getResultParts().get(msg.getPath());
		if (lst == null) {
			lst = new PartSubList();
			treeTestResult.getResultParts().put(msg.getPath(), lst);
		}
		lst.getParts().add(msg.getResultPart());
		treeTestResult.getElements().putAll(msg.getRelatedElements());

		if (log.isDebugEnabled())
			log.debug("Updating result with id " + treeTestResult.getUuid());

		treeTestResultDao.update(treeTestResult);

		return null;
	}

}
