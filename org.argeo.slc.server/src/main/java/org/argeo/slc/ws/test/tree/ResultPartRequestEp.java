package org.argeo.slc.ws.test.tree;

import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.test.TestRunDescriptor;
import org.argeo.slc.core.test.tree.PartSubList;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.TestRunDescriptorDao;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.msg.test.tree.ResultPartRequest;

public class ResultPartRequestEp extends AbstractMarshallingPayloadEndpoint {

	private Log log = LogFactory.getLog(getClass());

	private final TreeTestResultDao treeTestResultDao;
	private final TestRunDescriptorDao testRunDescriptorDao;

	public ResultPartRequestEp(TreeTestResultDao treeTestResultDao,
			TestRunDescriptorDao testRunDescriptorDao) {
		this.treeTestResultDao = treeTestResultDao;
		this.testRunDescriptorDao = testRunDescriptorDao;
	}

	@Override
	protected Object invokeInternal(Object requestObject) throws Exception {
		ResultPartRequest msg = (ResultPartRequest) requestObject;

		TestRunDescriptor testRunDescriptor = msg.getTestRunDescriptor();
		if (testRunDescriptor != null) {
			testRunDescriptorDao.saveOrUpdate(testRunDescriptor);
			if (log.isDebugEnabled())
				log.debug("Updated test run descriptor with id "
						+ testRunDescriptor.getTestRunUuid());
		}

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

		treeTestResultDao.update(treeTestResult);

		if (log.isDebugEnabled())
			log.debug("Update result with id " + treeTestResult.getUuid());
		return null;
	}

}
