package org.argeo.slc.core.structure.tree;

import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructureRegistry;

public interface TreeSRelated {
	public TreeSPath getBasePath();
	public StructureRegistry<TreeSPath> getRegistry();
	public StructureElement getStructureElement(String key);
}
