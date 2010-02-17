package org.argeo.slc.deploy;

import java.util.List;

import org.argeo.slc.build.NameVersion;

/** Provides access to modules */
public interface ModulesManager {
	/** @return a full fledged module descriptor. */
	public ModuleDescriptor getModuleDescriptor(String moduleName,
			String version);

	/**
	 * @return a list of minimal module descriptors
	 */
	public List<ModuleDescriptor> listModules();

	/** Synchronously upgrades the module referenced by this name version */
	public void upgrade(NameVersion nameVersion);
}
