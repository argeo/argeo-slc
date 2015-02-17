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

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Workspace;

import org.argeo.eclipse.ui.TreeParent;
import org.argeo.slc.SlcException;
import org.argeo.slc.jcr.SlcNames;

/**
 * UI Tree component. Wraps a result node of a JCR {@link Workspace}. It also
 * keeps a reference to its parent node that can either be a
 * {@link ResultFolder}, a {@link SingleResultNode} or a {@link VirtualFolder}.
 * It has no child.
 */

public class SingleResultNode extends ResultParent implements
		Comparable<SingleResultNode> {

	private final Node node;
	private boolean passed;

	// keeps a local reference to the node's name to avoid exception when the
	// session is lost

	/** Creates a new UiNode in the UI Tree */
	public SingleResultNode(TreeParent parent, Node node, String name) {
		super(name);
		setParent(parent);
		this.node = node;
		setPassed(refreshPassedStatus());
	}

	public boolean refreshPassedStatus() {
		try {
			Node check;
			if (node.hasNode(SlcNames.SLC_AGGREGATED_STATUS)) {
				check = node.getNode(SlcNames.SLC_AGGREGATED_STATUS);
				passed = check.getProperty(SlcNames.SLC_SUCCESS).getBoolean();
				return passed;
			} else
				// Happens only if the UI triggers a refresh while the execution
				// is in progress and the corresponding node is being built
				return false;
		} catch (RepositoryException re) {
			throw new SlcException(
					"Unexpected error while checking result status", re);
		}
	}

	/** returns the node wrapped by the current UI object */
	public Node getNode() {
		return node;
	}

	/**
	 * Override normal behavior : Results have no children for this view
	 */
	@Override
	public synchronized Object[] getChildren() {
		return null;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	public boolean isPassed() {
		return passed;
	}

	@Override
	protected void initialize() {
		// Do nothing this object is fully initialized at instantiation time.
	}

	public int compareTo(SingleResultNode o) {
		return super.compareTo(o);
	}

}
