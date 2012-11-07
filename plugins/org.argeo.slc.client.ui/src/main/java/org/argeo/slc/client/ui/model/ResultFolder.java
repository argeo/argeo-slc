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
import javax.jcr.RepositoryException;

import org.argeo.slc.SlcException;
import org.argeo.slc.jcr.SlcNames;

/**
 * UI Tree component that wrap a node of type ResultFolder. list either other
 * folders and/or a list of results. keeps a reference to its parent.
 */
public class ResultFolder extends ParentNodeFolder implements
		Comparable<ResultFolder> {

	/**
	 * 
	 * @param parent
	 * @param node
	 *            throws an exception if null
	 * @param name
	 */
	public ResultFolder(ParentNodeFolder parent, Node node, String name) {
		super(parent, node, name);
		try {
			// initialize passed status if possible
			if (node.hasNode(SlcNames.SLC_STATUS))
				setPassed(node.getNode(SlcNames.SLC_STATUS)
						.getProperty(SlcNames.SLC_SUCCESS).getBoolean());
		} catch (RepositoryException re) {
			throw new SlcException(
					"Unexpected error while initializing result folder : "
							+ getName(), re);
		}
	}

	/**
	 * Overriden to return an ordered list of children
	 */
	public synchronized Object[] getChildren() {
		Object[] children = super.getChildren();
		return ResultParentUtils.orderChildren(children);
	}

	public int compareTo(ResultFolder o) {
		return super.compareTo(o);
	}
}