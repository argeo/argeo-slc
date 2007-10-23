package org.argeo.slc.core.structure;

import java.util.List;

public interface StructureRegistry {
	public void register(StructureElement element);
	public void register(StructureAware structureAware);
	public List<StructureElement> listElements();
	
	public StructureElement getElement(StructurePath path);
}
