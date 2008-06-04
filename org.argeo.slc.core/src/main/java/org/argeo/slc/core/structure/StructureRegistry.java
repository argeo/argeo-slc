package org.argeo.slc.core.structure;

import java.util.List;

/** Registry where the whole structure is stored. */
public interface StructureRegistry<P extends StructurePath> {
	/** Read mode: the structure is only read. */
	public static String READ = "READ";
	/** All mode: everything is executed regardless of the active paths. */
	public static String ALL = "ALL";
	/** Active mode: only the active paths are executed. */
	public static String ACTIVE = "STATUS_ACTIVE";

	/** Adds an element to the registry. */
	public void register(P path, StructureElement element);

	/** Lists <b>all</b> registered elements. */
	public List<StructureElement> listElements();

	/** Lists <b>all</b> registered elements. */
	public List<P> listPaths();

	/** Gets a element based on its path. */
	public <T extends StructureElement> T getElement(P path);

	/**
	 * Set the interpreter mode: read, all or active.
	 * 
	 * @see #READ
	 * @see #ALL
	 * @see #STATUS_ACTIVE
	 */
	public void setMode(String mode);

	/**
	 * Gets the current interpreter mode.
	 * 
	 * @see #READ
	 * @see #ALL
	 * @see #STATUS_ACTIVE
	 */
	public String getMode();

	/**
	 * Gets the list of active paths, which will be run if executed in
	 * <code>STATUS_ACTIVE</code> mode.
	 */
	public List<P> getActivePaths();

	/**
	 * Sets the list of active path, which will be run if executed in
	 * <code>STATUS_ACTIVE</code> mode.
	 */
	public void setActivePaths(List<P> activePaths);
}
