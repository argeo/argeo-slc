package org.argeo.slc.ws.test.tree;

import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.msg.test.tree.CreateTreeTestResultRequest;
import org.argeo.slc.services.test.TestManagerService;

public class CreateTreeTestResultRequestEp extends
		AbstractMarshallingPayloadEndpoint {

	private Log log = LogFactory.getLog(getClass());

	private final TreeTestResultDao treeTestResultDao;
	private final TestManagerService testManagerService;

	public CreateTreeTestResultRequestEp(TreeTestResultDao treeTestResultDao,
			TestManagerService testManagerService) {
		this.treeTestResultDao = treeTestResultDao;
		this.testManagerService = testManagerService;
	}

	@Override
	protected Object invokeInternal(Object requestObject) throws Exception {
		CreateTreeTestResultRequest msg = (CreateTreeTestResultRequest) requestObject;
		TreeTestResult treeTestResult = msg.getTreeTestResult();

		if (log.isTraceEnabled())
			log.trace("Creating result #" + treeTestResult.getUuid());
		treeTestResultDao.create(treeTestResult);

		if (log.isTraceEnabled())
			log.trace("Registering test run descriptor #"
					+ msg.getTestRunDescriptor().getTestRunUuid());
		testManagerService
				.registerTestRunDescriptor(msg.getTestRunDescriptor());

		return null;
	}

}
