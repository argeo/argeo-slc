package org.argeo.slc.jcr;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.argeo.api.cms.CmsLog;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;
import org.argeo.slc.attachment.Attachment;
import org.argeo.slc.attachment.AttachmentsEnabled;
import org.argeo.slc.test.TestResult;
import org.argeo.slc.test.TestResultPart;
import org.argeo.slc.test.TestRun;
import org.argeo.slc.test.TestStatus;

/**
 * {@link TestResult} wrapping a JCR node of type
 * {@link SlcTypes#SLC_TEST_RESULT}.
 */
public class JcrTestResult implements TestResult, SlcNames, AttachmentsEnabled {
	private final static CmsLog log = CmsLog.getLog(JcrTestResult.class);

	/** Should only be set for an already existing result. */
	private String uuid;
	private Repository repository;
	private Session session;
	/**
	 * For testing purposes, best practice is to not set them explicitely but
	 * via other mechanisms such as JAAS or SPring Security.
	 */
	private Credentials credentials = null;
	private String resultType = SlcTypes.SLC_TEST_RESULT;

	/** cached for performance purposes */
	private String nodeIdentifier = null;

	private Map<String, String> attributes = new HashMap<String, String>();

	public void init() {
		try {
			session = repository.login(credentials);
			if (uuid == null) {
				// create new result
				uuid = UUID.randomUUID().toString();
				String path = SlcJcrUtils.createResultPath(session, uuid);
				Node resultNode = JcrUtils.mkdirs(session, path, resultType);
				resultNode.setProperty(SLC_UUID, uuid);
				for (String attr : attributes.keySet()) {
					String property = attr;
					// compatibility with legacy applications
					if ("testCase".equals(attr))
						property = SLC_TEST_CASE;
					else if ("testCaseType".equals(attr))
						property = SLC_TEST_CASE_TYPE;
					resultNode.setProperty(property, attributes.get(attr));
				}
				session.save();
				if (log.isDebugEnabled())
					log.debug("Created test result " + uuid);
			}
		} catch (Exception e) {
			JcrUtils.discardQuietly(session);
			throw new SlcException("Cannot initialize JCR result", e);
		}
	}

	public void destroy() {
		JcrUtils.logoutQuietly(session);
		if (log.isTraceEnabled())
			log.trace("Logged out session for result " + uuid);
	}

	public Node getNode() {
		try {
			Node resultNode;
			if (nodeIdentifier != null) {
				return session.getNodeByIdentifier(nodeIdentifier);
			} else {
				QueryManager qm = session.getWorkspace().getQueryManager();
				Query q = qm.createQuery("select * from ["
						+ SlcTypes.SLC_TEST_RESULT + "] where [slc:uuid]='"
						+ uuid + "'", Query.JCR_SQL2);
				resultNode = JcrUtils.querySingleNode(q);
				if (resultNode != null)
					nodeIdentifier = resultNode.getIdentifier();
			}
			return resultNode;
		} catch (Exception e) {
			throw new SlcException("Cannot get result node", e);
		}
	}

	public void notifyTestRun(TestRun testRun) {
		// TODO store meta data about the test running
		// if (log.isDebugEnabled())
		// log.debug("Running test "
		// + testRun.getTestDefinition().getClass().getName() + "...");
	}

	public void addResultPart(TestResultPart testResultPart) {
		Node node = getNode();

		try {
			// error : revert all unsaved changes on the resultNode to be sure
			// it is in a consistant state
			if (testResultPart.getExceptionMessage() != null)
				JcrUtils.discardQuietly(node.getSession());
			node.getSession().save();

			// add the new result part, retrieving status information
			Node resultPartNode = node.addNode(SlcNames.SLC_RESULT_PART,
					SlcTypes.SLC_CHECK);
			resultPartNode.setProperty(SLC_SUCCESS, testResultPart.getStatus()
					.equals(TestStatus.PASSED));
			if (testResultPart.getMessage() != null)
				resultPartNode.setProperty(SLC_MESSAGE,
						testResultPart.getMessage());
			if (testResultPart.getStatus().equals(TestStatus.ERROR)) {
				resultPartNode.setProperty(SLC_ERROR_MESSAGE,
						(testResultPart.getExceptionMessage() == null) ? ""
								: testResultPart.getExceptionMessage());
			}

			// helper update aggregate status node
			Node mainStatus;
			if (!node.hasNode(SLC_AGGREGATED_STATUS)) {

				mainStatus = node.addNode(SLC_AGGREGATED_STATUS,
						SlcTypes.SLC_CHECK);
				mainStatus.setProperty(SLC_SUCCESS,
						resultPartNode.getProperty(SLC_SUCCESS).getBoolean());
				if (resultPartNode.hasProperty(SLC_MESSAGE))
					mainStatus.setProperty(SLC_MESSAGE, resultPartNode
							.getProperty(SLC_MESSAGE).getString());
				if (resultPartNode.hasProperty(SLC_ERROR_MESSAGE))
					mainStatus.setProperty(SLC_ERROR_MESSAGE, resultPartNode
							.getProperty(SLC_ERROR_MESSAGE).getString());
			} else {
				mainStatus = node.getNode(SLC_AGGREGATED_STATUS);
				if (mainStatus.hasProperty(SLC_ERROR_MESSAGE)) {
					// main status already in error we do nothing
				} else if (resultPartNode.hasProperty(SLC_ERROR_MESSAGE)) {
					// main status was not in error and new result part is in
					// error; we update main status
					mainStatus.setProperty(SLC_SUCCESS, false);
					mainStatus.setProperty(SLC_ERROR_MESSAGE, resultPartNode
							.getProperty(SLC_ERROR_MESSAGE).getString());
					if (resultPartNode.hasProperty(SLC_MESSAGE))
						mainStatus.setProperty(SLC_MESSAGE, resultPartNode
								.getProperty(SLC_MESSAGE).getString());
					else
						// remove old message to remain consistent
						mainStatus.setProperty(SLC_MESSAGE, "");
				} else if (!mainStatus.getProperty(SLC_SUCCESS).getBoolean()) {
					// main status was already failed and new result part is not
					// in error, we do nothing
				} else if (!resultPartNode.getProperty(SLC_SUCCESS)
						.getBoolean()) {
					// new resultPart that is failed
					mainStatus.setProperty(SLC_SUCCESS, false);
					if (resultPartNode.hasProperty(SLC_MESSAGE))
						mainStatus.setProperty(SLC_MESSAGE, resultPartNode
								.getProperty(SLC_MESSAGE).getString());
					else
						// remove old message to remain consistent
						mainStatus.setProperty(SLC_MESSAGE, "");
				} else if (resultPartNode.hasProperty(SLC_MESSAGE)
						&& (!mainStatus.hasProperty(SLC_MESSAGE) || (""
								.equals(mainStatus.getProperty(SLC_MESSAGE)
										.getString().trim())))) {
					mainStatus.setProperty(SLC_MESSAGE, resultPartNode
							.getProperty(SLC_MESSAGE).getString());
				}
			}
			JcrUtils.updateLastModified(node);
			node.getSession().save();
		} catch (Exception e) {
			JcrUtils.discardUnderlyingSessionQuietly(node);
			throw new SlcException("Cannot add ResultPart to node " + node, e);
		}
	}

	public String getUuid() {
		Node node = getNode();
		try {
			return node.getProperty(SLC_UUID).getString();
		} catch (Exception e) {
			throw new SlcException("Cannot get UUID from " + node, e);
		}
	}

	/** JCR session is NOT logged out */
	public void close() {
		Node node = getNode();
		try {
			if (node.hasNode(SLC_COMPLETED))
				return;
			node.setProperty(SLC_COMPLETED, new GregorianCalendar());
			JcrUtils.updateLastModified(node);
			node.getSession().save();
		} catch (Exception e) {
			JcrUtils.discardUnderlyingSessionQuietly(node);
			throw new SlcException("Cannot get close date from " + node, e);
		}
	}

	public Date getCloseDate() {
		Node node = getNode();
		try {
			if (!node.hasNode(SLC_COMPLETED))
				return null;
			return node.getProperty(SLC_COMPLETED).getDate().getTime();
		} catch (Exception e) {
			throw new SlcException("Cannot get close date from " + node, e);
		}
	}

	public Map<String, String> getAttributes() {
		Node node = getNode();
		try {
			Map<String, String> map = new HashMap<String, String>();
			PropertyIterator pit = node.getProperties();
			while (pit.hasNext()) {
				Property p = pit.nextProperty();
				if (!p.isMultiple())
					map.put(p.getName(), p.getValue().getString());
			}
			return map;
		} catch (Exception e) {
			throw new SlcException("Cannot get close date from " + node, e);
		}
	}

	public void addAttachment(Attachment attachment) {
		// TODO implement it
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	public void setAttributes(Map<String, String> attributes) {
		if (uuid != null)
			throw new SlcException(
					"Attributes cannot be set on an already initialized test result."
							+ " Update the related JCR node directly instead.");
		this.attributes = attributes;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}
}
