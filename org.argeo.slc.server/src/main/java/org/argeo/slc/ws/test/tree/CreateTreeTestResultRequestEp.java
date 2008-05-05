package org.argeo.slc.ws.test.tree;

import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.msg.test.tree.CreateTreeTestResultRequest;

public class CreateTreeTestResultRequestEp extends
		AbstractMarshallingPayloadEndpoint {

	private Log log = LogFactory.getLog(getClass());

	private final TreeTestResultDao treeTestResultDao;

	public CreateTreeTestResultRequestEp(TreeTestResultDao treeTestResultDao) {
		this.treeTestResultDao = treeTestResultDao;
	}

	@Override
	protected Object invokeInternal(Object requestObject) throws Exception {
		CreateTreeTestResultRequest msg = (CreateTreeTestResultRequest) requestObject;
		TreeTestResult treeTestResult = msg.getTreeTestResult();

		if (log.isDebugEnabled())
			log.debug("Creating result with id " + treeTestResult.getUuid());

		treeTestResultDao.create(treeTestResult);

		return null;
	}

}
