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
