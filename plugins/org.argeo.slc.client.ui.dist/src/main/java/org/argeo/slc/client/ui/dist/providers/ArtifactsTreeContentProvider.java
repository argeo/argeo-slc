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
package org.argeo.slc.client.ui.dist.providers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.argeo.ArgeoException;
import org.argeo.eclipse.ui.jcr.utils.JcrItemsComparator;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ArtifactsTreeContentProvider implements ITreeContentProvider,
		SlcTypes {

	// Utils
	private boolean sortChildren = true;
	private JcrItemsComparator itemComparator = new JcrItemsComparator();

	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}

	public Object getParent(Object child) {
		return null;
	}

	public Object[] getChildren(Object parent) {
		Object[] elements = null;
		try {
			if (parent instanceof Node) {
				Node node = (Node) parent;
				NodeIterator ni = node.getNodes();
				List<Node> nodesList = new ArrayList<Node>();
				while (ni.hasNext()) {
					nodesList.add(ni.nextNode());
				}
				if (sortChildren) {
					Node[] arr = (Node[]) nodesList.toArray(new Node[nodesList
							.size()]);
					Arrays.sort(arr, itemComparator);
					return arr;
				} else
					return nodesList.toArray();

			}
		} catch (RepositoryException e) {
			throw new ArgeoException(
					"Unexpected exception while listing node properties", e);
		}
		return elements;
	}

	public boolean hasChildren(Object parent) {
		try {
			if (parent instanceof Node) {
				Node curNode = (Node) parent;
				// We manually stop digging at this level
				if (curNode.isNodeType(SLC_ARTIFACT_VERSION_BASE))
					return false;
				else if (curNode.hasNodes())
					return true;
			}
		} catch (RepositoryException e) {
			throw new ArgeoException(
					"Unexpected exception while checking if property is multiple",
					e);
		}
		return false;
	}

	public void setSortChildren(boolean sortChildren) {
		this.sortChildren = sortChildren;
	}

	public boolean getSortChildren() {
		return sortChildren;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}
}