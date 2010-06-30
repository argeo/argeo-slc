/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

package org.argeo.slc.jcr.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.BeanNodeMapper;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.core.attachment.SimpleAttachment;
import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.tree.PartSubList;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.structure.StructureElement;
import org.argeo.slc.test.TestResultPart;
import org.springframework.beans.BeanWrapper;

public class TreeTestResultNodeMapper extends BeanNodeMapper {
	private final static Log log = LogFactory
			.getLog(TreeTestResultNodeMapper.class);

	/**
	 * Transforms a TreeTestResult to the specified jcr Node in order to persist
	 * it.
	 * 
	 * @param beanWrapper
	 * @param node
	 * @throws RepositoryException
	 */
	protected void beanToNode(BeanWrapper beanWrapper, Node node)
			throws RepositoryException {

		if (log.isTraceEnabled())
			log.debug("Map TreeTestResult to node " + node.getPath());

		// We know we are mapping a TreeTestResult so we cast it
		TreeTestResult ttr = (TreeTestResult) beanWrapper.getWrappedInstance();

		// First we persist the class
		node.setProperty(getClassProperty(), ttr.getClass().getName());

		// Then we persist String uuid, Date closeDate
		node.setProperty("uuid", ttr.getUuid());
		if (ttr.getCloseDate() != null) {
			Calendar cal = new GregorianCalendar();
			cal.setTime(ttr.getCloseDate());
			node.setProperty("closeDate", cal);
		}

		Node childNode;

		// Elements & resultParts are merged, we use treeSPath to build the tree
		// Element label is stored as a property of the vertice
		// ResultParts are stored as childNode named resultpart[xx].

		SortedMap<TreeSPath, StructureElement> elements = ttr.getElements();

		for (TreeSPath key : elements.keySet()) {
			String relPath = key.getAsUniqueString();
			// We remove the first separator
			relPath = relPath.substring(1);

			// check if already exists.
			if (!node.hasNode(relPath)) {
				// TODO Factorize that
				Node tmpNode = node;
				String[] pathes = relPath.split("/");
				for (int i = 0; i < pathes.length; i++) {
					if (tmpNode.hasNode(pathes[i]))
						tmpNode = tmpNode.getNode(pathes[i]);
					else
						tmpNode = tmpNode.addNode(pathes[i]);
				}
				childNode = tmpNode;
			} else
				childNode = node.getNode(relPath);

			childNode.setProperty("label", elements.get(key).getLabel());
			// We add the tags
			Map<String, String> tags = elements.get(key).getTags();
			for (String tag : tags.keySet()) {
				NodeIterator tagIt = childNode.getNodes("tag");
				Node tagNode = null;
				while (tagIt.hasNext()) {
					Node n = tagIt.nextNode();
					if (n.getProperty("name").getString().equals(tag)) {
						tagNode = n;
					}
				}

				if (tagNode == null) {
					tagNode = childNode.addNode("tag");
					tagNode.setProperty("name", tag);
				}

				tagNode.setProperty("value", tags.get(tag));

				// remove forbidden characters
				// String cleanTag = JcrUtils.removeForbiddenCharacters(tag);
				// if (!cleanTag.equals(tag))
				// log.warn("Tag '" + tag + "' persisted as '" + cleanTag
				// + "'");
				// childNode.setProperty(cleanTag, tags.get(tag));
			}

			// We set the class in order to be able to retrieve
			childNode.setProperty(getClassProperty(), StructureElement.class
					.getName());
		}

		SortedMap<TreeSPath, PartSubList> resultParts = ttr.getResultParts();

		for (TreeSPath key : resultParts.keySet()) {
			String relPath = key.getAsUniqueString();

			// we get rid of the '/' that begins every TreeSPath Unique string
			// and add the partsublist level
			relPath = relPath.substring(1) + "/partsublist";

			// check if already exists.
			if (!node.hasNode(relPath)) {
				// TODO Factorize that
				Node tmpNode = node;
				String[] pathes = relPath.split("/");
				for (int i = 0; i < pathes.length; i++) {
					if (tmpNode.hasNode(pathes[i]))
						tmpNode = tmpNode.getNode(pathes[i]);
					else
						tmpNode = tmpNode.addNode(pathes[i]);
				}
				childNode = tmpNode;
				//log.debug("Node created " + childNode.getPath());
			} else {
				childNode = node.getNode(relPath);
				//log.debug("Node already existing " + childNode.getPath());
			}

			List<TestResultPart> list = resultParts.get(key).getParts();

			Node listNode;
			int i;
			for (i = 0; i < list.size(); i++) {
				// TestResultPart trp = list.get(i);
				// FIXME : ResultParts are systematicaly added.
				// There no check to see if already exists.
				listNode = childNode.addNode("resultpart");
				update(listNode, list.get(i));
			}
		}

		// TODO : store files in the graph
		// As for now, we only store on a vertice called after the name value of
		// the SimpleAttachment Object
		// and uuid & contentType as property

		List<SimpleAttachment> attachments = ttr.getAttachments();
		if (attachments.size() != 0) {
			if (node.hasNode("attachments"))
				childNode = node.getNode("attachments");
			else {
				if (getPrimaryNodeType() != null)
					childNode = node.addNode("attachments",
							getPrimaryNodeType());
				else
					childNode = node.addNode("attachments");
			}
			Node attachNode;
			for (int i = 0; i < attachments.size(); i++) {
				attachNode = childNode.addNode(attachments.get(i).getName());
				attachNode.setProperty("uuid", attachments.get(i).getUuid());
				attachNode.setProperty("contentType", attachments.get(i)
						.getContentType());
			}
		}

		// attributes are stored as properties of the testResult node
		for (String key : ttr.getAttributes().keySet()) {
			String mapValue = ttr.getAttributes().get(key);
			node.setProperty(key, mapValue);
		}

	}

	/**
	 * Transforms a node into a TreeTestResult Instance
	 */
	@SuppressWarnings("unchecked")
	protected Object nodeToBean(Node node) throws RepositoryException {
		// method variables
		String uuid;
		String clssName = node.getProperty(getClassProperty()).getString();
		QueryManager qm = node.getSession().getWorkspace().getQueryManager();
		Query query;

		if (log.isTraceEnabled())
			log.debug("Map node " + node.getPath() + " to bean " + clssName);

		// It's a very specific implementation,
		// We don't need to use a bean wrapper.
		TreeTestResult ttr = new TreeTestResult();

		// RESULTPART PARAMETERS
		uuid = node.getProperty("uuid").getString();
		ttr.setUuid(uuid);

		if (node.hasProperty("closeDate")) {
			ttr.setCloseDate((node.getProperty("closeDate").getDate())
					.getTime());
		}

		// ATTRIBUTES
		Map attributes = new TreeMap<String, String>();
		PropertyIterator propIt = node.getProperties();
		props: while (propIt.hasNext()) {
			Property prop = propIt.nextProperty();

			// TODO Define a rule to generalize it (Namespace ??)
			// Get rid of specific case. mainly uuid
			if ("uuid".equals(prop.getName())
					|| prop.getName().equals(getClassProperty())
					|| prop.getName().startsWith("jcr")) {
				continue props;
			}

			// else it's an attribute, we retrieve it
			attributes.put(prop.getName(), prop.getString());
		}
		ttr.setAttributes(attributes);

		// ATTACHMENTS
		NodeIterator ni;
		if (node.hasNode("attachments")) {
			List<SimpleAttachment> attachments = new ArrayList<SimpleAttachment>();

			ni = node.getNode("attachments").getNodes();
			while (ni.hasNext()) {
				Node curNode = ni.nextNode();
				attachments.add(new SimpleAttachment(curNode
						.getProperty("uuid").getString(), curNode.getName(),
						curNode.getProperty("contentType").getString()));
			}
			ttr.setAttachments(attachments);
		}

		// STRUCTURED ELEMENTS

		String basePath = node.getPath();
		SortedMap<TreeSPath, PartSubList> resultParts = new TreeMap<TreeSPath, PartSubList>();
		SortedMap<TreeSPath, StructureElement> elements = new TreeMap<TreeSPath, StructureElement>();

		// We have to add the uuid of the current node to be sure that we are in
		// its sub tree
		String queryString = "//testresult[@uuid='" + uuid + "']";

		// Business part of the current query
		queryString = queryString + "//*[@" + getClassProperty() + "='"
				+ StructureElement.class.getName() + "']";

		query = qm.createQuery(queryString, Query.XPATH);
		ni = query.execute().getNodes();

		while (ni.hasNext()) {
			Node curNode = ni.nextNode();
			String curPath = curNode.getPath().substring(basePath.length());
			TreeSPath tsp = new TreeSPath();

			// We must add the "/" at the begining of the jcr path to have a
			// TreeSPath string
			tsp.setAsUniqueString(tsp.getSeparator() + curPath);

			SimpleSElement se = new SimpleSElement();
			se.setLabel(curNode.getProperty("label").getString());

			Map<String, String> tagMap = new TreeMap<String, String>();
			NodeIterator tagIt = node.getNodes("tag");
			while (tagIt.hasNext()) {
				Node tagNode = tagIt.nextNode();
				tagMap.put(tagNode.getProperty("name").getString(), tagNode
						.getProperty("value").getString());

			}
			// PropertyIterator tagIt = curNode.getProperties();
			// tags: while (tagIt.hasNext()) {
			// Property prop = tagIt.nextProperty();
			// //log.debug("Handling property named : " + prop.getName());
			//
			// // TODO Define a rule to generalize it
			// // Specific case. mainly uuid
			// if ("uuid".equals(prop.getName())
			// || prop.getName().equals(getClassProperty())
			// || prop.getName().startsWith("jcr")) {
			// continue tags;
			// }
			//
			// // else it's an attribute, we retrieve it
			// tagMap.put(prop.getName(), prop.getString());
			// }

			se.setTags(tagMap);
			elements.put(tsp, se);
		}
		//log.debug("We added " + elements.size() + " elements");

		ttr.setElements(elements);

		// RESULTPARTS

		// We have to had the uuid of the current node to be sure that we are in
		// its sub tree
		queryString = "//testresult[@uuid='" + uuid + "']";

		// Business part of the current query
		queryString = queryString + "//partsublist";
		query = qm.createQuery(queryString, Query.XPATH);
		ni = query.execute().getNodes();
		while (ni.hasNext()) {
			Node curNode = ni.nextNode();
			String curPath = curNode.getParent().getPath().substring(
					basePath.length());

			TreeSPath tsp = new TreeSPath();
			// We must add the "/" at the begining of the jcr path to have a
			// TreeSPath string
			tsp.setAsUniqueString(tsp.getSeparator() + curPath);

			NodeIterator ni2 = curNode.getNodes("resultpart");
			List<TestResultPart> parts = new Vector<TestResultPart>();
			while (ni2.hasNext()) {
				parts.add((TestResultPart) load(ni2.nextNode()));
			}
			PartSubList psl = new PartSubList();
			psl.setParts(parts);
			resultParts.put(tsp, psl);
		}

		ttr.setResultParts(resultParts);

		return ttr;
	}
}
