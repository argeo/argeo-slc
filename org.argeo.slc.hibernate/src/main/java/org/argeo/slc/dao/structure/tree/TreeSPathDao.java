package org.argeo.slc.dao.structure.tree;

import org.argeo.slc.core.structure.tree.TreeSPath;

public interface TreeSPathDao {
	public void create(TreeSPath path);

	public TreeSPath getTreeSPath(String pathString);

	public TreeSPath getOrCreate(TreeSPath pathTransient);
}
