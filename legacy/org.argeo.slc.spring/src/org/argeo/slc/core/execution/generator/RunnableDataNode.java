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
