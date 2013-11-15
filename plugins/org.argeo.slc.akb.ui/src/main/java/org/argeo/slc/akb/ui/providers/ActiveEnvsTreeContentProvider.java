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
package org.argeo.slc.akb.ui.providers;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbNames;
import org.argeo.slc.akb.AkbTypes;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Content provider for a tree of active AKB environments. Displays
 * <CODE>ActiveTreeItem</CODE> to be able to display subtrees of item without
 * duplicating the information from the corresponding template.
 */
public class ActiveEnvsTreeContentProvider implements ITreeContentProvider {
	// private final static Log log = LogFactory
	// .getLog(ActiveEnvsTreeContentProvider.class);

	/**
	 * @param parent
	 *            Pass current user home as parameter
	 * 
	 */
	public Object[] getElements(Object parent) {
		if (parent instanceof Object[])
			return (Object[]) parent;
		else
			return null;
	}

	public Object getParent(Object child) {
		return ((ActiveTreeItem) child).getParent();
	}

	public Object[] getChildren(Object parent) {
		try {
			ActiveTreeItem currItem = (ActiveTreeItem) parent;
			Node parNode = currItem.getNode();
			Node envNode = currItem.getEnvironment();

			if (parNode.isNodeType(AkbTypes.AKB_ENV)) {
				Session session = parNode.getSession();
				if (parNode.hasProperty(AkbNames.AKB_ENV_TEMPLATE_PATH)
						&& session.nodeExists(parNode.getProperty(
								AkbNames.AKB_ENV_TEMPLATE_PATH).getString()))
					parNode = session.getNode(parNode.getProperty(
							AkbNames.AKB_ENV_TEMPLATE_PATH).getString());
				else
					return null;
			}

			NodeIterator ni = parNode.getNodes();
			List<ActiveTreeItem> children = new ArrayList<ActiveTreeItem>();
			while (ni.hasNext()) {
				Node currNode = ni.nextNode();
				if (!currNode.isNodeType(AkbTypes.AKB_CONNECTOR_FOLDER)) {
					ActiveTreeItem currChild = new ActiveTreeItem(currItem,
							currNode, envNode);
					children.add(currChild);
				}
			}
			return children.toArray();
		} catch (RepositoryException e) {
			throw new AkbException("Error while getting children nodes", e);
		}
	}

	public boolean hasChildren(Object parent) {
		try {
			ActiveTreeItem currItem = (ActiveTreeItem) parent;
			Node parNode = currItem.getNode();

			if (parNode.isNodeType(AkbTypes.AKB_ENV)) {
				Session session = parNode.getSession();
				if (parNode.hasProperty(AkbNames.AKB_ENV_TEMPLATE_PATH)
						&& session.nodeExists(parNode.getProperty(
								AkbNames.AKB_ENV_TEMPLATE_PATH).getString()))
					parNode = session.getNode(parNode.getProperty(
							AkbNames.AKB_ENV_TEMPLATE_PATH).getString());
				else
					return false;
			}

			return parNode.hasNodes();
		} catch (RepositoryException e) {
			throw new AkbException("Error while checking children nodes", e);
		}
	}

	public void dispose() {
		// FIXME implement if needed
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}