/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.argeo.slc.structure;

import java.util.List;

/** Registry where the whole structure is stored. */
public interface StructureRegistry<P extends StructurePath> {
	/** Read mode: the structure is only read. */
	public static String READ = "READ";
	/** All mode: everything is executed regardless of the active paths. */
	public static String ALL = "ALL";
	/** Active mode: only the active paths are executed. */
	public static String ACTIVE = "ACTIVE";

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
