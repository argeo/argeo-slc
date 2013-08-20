package org.argeo.slc.repo.osgi;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.DefaultNameVersion;
import org.argeo.slc.NameVersion;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.build.ModularDistribution;
import org.argeo.slc.repo.ArtifactDistribution;

public class ArgeoOsgiDistribution extends ArtifactDistribution implements
		ModularDistribution {
	private final static Log log = LogFactory
			.getLog(ArgeoOsgiDistribution.class);

	private Set<ArtifactDistribution> modules = new HashSet<ArtifactDistribution>();

	public ArgeoOsgiDistribution(String coords) {
		super(coords);
	}

	public void init() {
		if (log.isDebugEnabled()) {
			log.debug("## " + toString());
			for (NameVersion nv : listModulesNameVersions()) {
				log.debug(nv);
			}
		}
	}

	public void destroy() {

	}

	public Distribution getModuleDistribution(String moduleName,
			String moduleVersion) {
		NameVersion searched = new DefaultNameVersion(moduleName, moduleVersion);
		for (ArtifactDistribution ad : modules) {
			if (ad.equals(searched))
				return ad;
		}
		return null;
	}

	public Set<NameVersion> listModulesNameVersions() {
		return new TreeSet<NameVersion>(modules);
	}

	public Object getModulesDescriptor(String descriptorType) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setModules(Set<ArtifactDistribution> modules) {
		this.modules = modules;
	}

}
