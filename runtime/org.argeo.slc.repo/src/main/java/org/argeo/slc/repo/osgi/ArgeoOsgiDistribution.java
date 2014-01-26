package org.argeo.slc.repo.osgi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.CategorizedNameVersion;
import org.argeo.slc.ModuleSet;
import org.argeo.slc.NameVersion;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.build.ModularDistribution;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.repo.ArtifactDistribution;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/** A consistent and versioned OSGi distribution, which can be built and tested. */
public class ArgeoOsgiDistribution extends ArtifactDistribution implements
		ModularDistribution {
	private final static Log log = LogFactory
			.getLog(ArgeoOsgiDistribution.class);

	private List<Object> modules = new ArrayList<Object>();

	public ArgeoOsgiDistribution(String coords) {
		super(coords);
	}

	public void init() {
		if (log.isDebugEnabled()) {
			Iterator<? extends NameVersion> nvIt = nameVersions();
			while (nvIt.hasNext()) {
				log.debug(nvIt.next());

			}
		}
	}

	// private static void print(ModuleSet moduleSet, Integer depth) {
	// StringBuilder prefix = new StringBuilder();
	// for (int i = 0; i < depth; i++) {
	// prefix.append(' ');
	// }
	// String p = prefix.toString();
	// prefix.setLength(0);
	// log.debug(p + "## " + moduleSet.toString());
	// Iterator<? extends NameVersion> nvIt = moduleSet.nameVersions();
	// while (nvIt.hasNext()) {
	// NameVersion nv = nvIt.next();
	// if (nv instanceof ModuleSet)
	// print((ModuleSet) nv, depth + 1);
	// else
	// log.debug(p + nv);
	//
	// }
	// }

	public void destroy() {

	}

	public Distribution getModuleDistribution(String moduleName,
			String moduleVersion) {
		// NameVersion searched = new DefaultNameVersion(moduleName,
		// moduleVersion);
		// for (Distribution ad : modules) {
		// if (ad.equals(searched))
		// return ad;
		// }
		return null;
	}

	public Iterator<NameVersion> nameVersions() {
		List<NameVersion> nameVersions = new ArrayList<NameVersion>();
		for (Object module : modules) {
			// extract runnable from execution flow
			if (module instanceof ExecutionFlow)
				module = ((ExecutionFlow) module).getRunnable();

			if (module instanceof ModuleSet)
				addNameVersions(nameVersions, (ModuleSet) module);
			else if (module instanceof NameVersion) {
				NameVersion nv = (NameVersion) module;
				if (!nameVersions.contains(nv))
					nameVersions.add(nv);
			} else
				log.warn("Ignored " + module);
		}
		return nameVersions.iterator();
	}

	private void addNameVersions(List<NameVersion> nameVersions,
			ModuleSet moduleSet) {
		Iterator<? extends NameVersion> it = moduleSet.nameVersions();
		while (it.hasNext()) {
			NameVersion nv = it.next();
			if (!nameVersions.contains(nv))
				nameVersions.add(nv);
		}
	}

	public Object getModulesDescriptor(String descriptorType) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setModules(List<Object> modules) {
		this.modules = modules;
	}

}
