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

import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;

/**
 * UI Tree component. Virtual folder to list a list of results. Keeps a
 * reference to its parent that might be null. It also keeps a reference to all
 * nodes that must be displayed as children of the current virtual folder.
 */
public class VirtualFolder extends ResultParent {
	List<Node> displayedNodes;

	public VirtualFolder(VirtualFolder parent, List<Node> displayedNodes,
			String name) {
		super(name);
		setParent(parent);
		this.displayedNodes = displayedNodes;
	}

	@Override
	protected void initialize() {
		try {
			for (Node currNode : displayedNodes) {
				if (currNode.isNodeType(SlcTypes.SLC_TEST_RESULT)) {
					SingleResultNode srn = new SingleResultNode(this, currNode,
							currNode.getProperty(SlcNames.SLC_TEST_CASE)
									.getString());
					addChild(srn);
				}
			}
		} catch (RepositoryException re) {
			throw new SlcException(
					"Unexpected error while initializing ParentNodeFolder : "
							+ getName(), re);
		}
	}
}