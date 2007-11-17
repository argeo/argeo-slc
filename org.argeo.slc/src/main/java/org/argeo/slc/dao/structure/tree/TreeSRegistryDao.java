package org.argeo.slc.dao.structure.tree;

import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.structure.tree.TreeSRegistry;

public interface TreeSRegistryDao {
	public TreeSRegistry getTreeSRegistry(TreeSPath treeSPath);
	public void create(TreeSRegistry registry);
}
