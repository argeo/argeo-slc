package org.argeo.slc.ws.test.tree;

import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.msg.test.tree.CloseTreeTestResultRequest;

public class CloseTreeTestResultRequestEp extends
		AbstractMarshallingPayloadEndpoint {

	private Log log = LogFactory.getLog(getClass());

	private final TreeTestResultDao treeTestResultDao;

	public CloseTreeTestResultRequestEp(TreeTestResultDao treeTestResultDao) {
		this.treeTestResultDao = treeTestResultDao;
	}

	@Override
	protected Object invokeInternal(Object requestObject) throws Exception {
		CloseTreeTestResultRequest msg = (CloseTreeTestResultRequest) requestObject;

		if (log.isDebugEnabled())
			log.debug("Closing result with id " + msg.getResultUuid()
					+ " at date " + msg.getCloseDate());

		treeTestResultDao.close(msg.getResultUuid(), msg.getCloseDate());

		return null;
	}

}
