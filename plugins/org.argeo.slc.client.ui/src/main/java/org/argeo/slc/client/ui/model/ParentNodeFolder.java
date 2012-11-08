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
 * UI Tree component that wrap a node of type NT_UNSTRUCTURED or base node for
 * UI specific, user defined tree structure of type SLC_MY_RESULTS_ROOT_FOLDER.
 * 
 * It is used for
 * <ul>
 * <li>automatically generated tree structure to store results (typically
 * Year/Month/Day...)</li>
 * <li>parent node for user defined tree structure (typically My Results node)</li>
 * </ul>
 * It thus lists either result folders, other folders and/or a list of results
 * and keeps a reference to its parent.
 */
public class ParentNodeFolder extends ResultParent {
	// private final static Log log = LogFactory.getLog(ParentNodeFolder.class);

	private Node node = null;

	/**
	 * 
	 * @param parent
	 * @param node
	 *            throws an exception if null
	 * @param name
	 */
	public ParentNodeFolder(ParentNodeFolder parent, Node node, String name) {
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
				} else if (currNode.isNodeType(SlcTypes.SLC_RESULT_FOLDER)) {
					// FIXME change label
					ResultFolder rf = new ResultFolder(this, currNode,
							currNode.getName());
					addChild(rf);
				} else if (currNode.isNodeType(SlcTypes.SLC_CHECK)) {
					// FIXME : manually skip node types that are not to be
					// displayed
					// Do nothing
				} else if (currNode.isNodeType(NodeType.NT_UNSTRUCTURED))
					addChild(new ParentNodeFolder(this, currNode,
							currNode.getName()));
			}
		} catch (RepositoryException re) {
			throw new SlcException(
					"Unexpected error while initializing ParentNodeFolder : "
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

	/**
	 * Overriden in the specific case of "My result" root object to return an
	 * ordered list of children
	 */
	public synchronized Object[] getChildren() {
		Object[] children = super.getChildren();
		try {
			if (node.isNodeType(SlcTypes.SLC_MY_RESULT_ROOT_FOLDER))
				return ResultParentUtils.orderChildren(children);
			else
				return children;
		} catch (RepositoryException re) {
			throw new SlcException(
					"Unexpected error while initializing simple node folder : "
							+ getName(), re);
		}
	}
}