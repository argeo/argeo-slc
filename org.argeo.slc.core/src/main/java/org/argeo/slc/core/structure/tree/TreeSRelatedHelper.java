package org.argeo.slc.core.structure.tree;

import org.argeo.slc.core.structure.StructureRegistry;

/**
 * Provides default implementations of some methods of
 * <code>TreeSRelated</code>.
 */
public abstract class TreeSRelatedHelper implements TreeSRelated {
	private TreeSPath basePath;
	private StructureRegistry<TreeSPath> registry;


	public TreeSPath getBasePath() {
		return basePath;
	}

	public StructureRegistry<TreeSPath> getRegistry() {
		return registry;
	}

	public void notifyCurrentPath(StructureRegistry<TreeSPath> registry,
			TreeSPath path) {
		basePath = path;
		this.registry = registry;
	}
}
