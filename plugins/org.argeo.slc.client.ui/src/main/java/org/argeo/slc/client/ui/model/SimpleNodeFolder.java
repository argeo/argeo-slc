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
import javax.jcr.nodetype.NodeType;

import org.argeo.slc.SlcException;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;

/**
 * UI Tree component that wrap a node of type NT_UNSTRUCTURED. list either other
 * folders and/or a list of results. keeps a reference to its parent.
 */
public class SimpleNodeFolder extends ResultParent {

	private Node node = null;

	/**
	 * 
	 * @param parent
	 * @param node
	 *            throws an exception if null
	 * @param name
	 */
	public SimpleNodeFolder(SimpleNodeFolder parent, Node node, String name) {
		super(name);
		if (node == null)
			throw new SlcException("Node Object cannot be null");
		setParent(parent);
		this.node = node;
	}

	@Override
	protected void initialize() {
		try {
			NodeIterator ni = node.getNodes();
			while (ni.hasNext()) {
				Node currNode = ni.nextNode();
				if (currNode.isNodeType(SlcTypes.SLC_TEST_RESULT)) {
					SingleResultNode srn = new SingleResultNode(this, currNode,
							currNode.getProperty(SlcNames.SLC_TEST_CASE)
									.getString());
					addChild(srn);
				} else if (currNode.isNodeType(NodeType.NT_UNSTRUCTURED))
					addChild(new SimpleNodeFolder(this, currNode,
							currNode.getName()));
			}
		} catch (RepositoryException re) {
			throw new SlcException(
					"Unexpected error while initializing simple node folder : "
							+ getName(), re);
		}
	}

	@Override
	public synchronized void dispose() {
		super.dispose();
	}

	public Node getNode() {
		return node;
	}
}