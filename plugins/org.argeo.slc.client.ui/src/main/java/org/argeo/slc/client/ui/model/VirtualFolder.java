/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.argeo.ArgeoException;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;

/**
 * UI Tree component. Virtual folder to list either other folders and/or a list
 * of results. Keeps a reference to its parent that might be null if the .
 */
public class VirtualFolder extends ResultParent {

	private Node node = null;
	private boolean isPassed = true;

	public VirtualFolder(VirtualFolder parent, Node node, String name) {
		super(name);
		setParent(parent);
		this.node = node;
	}

	@Override
	public synchronized void dispose() {
		super.dispose();
	}

	/** Override normal behavior to initialize display */
	@Override
	public synchronized Object[] getChildren() {
		if (isLoaded()) {
			return super.getChildren();
		} else {
			// initialize current object
			try {
				if (node != null) {
					NodeIterator ni = node.getNodes();
					while (ni.hasNext()) {
						Node currNode = ni.nextNode();
						if (currNode.isNodeType(SlcTypes.SLC_TEST_RESULT))
							addChild(new SingleResultNode(this, node, node
									.getProperty(SlcNames.SLC_TEST_CASE)
									.getString()));
						else if (currNode
								.isNodeType(SlcTypes.SLC_RESULT_FOLDER))
							addChild(new VirtualFolder(this, node,
									node.getName()));
					}
				}
				return super.getChildren();
			} catch (RepositoryException e) {
				throw new ArgeoException(
						"Cannot initialize WorkspaceNode UI object."
								+ getName(), e);
			}
		}
	}

	// @Override
	// public boolean refreshPassedStatus() {
	// Object[] children = getChildren();
	// isPassed = true;
	// checkChildrenStatus: for (int i = 0; i <= children.length; i++) {
	// if (children[i] instanceof VirtualFolder) {
	//
	// }
	// if (!((ResultParent) children[i]).isPassed()) {
	// isPassed = false;
	// break checkChildrenStatus;
	// }
	// }
	// return isPassed;
	// }

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
	}

}