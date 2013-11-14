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

import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbTypes;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/** Basic content provider for a tree of AKB environment templates */
public class TemplatesTreeContentProvider implements ITreeContentProvider {

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
		try {
			Node node = (Node) child;
			if (node.getDepth() == 0)
				return null;
			else
				return node.getParent();
		} catch (RepositoryException e) {
			throw new AkbException("Error while getting parent node", e);
		}
	}

	public Object[] getChildren(Object parent) {
		try {
			NodeIterator ni = ((Node) parent).getNodes();
			List<Node> nodes = new ArrayList<Node>();

			while (ni.hasNext()) {
				Node currNode = ni.nextNode();
				if (!currNode.isNodeType(AkbTypes.AKB_CONNECTOR_FOLDER))
					nodes.add(currNode);
			}

			return nodes.toArray();
		} catch (RepositoryException e) {
			throw new AkbException("Error while getting children nodes", e);
		}
	}

	public boolean hasChildren(Object parent) {
		try {
			// refine this
			return ((Node) parent).hasNodes();
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