package org.argeo.slc.deploy;

import org.argeo.slc.NameVersion;

/**
 * Represents a deployed module of a broader deployed system. A module is
 * uniquely identifiable via a name / version.
 */
public interface Module extends DeployedSystem, NameVersion {
	/** A serializable stateless description of the module */
	public ModuleDescriptor getModuleDescriptor();
}
