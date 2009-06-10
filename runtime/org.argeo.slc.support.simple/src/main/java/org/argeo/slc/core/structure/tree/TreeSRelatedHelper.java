package org.argeo.slc.core.structure.tree;

import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.structure.StructureAware;
import org.argeo.slc.structure.StructureElement;
import org.argeo.slc.structure.StructureRegistry;

/**
 * Provides default implementations of some methods of <code>TreeSRelated</code>
 * .
 */
public abstract class TreeSRelatedHelper implements TreeSRelated {
	private TreeSPath basePath;
	private StructureRegistry<TreeSPath> registry;

	// private ThreadLocal<TreeSPath> basePath = new ThreadLocal<TreeSPath>();
	// private ThreadLocal<StructureRegistry<TreeSPath>> registry = new
	// ThreadLocal<StructureRegistry<TreeSPath>>();

	public TreeSPath getBasePath() {
		return basePath;
	}

	public StructureRegistry<TreeSPath> getRegistry() {
		return registry;
	}

	public void notifyCurrentPath(StructureRegistry<TreeSPath> registry,
			TreeSPath path) {
		this.basePath = path;
		this.registry = registry;
	}

	public StructureElement getStructureElement(String key) {
		return new SimpleSElement(key);
	}

	/**
	 * Checks wether the object is {@link StructureAware} and forward path and
	 * registry. null safe for both arguments.
	 */
	@SuppressWarnings(value = { "unchecked" })
	protected void forwardPath(Object obj, String childName) {
		if (obj == null)
			return;

		if (obj instanceof StructureAware && basePath != null) {
			TreeSPath path;
			if (childName != null)
				path = basePath.createChild(childName);
			else
				path = basePath;

			((StructureAware<TreeSPath>) obj).notifyCurrentPath(registry, path);
		}
	}

}
