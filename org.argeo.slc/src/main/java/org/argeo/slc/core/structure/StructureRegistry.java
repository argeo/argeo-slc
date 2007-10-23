package org.argeo.slc.core.structure;

import java.util.List;

public interface StructureRegistry {
	public static String READ = "READ";
	public static String ALL = "ALL";
	public static String ACTIVE = "ACTIVE";
	
	public void register(StructureElement element);
	public void register(StructureAware structureAware);
	public List<StructureElement> listElements();
	
	public StructureElement getElement(StructurePath path);
	
	public void setMode(String mode);
	public String getMode();
	
	public List<StructurePath> getActivePaths();
	public void setActivePaths(List<StructurePath> activePaths);
}
