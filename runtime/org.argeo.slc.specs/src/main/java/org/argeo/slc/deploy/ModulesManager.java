/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
package org.argeo.slc.deploy;

import java.util.List;

import org.argeo.slc.NameVersion;

/** Provides access to deployed modules */
public interface ModulesManager {
	/** @return a full fledged module descriptor. */
	public ModuleDescriptor getModuleDescriptor(String moduleName,
			String version);

	/**
	 * @return a list of minimal module descriptors of the deployed modules
	 */
	public List<ModuleDescriptor> listModules();

	/** Synchronously upgrades the module referenced by this name version */
	public void upgrade(NameVersion nameVersion);

	/** Starts the module */
	public void start(NameVersion nameVersion);

	/** Stops the module */
	public void stop(NameVersion nameVersion);
}
