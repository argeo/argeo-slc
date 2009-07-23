package org.argeo.slc.build;

import java.util.Set;

public interface ModularDistribution extends Distribution, NameVersion {
	public Distribution getModuleDistribution(String moduleName,
			String moduleVersion);

	public Set<NameVersion> listModulesNameVersions();

	public Object getModulesDescriptor(String descriptorType);
}
