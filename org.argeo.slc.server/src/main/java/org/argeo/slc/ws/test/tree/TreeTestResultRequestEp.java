package org.argeo.slc.ws.test.tree;

import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.msg.test.tree.TreeTestResultRequest;

public class TreeTestResultRequestEp extends AbstractMarshallingPayloadEndpoint {

	private Log log = LogFactory.getLog(getClass());

	private final TreeTestResultDao treeTestResultDao;

	public TreeTestResultRequestEp(TreeTestResultDao treeTestResultDao) {
		this.treeTestResultDao = treeTestResultDao;
	}

	@Override
	protected Object invokeInternal(Object requestObject) throws Exception {
		TreeTestResultRequest msg = (TreeTestResultRequest) requestObject;
		TreeTestResult treeTestResult = msg.getTreeTestResult();

		if (treeTestResultDao.getTestResult(treeTestResult.getUuid()) == null) {
			treeTestResultDao.create(treeTestResult);
			log.debug("Created TreeTestResult with uuid "
					+ treeTestResult.getUuid());
		} else {
			treeTestResultDao.update(treeTestResult);
			log.debug("Updated TreeTestResult with uuid "
					+ treeTestResult.getUuid());
		}
		return null;
	}

}
