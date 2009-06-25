package org.argeo.slc.core.structure.tree;

import org.argeo.slc.structure.StructureAware;
import org.argeo.slc.structure.StructureElement;
import org.argeo.slc.structure.StructureRegistry;

public interface TreeSRelated extends StructureAware<TreeSPath> {
	public TreeSPath getBasePath();

	public StructureRegistry<TreeSPath> getRegistry();

	public StructureElement getStructureElement(String key);
}
