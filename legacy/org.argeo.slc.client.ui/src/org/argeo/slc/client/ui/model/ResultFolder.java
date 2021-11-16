package org.argeo.slc.client.ui.model;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;

/**
 * UI Tree component that wrap a node of type ResultFolder. list either other
 * folders and/or a list of results. keeps a reference to its parent.
 */
public class ResultFolder extends ParentNodeFolder {

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
			if (node.hasNode(SlcNames.SLC_AGGREGATED_STATUS))
				setPassed(node.getNode(SlcNames.SLC_AGGREGATED_STATUS)
						.getProperty(SlcNames.SLC_SUCCESS).getBoolean());
		} catch (RepositoryException re) {
			throw new SlcException(
					"Unexpected error while initializing result folder : "
							+ getName(), re);
		}
	}

	// /**
	// * Overriden to return an ordered list of children
	// */
	// public synchronized Object[] getChildren() {
	// Object[] children = super.getChildren();
	// return ResultParentUtils.orderChildren(children);
	// }
	//
	// public int compareTo(ResultFolder o) {
	// return super.compareTo(o);
	// }
}