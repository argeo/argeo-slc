package org.argeo.slc.repo.osgi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.ModuleSet;
import org.argeo.slc.NameVersion;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.repo.ArgeoOsgiDistribution;
import org.argeo.slc.repo.ArtifactDistribution;

/**
 * A consistent and versioned OSGi distribution, which can be built and tested.
 */
public class ArgeoOsgiDistributionImpl extends ArtifactDistribution implements ArgeoOsgiDistribution {
	private final static Log log = LogFactory.getLog(ArgeoOsgiDistributionImpl.class);

	private List<Object> modules = new ArrayList<Object>();

	public ArgeoOsgiDistributionImpl(String coords) {
		super(coords);
	}

	public void init() {
		if (log.isDebugEnabled())
			log.debug(describe());
	}

	public void destroy() {

	}

	public String describe() {
		SortedSet<String> sort = new TreeSet<String>();
		Iterator<? extends NameVersion> nvIt = nameVersions();
		while (nvIt.hasNext()) {
			NameVersion nv = nvIt.next();
			String str = nv.toString();
			if (nv instanceof MavenWrapper)
				str = str + "\t(Maven)";
			else if (nv instanceof UriWrapper)
				str = str + "\t(URI)";
			else if (nv instanceof OsgiCategorizedNV)
				str = str + "\t(OSGi from archive)";
			else if (nv instanceof BndWrapper)
				str = str + "\t(Plain BND from archive)";
			else
				str = str + "\t(UNKNOWN??)";
			sort.add(str);
		}

		StringBuffer buf = new StringBuffer("## DISTRIBUTION " + toString() + " ##\n");
		for (String str : sort) {
			buf.append(str).append('\n');
		}
		return buf.toString();
	}

	public Iterator<NameVersion> nameVersions() {
		List<NameVersion> nameVersions = new ArrayList<NameVersion>();
		for (Object module : modules) {
			// extract runnable from execution flow
			if (module instanceof ExecutionFlow) {
				for (Iterator<Runnable> it = ((ExecutionFlow) module).runnables(); it.hasNext();) {
					processModule(nameVersions, it.next());
				}
			} else {
				processModule(nameVersions, module);
			}
		}
		return nameVersions.iterator();
	}

	private void processModule(List<NameVersion> nameVersions, Object module) {
		if (module instanceof ModuleSet)
			addNameVersions(nameVersions, (ModuleSet) module);
		else if (module instanceof NameVersion) {
			NameVersion nv = (NameVersion) module;
			addNameVersion(nameVersions, nv);
		} else
			log.warn("Ignored " + module);
	}

	private void addNameVersions(List<NameVersion> nameVersions, ModuleSet moduleSet) {
		Iterator<? extends NameVersion> it = moduleSet.nameVersions();
		while (it.hasNext()) {
			NameVersion nv = it.next();
			addNameVersion(nameVersions, nv);
		}
	}

	protected void addNameVersion(List<NameVersion> nameVersions, NameVersion nv) {
		if (!nameVersions.contains(nv)) {
			nameVersions.add(nv);
		}
	}

	// Modular distribution interface methods. Not yet used.
	public Distribution getModuleDistribution(String moduleName, String moduleVersion) {
		throw new UnsupportedOperationException();
	}

	public Object getModulesDescriptor(String descriptorType) {
		throw new UnsupportedOperationException();
	}

	/* DEPENDENCY INJECTION */
	public void setModules(List<Object> modules) {
		this.modules = modules;
	}
}
