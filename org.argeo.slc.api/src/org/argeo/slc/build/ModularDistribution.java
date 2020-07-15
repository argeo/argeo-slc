package org.argeo.slc.build;

import org.argeo.slc.ModuleSet;
import org.argeo.slc.NameVersion;

/**
 * A distribution of modules, that is components that can be identified by a
 * name / version couple.
 * 
 * @see NameVersion
 */
public interface ModularDistribution extends Distribution, NameVersion,
		ModuleSet {
	public Distribution getModuleDistribution(String moduleName,
			String moduleVersion);

	/** A descriptor such as P2, OBR or yum metadata. */
	public Object getModulesDescriptor(String descriptorType);
}
