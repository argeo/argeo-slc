package org.argeo.slc.core.structure;

import java.util.List;

/** Registry where the whole structure is stored. */
public interface StructureRegistry {
	/** Read mode: the structure is only read. */
	public static String READ = "READ";
	/** All mode: everything is executed regardless of the active paths. */
	public static String ALL = "ALL";
	/** Active mode: only the active paths are executed. */
	public static String ACTIVE = "ACTIVE";

	/** Adds an element to the registry. */
	public void register(StructurePath path, StructureElement element);

	/** Lists <b>all</b> registered elements. */
	public List<StructureElement> listElements();

	/** Lists <b>all</b> registered elements. */
	public List<StructurePath> listPaths();

	/** Gets a element based on its path. */
	public StructureElement getElement(StructurePath path);

	/**
	 * Set the interpreter mode: read, all or active.
	 * 
	 * @see #READ
	 * @see #ALL
	 * @see #ACTIVE
	 */
	public void setMode(String mode);

	/**
	 * Gets the current interpreter mode.
	 * 
	 * @see #READ
	 * @see #ALL
	 * @see #ACTIVE
	 */
	public String getMode();

	/**
	 * Gets the list of active paths, which will be run if executed in
	 * <code>ACTIVE</code> mode.
	 */
	public List<StructurePath> getActivePaths();

	/**
	 * Sets the list of active path, which will be run if executed in
	 * <code>ACTIVE</code> mode.
	 */
	public void setActivePaths(List<StructurePath> activePaths);
}
