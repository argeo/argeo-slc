package org.argeo.slc.core.structure;

public interface StructureElement {
	public StructurePath getPath();
	public String getDescription();
	public Boolean getActive();
	public void setActive(Boolean active);
}
