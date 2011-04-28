package org.argeo.slc.jcr.execution;

import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.core.attachment.Attachment;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultListener;
import org.argeo.slc.jcr.SlcJcrUtils;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.argeo.slc.test.TestResultPart;
import org.argeo.slc.test.TestStatus;

/** Persists results in JCR */
public class JcrResultListener implements TreeTestResultListener, SlcNames {
	private final static Log log = LogFactory.getLog(JcrResultListener.class);

	private Session session;

	/** Caches the mapping between SLC uuids and internal JCR identifiers */
	private Map<String, String> uuidToIdentifier = Collections
			.synchronizedMap(new HashMap<String, String>());

	public void resultPartAdded(TreeTestResult testResult,
			TestResultPart testResultPart) {
		try {
			String uuid = testResult.getUuid();
			Node resultNode = getResultNode(uuid);
			if (resultNode == null) {
				resultNode = createResultNode(testResult);
				// session.save();
			}
			String partParentPath;
			TreeSPath currentPath = testResult.getCurrentPath();
			if (currentPath != null) {
				String subPath = currentPath.getAsUniqueString();
				partParentPath = resultNode.getPath() + subPath;
			} else {
				partParentPath = resultNode.getPath();
				// TODO create some depth?
			}

			Node partParentNode;
			if (session.itemExists(partParentPath)) {
				partParentNode = session.getNode(partParentPath);
			} else {
				partParentNode = JcrUtils.mkdirs(session, partParentPath);
				// session.save();
			}
			// create part node
			// TODO find a better name
			String partNodeName = Long.toString(System.currentTimeMillis());
			Node resultPartNode = partParentNode.addNode(partNodeName,
					SlcTypes.SLC_CHECK);
			resultPartNode.setProperty(SLC_SUCCESS,
					testResultPart.getStatus() == TestStatus.PASSED);
			if (testResultPart.getMessage() != null)
				resultPartNode.setProperty(SLC_MESSAGE,
						testResultPart.getMessage());
			if (testResultPart.getExceptionMessage() != null)
				resultPartNode.setProperty(SLC_ERROR_MESSAGE,
						testResultPart.getExceptionMessage());
			// JcrUtils.debug(resultPartNode);
			
			JcrUtils.updateLastModified(resultNode);
			
			session.save();
		} catch (RepositoryException e) {
			JcrUtils.discardQuietly(session);
			log.error("Cannot add result part " + testResultPart + " to "
					+ testResult, e);
			// throw new SlcException("Cannot add result part " + testResultPart
			// + " to " + testResult, e);
		}

	}

	/** @return null if does not exist */
	protected Node getResultNode(String uuid) throws RepositoryException {
		Node resultNode;
		if (uuidToIdentifier.containsKey(uuid)) {
			return session.getNodeByIdentifier(uuidToIdentifier.get(uuid));
		} else {
			Query q = session
					.getWorkspace()
					.getQueryManager()
					.createQuery(
							"select * from [slc:result] where [slc:uuid]='"
									+ uuid + "'", Query.JCR_SQL2);
			resultNode = JcrUtils.querySingleNode(q);
			if (resultNode != null)
				uuidToIdentifier.put(uuid, resultNode.getIdentifier());
		}
		return resultNode;
	}

	protected Node createResultNode(TreeTestResult testResult)
			throws RepositoryException {
		String uuid = testResult.getUuid();
		String path = SlcJcrUtils.createResultPath(uuid);
		Node resultNode = JcrUtils.mkdirs(session, path, SlcTypes.SLC_RESULT);
		resultNode.setProperty(SLC_UUID, uuid);
		for (Map.Entry<String, String> entry : testResult.getAttributes()
				.entrySet()) {
			resultNode.setProperty(entry.getKey(), entry.getValue());
		}

		uuidToIdentifier.put(uuid, resultNode.getIdentifier());
		return resultNode;
	}

	public void close(TreeTestResult testResult) {
		try {
			String uuid = testResult.getUuid();
			Node resultNode = getResultNode(uuid);
			if (resultNode == null)
				resultNode = createResultNode(testResult);
			JcrUtils.updateLastModified(resultNode);
			GregorianCalendar closeDate = new GregorianCalendar();
			closeDate.setTime(testResult.getCloseDate());
			resultNode.setProperty(SLC_COMPLETED, closeDate);

			uuidToIdentifier.remove(uuid);
			session.save();
		} catch (RepositoryException e) {
			JcrUtils.discardQuietly(session);
			log.error("Cannot close result " + testResult, e);
			// throw new SlcException("Cannot close result " + testResult, e);
		}

	}

	public void addAttachment(TreeTestResult testResult, Attachment attachment) {

	}

	public void setSession(Session session) {
		this.session = session;
	}

}
