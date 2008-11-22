package org.argeo.slc.core.structure.tree;

import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructureRegistry;

/**
 * Provides default implementations of some methods of <code>TreeSRelated</code>.
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

	public StructureElement getStructureElement(String key) {
		return new SimpleSElement(key);
	}

}
