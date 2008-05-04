package org.argeo.slc.ws.test.tree;

import org.springframework.ws.server.endpoint.AbstractMarshallingPayloadEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.test.tree.PartSubList;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.msg.test.tree.ResultPartRequest;

public class ResultPartRequestEp extends AbstractMarshallingPayloadEndpoint {

	private Log log = LogFactory.getLog(getClass());

	private final TreeTestResultDao treeTestResultDao;

	public ResultPartRequestEp(TreeTestResultDao treeTestResultDao) {
		this.treeTestResultDao = treeTestResultDao;
	}

	@Override
	protected Object invokeInternal(Object requestObject) throws Exception {
		ResultPartRequest msg = (ResultPartRequest) requestObject;
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
