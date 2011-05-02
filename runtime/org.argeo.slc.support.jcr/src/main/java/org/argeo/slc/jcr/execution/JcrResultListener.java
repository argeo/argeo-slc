package org.argeo.slc.jcr.execution;

import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.attachment.Attachment;
import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.tree.PartSubList;
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
			SimpleSElement element = null;
			if (testResult.getElements().containsKey(currentPath)) {
				element = (SimpleSElement) testResult.getElements().get(
						currentPath);
			}

			String elementLabel = element != null && element.getLabel() != null
					&& !element.getLabel().trim().equals("") ? element
					.getLabel() : null;
			String partNodeName = elementLabel != null ? JcrUtils
					.replaceInvalidChars(elementLabel, '_') : Long
					.toString(System.currentTimeMillis());

			Node resultPartNode = partParentNode.addNode(partNodeName,
					SlcTypes.SLC_CHECK);
			resultPartNode.setProperty(SLC_SUCCESS,
					testResultPart.getStatus() == TestStatus.PASSED);
			if (elementLabel != null)
				resultPartNode.setProperty(Property.JCR_TITLE, elementLabel);
			if (testResultPart.getMessage() != null)
				resultPartNode.setProperty(SLC_MESSAGE,
						testResultPart.getMessage());
			if (testResultPart.getExceptionMessage() != null)
				resultPartNode.setProperty(SLC_ERROR_MESSAGE,
						testResultPart.getExceptionMessage());
			// JcrUtils.debug(resultPartNode);

			JcrUtils.updateLastModified(resultNode);

			if (element != null) {
				element = (SimpleSElement) testResult.getElements().get(
						currentPath);
				if (log.isTraceEnabled())
					log.trace("  Path= " + currentPath + ", part="
							+ testResultPart.getMessage());
				for (Map.Entry<String, String> tag : element.getTags()
						.entrySet()) {
					String tagNodeName = JcrUtils.replaceInvalidChars(
							tag.getKey(), '_');
					// log.debug("key=" + tag.getKey() + ", tagNodeName="
					// + tagNodeName);
					Node tagNode = resultPartNode.addNode(tagNodeName,
							SlcTypes.SLC_PROPERTY);
					tagNode.setProperty(SLC_NAME, tag.getKey());
					tagNode.setProperty(SLC_VALUE, tag.getValue());
				}
			}

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

	/**
	 * Creates and populates a {@link TreeTestResult} from the related result
	 * node. Meant to simplify migration of legacy applications. This is no
	 * stable API.
	 */
	public static TreeTestResult nodeToTreeTestResult(Node resultNode) {
		try {
			String resultPath = resultNode.getPath();
			TreeTestResult ttr = new TreeTestResult();
			// base properties
			ttr.setUuid(resultNode.getProperty(SLC_UUID).getString());
			if (resultNode.hasProperty(SLC_COMPLETED))
				ttr.setCloseDate(resultNode.getProperty(SLC_COMPLETED)
						.getDate().getTime());
			// attributes
			for (PropertyIterator pit = resultNode.getProperties(); pit
					.hasNext();) {
				Property p = pit.nextProperty();
				if (p.getName().indexOf(':') < 0) {
					ttr.getAttributes().put(p.getName(), p.getString());
				}
			}

			QueryManager qm = resultNode.getSession().getWorkspace()
					.getQueryManager();
			String statement = "SELECT * FROM [" + SlcTypes.SLC_CHECK
					+ "] WHERE ISDESCENDANTNODE(['" + resultPath + "'])";
			NodeIterator nit = qm.createQuery(statement, Query.JCR_SQL2)
					.execute().getNodes();
			while (nit.hasNext()) {
				Node checkNode = nit.nextNode();
				String relPath = checkNode.getPath().substring(
						resultPath.length());
				TreeSPath tsp = new TreeSPath(relPath);

				// result part
				SimpleResultPart srp = new SimpleResultPart();
				if (checkNode.getProperty(SLC_SUCCESS).getBoolean())
					srp.setStatus(TestStatus.PASSED);
				else if (checkNode.hasProperty(SLC_ERROR_MESSAGE))
					srp.setStatus(TestStatus.ERROR);
				else
					srp.setStatus(TestStatus.FAILED);
				if (checkNode.hasProperty(SLC_MESSAGE))
					srp.setMessage(checkNode.getProperty(SLC_MESSAGE)
							.getString());
				if (!ttr.getResultParts().containsKey(tsp))
					ttr.getResultParts().put(tsp, new PartSubList());
				ttr.getResultParts().get(tsp).getParts().add(srp);

				// element
				SimpleSElement elem = new SimpleSElement();
				if (checkNode.hasProperty(Property.JCR_TITLE))
					elem.setLabel(checkNode.getProperty(Property.JCR_TITLE)
							.getString());
				else
					elem.setLabel("");// some legacy code expect it to be set
				for (NodeIterator tagIt = checkNode.getNodes(); tagIt.hasNext();) {
					Node tagNode = tagIt.nextNode();
					elem.getTags().put(
							tagNode.getProperty(SLC_NAME).getString(),
							tagNode.getProperty(SLC_VALUE).getString());
				}
				ttr.getElements().put(tsp, elem);
			}
			return ttr;
		} catch (RepositoryException e) {
			throw new SlcException("Cannot generate tree test result from "
					+ resultNode, e);
		}
	}
}
