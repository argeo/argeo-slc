package org.argeo.slc.ws.test.tree;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.test.tree.PartSubList;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.msg.test.tree.ResultPartRequest;
import org.argeo.slc.services.test.TestManagerService;
import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

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

		treeTestResultDao.addResultPart(msg.getResultUuid(), msg.getPath(), msg
				.getResultPart(), msg.getRelatedElements());

		if (log.isDebugEnabled())
			log.debug("Added result part to test result #"
					+ msg.getResultUuid());

		return null;
	}

}
