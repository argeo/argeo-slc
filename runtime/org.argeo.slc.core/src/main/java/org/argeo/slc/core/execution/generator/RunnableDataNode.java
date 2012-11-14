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
package org.argeo.slc.core.execution.generator;

import java.util.List;
import java.util.Map;

/**
 * Stores information relative to a Runnable.
 * Allows to structure the information as a tree, each node
 * storing data as a Map.
 */
public interface RunnableDataNode {

	/**
	 * @return a Map containing the data associated with this node.
	 * Data associated with parent nodes are expected
	 * to be contained in the returned Map
	 */
	public Map<String, Object> getData();	

	/**
	 * @return the name of the bean to create.
	 * Can be null if no bean shall be created for the 
	 * <code>RunnableDataNode</code> (e.g. is is a sub-node)
	 */
	public String getBeanName();
	
	/**
	 * @return the path of the flow bean to create.
	 * Can be null if the bean to created is not an
	 * <code>ExecutionFlow</code> or if no bean shall be created for the 
	 * <code>RunnableDataNode</code> (e.g. is is a sub-node)
	 */
	public String getPath();
		
	/**
	 * @return whether the <code>RunnableDataNode</code> has
	 * children or not.
	 * Expected to be equivalent to <code>getChildren().empty()</code>
	 */
	public boolean isLeaf();

	/**
	 * @return the list of <code>RunnableDataNode</code> children.
	 * Can be empty. Shall not be null.
	 */
	public List<RunnableDataNode> getChildren();
	
	/**
	 * @return the <code>RunnableDataNode</code> parent.
	 * Can be null if no parent is defined (top node).
	 */
	public RunnableDataNode getParent();
	
	/**
	 * Sets the <code>RunnableDataNode</code> parent
	 * @param parent
	 */
	public void setParent(RunnableDataNode parent);	
}
