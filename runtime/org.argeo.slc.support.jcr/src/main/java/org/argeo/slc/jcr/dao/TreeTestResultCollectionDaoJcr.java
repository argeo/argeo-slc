package org.argeo.slc.jcr.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.query.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.test.tree.ResultAttributes;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultCollection;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;

/** JCR implementation of collections DAO. */
public class TreeTestResultCollectionDaoJcr extends AbstractSlcJcrDao implements
		TreeTestResultCollectionDao {

	// FIXME : we handle testResultCollection by adding a property called
	// "TestResultCollectionId "
	final private String ttrColProp = "TestResultCollectionId";

	private final static Log log = LogFactory
			.getLog(TreeTestResultCollectionDaoJcr.class);

	public void create(TreeTestResultCollection ttrCollection) {
		try {
			Node curNode;
			String colId = ttrCollection.getId();
			for (TreeTestResult ttr : ttrCollection.getResults()) {
				curNode = nodeMapper.save(getSession(), basePath(ttr), ttr);
				curNode.setProperty(ttrColProp, colId);
			}
			getSession().save();
		} catch (Exception e) {
			throw new SlcException("Cannot create TreeTestResultCollection "
					+ ttrCollection, e);
		}
	}

	public TreeTestResultCollection getTestResultCollection(String id) {
		TreeTestResultCollection res = new TreeTestResultCollection();
		res.setId(id);

		// String queryString = "//*[@" + ttrColProp + "='" + id + "']";
		// Query query = queryManager.createQuery(queryString, Query.XPATH);
		// log.debug("retrieving all nodes of a col - " + queryString);
		int i = 0;
		NodeIterator ni = resultNodesInCollection(id);
		while (ni.hasNext()) {
			i++;
			res.getResults().add(
					(TreeTestResult) nodeMapper.load(ni.nextNode()));
		}
		// log.debug(i + " nodes found");
		return res;
	}

	/**
	 * 
	 * FIXME : validate what this method must really do ? what happen if one of
	 * the TreeTestResult of the collection is not found in the jcr repository?
	 * Now we create ttr that are not found and update existing ones.
	 * FurtherMore if a TreeTestResult is persisted as member the collection but
	 * is not in the object passed, it is removed.
	 */
	public void update(TreeTestResultCollection ttrCollection) {
		try {
			log.debug("Update ");
			String queryString;
			Query query;
			Node curNode;
			String colId = ttrCollection.getId();
			// We add or update existing ones
			for (TreeTestResult ttr : ttrCollection.getResults()) {
				queryString = "//*[@uuid='" + ttr.getUuid() + "']";
				query = queryManager.createQuery(queryString, Query.XPATH);
				curNode = JcrUtils.querySingleNode(query);
				if (curNode == null) {
					curNode = nodeMapper.save(getSession(), basePath(ttr), ttr);
					log.debug("New Node added");
				} else {
					nodeMapper.update(curNode, ttr);
					log.debug("Node updated");
				}
				log
						.debug("-----------------------------------------------------------------");
				curNode.setProperty(ttrColProp, colId);
				JcrUtils.debug(curNode.getSession().getRootNode());
			}
			// We remove those who are not part of the collection anymore
			queryString = "//*[@" + ttrColProp + "='" + colId + "']";
			query = queryManager.createQuery(queryString, Query.XPATH);
			log.debug("Query :" + queryString);
			NodeIterator ni = query.execute().getNodes();
			int i = 0;
			while (ni.hasNext()) {
				log.debug("Node " + (++i));
				curNode = ni.nextNode();
				String uuid = curNode.getProperty("uuid").getString();
				boolean isPartOfTheSet = false;
				for (TreeTestResult ttr : ttrCollection.getResults()) {
					if (uuid.equals(ttr.getUuid())) {
						isPartOfTheSet = true;
						log.debug("Node " + i + " found");
						break;
					}
				}
				if (!isPartOfTheSet) {
					log.debug("Node " + i + " not found. trying to remove");
					curNode.getProperty(ttrColProp).remove();
				}
			}
			getSession().save();
		} catch (Exception e) {
			throw new SlcException("Cannot update TreeTestResultCollection "
					+ ttrCollection, e);
		}
	}

	public void delete(TreeTestResultCollection ttrCollection) {
		try {
			// FIXME: should not delete sub nodes
			Node curNode;
			String colId = ttrCollection.getId();
			NodeIterator ni = resultNodesInCollection(colId);
			while (ni.hasNext()) {
				curNode = ni.nextNode();
				curNode.remove();
			}
			getSession().save();
		} catch (Exception e) {
			throw new SlcException("Cannot delete TreeTestResultCollection "
					+ ttrCollection, e);
		}
	}

	public SortedSet<TreeTestResultCollection> listCollections() {
		// FIXME: optimize
		Map<String, TreeTestResultCollection> lst = new HashMap<String, TreeTestResultCollection>();
		NodeIterator nodeIterator = query("//testresult");
		while (nodeIterator.hasNext()) {
			Node node = nodeIterator.nextNode();
			String colId = property(node, ttrColProp);
			if (colId != null) {
				if (!lst.containsKey(colId))
					lst.put(colId, new TreeTestResultCollection(colId));
				TreeTestResultCollection ttrc = lst.get(colId);
				ttrc.getResults().add((TreeTestResult) nodeMapper.load(node));
			}
		}
		return new TreeSet<TreeTestResultCollection>(lst.values());
	}

	public void addResultToCollection(final TreeTestResultCollection ttrc,
			final String resultUuid) {
		try {
			String queryString;
			Node curNode;
			String colId = ttrc.getId();
			queryString = "//*[@uuid='" + resultUuid + "']";
			Query query = queryManager.createQuery(queryString, Query.XPATH);
			curNode = JcrUtils.querySingleNode(query);
			if (curNode == null) {
				throw new SlcException("Cannot add TreeTestResult of Id "
						+ resultUuid + " to collection " + colId);
			} else
				curNode.setProperty(ttrColProp, colId);
			getSession().save();
		} catch (Exception e) {
			throw new SlcException("Cannot add TreeTestResult of Id "
					+ resultUuid + " to collection " + ttrc, e);
		}

	}

	public void removeResultFromCollection(final TreeTestResultCollection ttrc,
			final String resultUuid) {
		try {
			log.debug("remove result");
			String queryString;
			Node curNode;
			String colId = ttrc.getId();
			queryString = "//*[@uuid='" + resultUuid + "']";
			Query query = queryManager.createQuery(queryString, Query.XPATH);
			curNode = JcrUtils.querySingleNode(query);
			log.debug("Query : " + queryString + " - Node retrieved "
					+ curNode.getPath());
			if (curNode == null) {
				throw new SlcException("Cannot remove TreeTestResult of Id "
						+ resultUuid + " from collection " + colId);
			} else {
				curNode.getProperty(ttrColProp).remove();
				log.debug("Property removed : "
						+ curNode.getProperty(ttrColProp).getString());
			}
			getSession().save();
		} catch (Exception e) {
			throw new SlcException("Cannot remove TreeTestResult of Id "
					+ resultUuid + " from collection " + ttrc, e);
		}
	}

	// FIXME specify and implement this method
	public List<ResultAttributes> listResultAttributes(String collectionId) {
		// FIXME: optimize
		List<ResultAttributes> list = new ArrayList<ResultAttributes>();
		if (collectionId == null) {
			List<TreeTestResult> results = asTreeTestResultList(resultNodes(
					null, null));

			for (TreeTestResult ttr : results) {
				list.add(new ResultAttributes(ttr));
			}
		} else {
			NodeIterator nodeIterator = resultNodesInCollection(collectionId);
			while (nodeIterator.hasNext()) {
				list.add(new ResultAttributes((TreeTestResult) nodeMapper
						.load(nodeIterator.nextNode())));
			}
		}

		return list;
		// throw new UnsupportedOperationException();
		// List<ResultAttributes> list;
		// if (collectionId == null)
		// list = getHibernateTemplate().find(
		// "select new org.argeo.slc.core.test.tree.ResultAttributes(ttr)"
		// + " from TreeTestResult ttr");
		// else
		// list = getHibernateTemplate()
		// .find(
		// "select new org.argeo.slc.core.test.tree.ResultAttributes(ttr) "
		// + " from TreeTestResult ttr, TreeTestResultCollection ttrc "
		// + " where ttr in elements(ttrc.results) and ttrc.id=?",
		// collectionId);
		//
		// return list;
	}

	public List<TreeTestResult> listResults(String collectionId,
			Map<String, String> attributes) {
		List<TreeTestResult> list;

		if (collectionId == null) {
			if (attributes == null || attributes.size() == 0)
				list = asTreeTestResultList(resultNodes(null, null));
			else if (attributes.size() == 1) {
				Map.Entry<String, String> entry = attributes.entrySet()
						.iterator().next();
				list = asTreeTestResultList(resultNodes(entry.getKey(), entry
						.getValue()));
			} else {
				throw new SlcException(
						"Multiple attributes filter are currently not supported.");
			}
		} else {
			if (attributes == null || attributes.size() == 0)
				list = asTreeTestResultList(resultNodesInCollection(collectionId));
			else if (attributes.size() == 1) {
				Map.Entry<String, String> entry = attributes.entrySet()
						.iterator().next();
				list = asTreeTestResultList(resultNodesInCollection(
						collectionId, entry.getKey(), entry.getValue()));
			} else {
				throw new SlcException(
						"Multiple attributes filter are currently not supported.");
			}
		}
		return list;
	}

	// UTILITIES

	protected NodeIterator resultNodesInCollection(String collectionId,
			String attributeKey, String attributeValue) {
		String queryString = "//testresult[@" + ttrColProp + "='"
				+ collectionId + "' and @" + attributeKey + "='"
				+ attributeValue + "']";
		return query(queryString);
	}

	protected NodeIterator resultNodesInCollection(String collectionId) {
		String queryString = "//testresult[@" + ttrColProp + "='"
				+ collectionId + "']";
		return query(queryString);
	}

	protected NodeIterator resultNodes(String attributeKey,
			String attributeValue) {
		String queryString;
		if (attributeKey != null)
			queryString = "//testResult[@" + attributeKey + "='"
					+ attributeValue + "']";
		else
			queryString = "//testResult";
		return query(queryString);
	}

	protected List<TreeTestResult> asTreeTestResultList(
			NodeIterator nodeIterator) {
		List<TreeTestResult> lst = new ArrayList<TreeTestResult>();
		while (nodeIterator.hasNext()) {
			lst.add((TreeTestResult) nodeMapper.load(nodeIterator.nextNode()));
		}
		return lst;
	}

	private NodeIterator query(String query) {
		try {
			if (log.isDebugEnabled())
				log.debug("Retrieve nodes from query: " + query);
			Query q = queryManager.createQuery(query, Query.XPATH);
			return q.execute().getNodes();
		} catch (Exception e) {
			throw new SlcException("Cannot load nodes from query: " + query);
		}
	}

	private String property(Node node, String key) {
		try {
			return node.getProperty(key).getString();
		} catch (Exception e) {
			log.warn("Cannot retrieve property " + key + " of node " + node, e);
			return null;
		}
	}

}
