package org.argeo.slc.dao.structure.tree;

import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.structure.tree.TreeSRegistry;

/**
 * DAO for tree-base structure registry.
 * 
 * @see TreeSRegistry
 */
public interface TreeSRegistryDao {
	/** Gets the TreeSRegistry which has the same root path as the provided path. */
	public TreeSRegistry getActiveTreeSRegistry();

	/** Creates a new registry. */
	public void create(TreeSRegistry registry);

	/** Updates an existing registry. */
	public void update(TreeSRegistry registry);
}
