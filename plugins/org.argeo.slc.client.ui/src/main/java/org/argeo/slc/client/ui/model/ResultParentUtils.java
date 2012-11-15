/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.client.ui.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.argeo.slc.SlcException;
import org.argeo.slc.jcr.SlcJcrResultUtils;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;

public class ResultParentUtils {
	// private final static Log log =
	// LogFactory.getLog(ResultParentUtils.class);

	// public static Object[] orderChildren(Object[] children) {
	// List<ResultFolder> folders = new ArrayList<ResultFolder>();
	// List<SingleResultNode> results = new ArrayList<SingleResultNode>();
	// for (Object child : children) {
	// if (child instanceof ResultFolder)
	// folders.add((ResultFolder) child);
	// else if (child instanceof SingleResultNode)
	// results.add((SingleResultNode) child);
	// }
	//
	// // Comparator first = Collections.reverseOrder();
	// Collections.sort(folders);
	// // Comparator<SingleResultNode> second = Collections.reverseOrder();
	// Collections.sort(results);
	//
	// Object[] orderedChildren = new Object[folders.size() + results.size()];
	// int i = 0;
	// Iterator<ResultFolder> it = folders.iterator();
	// while (it.hasNext()) {
	// orderedChildren[i] = it.next();
	// i++;
	// }
	// Iterator<SingleResultNode> it2 = results.iterator();
	// while (it2.hasNext()) {
	// orderedChildren[i] = it2.next();
	// i++;
	// }
	// return orderedChildren;
	// }

	public static List<Node> getResultsForDates(Session session,
			List<String> dateRelPathes) {
		if (dateRelPathes == null || dateRelPathes.size() == 0)
			throw new SlcException("Specify at least one correct date as Path");

		try {
			String basePath = SlcJcrResultUtils.getSlcResultsBasePath(session);
			Iterator<String> it = dateRelPathes.iterator();
			StringBuffer clause = new StringBuffer();
			clause.append("SELECT * FROM [");
			clause.append(SlcTypes.SLC_DIFF_RESULT);
			clause.append("] as results");
			clause.append(" WHERE ");
			while (it.hasNext()) {
				String absPath = basePath + "/" + it.next();
				clause.append("ISDESCENDANTNODE(results, [");
				clause.append(absPath);
				clause.append("]) ");
				clause.append(" OR ");
			}
			// remove last " OR "
			clause.delete(clause.length() - 4, clause.length());
			clause.append(" ORDER BY results.[" + Property.JCR_CREATED
					+ "] DESC");

			// log.debug("request : " + clause.toString());
			QueryManager qm = session.getWorkspace().getQueryManager();
			Query q = qm.createQuery(clause.toString(), Query.JCR_SQL2);
			QueryResult result = q.execute();

			NodeIterator ni = result.getNodes();
			List<Node> nodes = new ArrayList<Node>();
			while (ni.hasNext()) {
				nodes.add(ni.nextNode());
			}
			return nodes;
		} catch (RepositoryException re) {
			throw new SlcException(
					"Unexpected error while getting Results for given date", re);
		}
	}

	/**
	 * recursively update passed status of the current ResultFolder and its
	 * parent if needed
	 * 
	 * @param node
	 *            cannot be null
	 * 
	 */
	public static void updatePassedStatus(Node node, boolean passed) {
		try {
			if (!node.hasNode(SlcNames.SLC_STATUS))
				// we have reached the root of the tree. stop the
				// recursivity
				return;
			boolean pStatus = node.getNode(SlcNames.SLC_STATUS)
					.getProperty(SlcNames.SLC_SUCCESS).getBoolean();
			if (pStatus == passed)
				// nothing to update
				return;
			else if (!passed) {
				// New status is 'failed' : we only update status of the result
				// folder and its
				// parent if needed
				node.getNode(SlcNames.SLC_STATUS).setProperty(
						SlcNames.SLC_SUCCESS, passed);
				updatePassedStatus(node.getParent(), passed);
			} else {
				// New status is 'passed': we must first check if all siblings
				// have also
				// successfully completed
				boolean success = true;
				NodeIterator ni = node.getNodes();
				children: while (ni.hasNext()) {
					Node currNode = ni.nextNode();
					if ((currNode.isNodeType(SlcTypes.SLC_DIFF_RESULT) || currNode
							.isNodeType(SlcTypes.SLC_RESULT_FOLDER))
							&& !currNode.getNode(SlcNames.SLC_STATUS)
									.getProperty(SlcNames.SLC_SUCCESS)
									.getBoolean()) {
						success = false;
						break children;
					}
				}
				if (success) {
					node.getNode(SlcNames.SLC_STATUS).setProperty(
							SlcNames.SLC_SUCCESS, passed);
					updatePassedStatus(node.getParent(), passed);
				} else
					// one of the siblings had also the failed status so
					// above tree remains unchanged.
					return;
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot update result passed status", e);
		}
	}
}
