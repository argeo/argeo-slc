package org.argeo.slc.jcr.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.attachment.SimpleAttachment;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.structure.StructureElement;
import org.argeo.slc.test.TestResult;

/**
 * The JCR implementation for tree-based result of the test result dao.
 * 
 * @see TreeTestResult
 */

public class TreeTestResultDaoJcr extends AbstractSlcJcrDao implements
		TreeTestResultDao {

	private final static Log log = LogFactory
			.getLog(TreeTestResultDaoJcr.class);

	public synchronized void create(TestResult testResult) {
		try {
			nodeMapper.save(getSession(), basePath(testResult), testResult);
			getSession().save();
		} catch (Exception e) {
			throw new SlcException("Cannot create testResult " + testResult, e);
		}
	}

	public synchronized void update(TestResult testResult) {
		try {
			nodeMapper.save(getSession(), basePath(testResult), testResult);
			getSession().save();
		} catch (Exception e) {
			throw new SlcException("Cannot update testResult" + testResult, e);
		}
	}

	public TreeTestResult getTestResult(String uuid) {

		try {
			String queryString = "//testresult[@uuid='" + uuid + "']";
			Query query = queryManager.createQuery(queryString, Query.XPATH);
			Node node = JcrUtils.querySingleNode(query);
			if (node == null)
				return null;
			return (TreeTestResult) nodeMapper.load(node);

		} catch (Exception e) {
			throw new SlcException("Cannot load TestResult with ID " + uuid, e);
		}

	}

	public List<TreeTestResult> listTestResults() {
		try {
			// TODO: optimize query
			String queryString = "//testresult";
			Query query = queryManager.createQuery(queryString, Query.XPATH);
			QueryResult queryResult = query.execute();
			NodeIterator nodeIterator = queryResult.getNodes();
			if (nodeIterator.hasNext()) {
				List<TreeTestResult> list = new ArrayList<TreeTestResult>();
				nodes: while (nodeIterator.hasNext()) {
					Node curNode = (Node) nodeIterator.next();

					// TODO improve architecture and get rid of this hack
					if ("slc".equals(curNode.getParent().getName()))
						continue nodes;

					list.add((TreeTestResult) nodeMapper.load(curNode));
				}
				return list;
			} else
				return null;

		} catch (Exception e) {
			throw new SlcException("Cannot load list of TestResult ", e);
		}
	}

	public List<TreeTestResult> listResults(TreeSPath path) {
		try {
			// TODO: optimize query
			String queryString = "//testresult" + path.getAsUniqueString();
			Query query = queryManager.createQuery(queryString, Query.XPATH);
			QueryResult queryResult = query.execute();
			NodeIterator nodeIterator = queryResult.getNodes();
			if (nodeIterator.hasNext()) {
				List<TreeTestResult> list = new ArrayList<TreeTestResult>();
				while (nodeIterator.hasNext()) {
					list.add((TreeTestResult) nodeMapper
							.load((Node) nodeIterator.next()));
				}
				return list;
			} else
				return null;

		} catch (Exception e) {
			throw new SlcException("Cannot load list of TestResult ", e);
		}
	}

	public synchronized void close(final String testResultId,
			final Date closeDate) {
		try {
			// TODO: optimize query
			String queryString = "//testresult[@uuid='" + testResultId + "']";
			Query query = queryManager.createQuery(queryString, Query.XPATH);
			Node resNode = JcrUtils.querySingleNode(query);
			Calendar cal = new GregorianCalendar();
			cal.setTime(closeDate);
			if (resNode != null)
				resNode.setProperty("closeDate", cal);
			else if (log.isDebugEnabled())
				log.debug("Cannot close because a node for test result # "
						+ testResultId + " was not found");
			getSession().save();
		} catch (Exception e) {
			throw new SlcException("Cannot close TestResult " + testResultId, e);
		}

	}

	/**
	 * Add a SimpleResultPart to the TreeTestResult of ID testResultId at
	 * treeSPath path
	 * 
	 * May also add some relatedElements
	 * 
	 */
	// TODO do we load objects, do treatment and persist them or do we work
	// directly in JCR
	public synchronized void addResultPart(final String testResultId,
			final TreeSPath path, final SimpleResultPart resultPart,
			final Map<TreeSPath, StructureElement> relatedElements) {

		try {
			// TODO: optimize query
			String queryString = "//testresult[@uuid='" + testResultId + "']";
			Query query = queryManager.createQuery(queryString, Query.XPATH);
			Node resNode = JcrUtils.querySingleNode(query);

			Node curNode;
			String usedPath = path.getAsUniqueString().substring(1)
					+ "/partsublist";

			if (resNode.hasNode(usedPath))
				curNode = resNode.getNode(usedPath);
			else {

				// TODO Factorize that
				Node tmpNode = resNode;
				String[] pathes = usedPath.split("/");
				for (int i = 0; i < pathes.length; i++) {
					if (tmpNode.hasNode(pathes[i]))
						tmpNode = tmpNode.getNode(pathes[i]);
					else
						tmpNode = tmpNode.addNode(pathes[i]);
				}
				curNode = tmpNode;
			}

			nodeMapper.update(curNode.addNode("resultPart"), resultPart);

			if (relatedElements != null) {
				for (TreeSPath key : relatedElements.keySet()) {
					String relPath = key.getAsUniqueString().substring(1);

					// check if already exists.
					if (!resNode.hasNode(relPath)) {

						// TODO Factorize that
						Node tmpNode = resNode;
						String[] pathes = usedPath.split("/");
						for (int i = 0; i < pathes.length; i++) {
							if (tmpNode.hasNode(pathes[i]))
								tmpNode = tmpNode.getNode(pathes[i]);
							else
								tmpNode = tmpNode.addNode(pathes[i]);
						}
						curNode = tmpNode;
					} else
						curNode = resNode.getNode(relPath);

					curNode.setProperty("label", relatedElements.get(key)
							.getLabel());
					// We add the tags
					Map<String, String> tags = relatedElements.get(key)
							.getTags();
					for (String tag : tags.keySet()) {
						String cleanTag = JcrUtils
								.removeForbiddenCharacters(tag);
						if (!cleanTag.equals(tag))
							log.warn("Tag '" + tag + "' persisted as '"
									+ cleanTag + "'");
						curNode.setProperty(cleanTag, tags.get(tag));
					}

					// We set the class in order to be able to retrieve
					curNode.setProperty("class", StructureElement.class
							.getName());
				}
			}
			getSession().save();

		} catch (Exception e) {
			throw new SlcException("Cannot add resultPart", e);
		}
	}

	public synchronized void addAttachment(final String testResultId,
			final SimpleAttachment attachment) {

		try {
			// TODO: optimize query
			// Might not be OK.
			// Do we have a notion of "currentNode" when we call JCRUtils one
			// more time.

			// Check if attachment already exists
			String queryString = "//testresult[@uuid='" + testResultId + "']";
			Query query = queryManager.createQuery(queryString, Query.XPATH);
			Node resNode = JcrUtils.querySingleNode(query);

			queryString = ".//*[@uuid='" + attachment.getUuid() + "']";
			query = queryManager.createQuery(queryString, Query.XPATH);
			Node atNode = JcrUtils.querySingleNode(query);

			if (atNode != null) {
				if (log.isDebugEnabled())
					log.debug("Attachement already There ");
			} else {
				if (resNode.hasNode("attachments"))
					atNode = resNode.getNode("attachments");
				else {
					atNode = resNode.addNode("attachments");
				}
				Node attachNode;
				attachNode = atNode.addNode(attachment.getName());
				attachNode.setProperty("uuid", attachment.getUuid());
				attachNode.setProperty("contentType", attachment
						.getContentType());
				getSession().save();
			}

		} catch (Exception e) {
			throw new SlcException("Cannot Add Attachment to " + testResultId,
					e);
		}
	}

	protected TreeTestResult getTreeTestResult(Session session,
			String testResultId) {
		try {
			String queryString = "//testresult[@uuid='" + testResultId + "']";
			QueryManager qm = session.getWorkspace().getQueryManager();
			Query query = qm.createQuery(queryString, Query.XPATH);
			Node node = JcrUtils.querySingleNode(query);
			if (node == null)
				return null;
			return (TreeTestResult) nodeMapper.load(node);

		} catch (Exception e) {
			throw new SlcException("Cannot load TestResult with ID "
					+ testResultId + " For Session " + session, e);
		}
	}

	public synchronized void updateAttributes(final String testResultId,
			final Map<String, String> attributes) {
		try {
			String queryString = "//testresult[@uuid='" + testResultId + "']";
			Query query = queryManager.createQuery(queryString, Query.XPATH);
			Node node = JcrUtils.querySingleNode(query);

			for (String key : attributes.keySet()) {
				node.setProperty(key, attributes.get(key));
			}
			getSession().save();
		} catch (Exception e) {
			throw new SlcException(
					"Cannot update Attributes on TestResult with ID "
							+ testResultId, e);
		}
	}

}
