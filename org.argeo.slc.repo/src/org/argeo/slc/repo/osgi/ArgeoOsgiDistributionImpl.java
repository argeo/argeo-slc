package org.argeo.slc.repo.osgi;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.CategoryNameVersion;
import org.argeo.slc.ModuleSet;
import org.argeo.slc.NameVersion;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.execution.ExecutionFlow;
import org.argeo.slc.repo.ArgeoOsgiDistribution;
import org.argeo.slc.repo.ArtifactDistribution;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.osgi.framework.Constants;

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
		migrateTov2(Paths.get(System.getProperty("user.home"), "dev/git/unstable/argeo-tp/argeo-tp"));
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
			else if (nv instanceof ArchiveWrapperCNV)
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

	public void migrateTov2(Path baseDir) {
		Set<ArchiveWrapper> archiveWrappers = new HashSet<>();
		Iterator<? extends NameVersion> nvIt = nameVersions();
		while (nvIt.hasNext()) {
			NameVersion nv = nvIt.next();
			try {
				if (nv instanceof CategoryNameVersion) {
					CategoryNameVersion cnv = (CategoryNameVersion) nv;
					// TODO add branch?
					Path categoryBase = baseDir.resolve(cnv.getCategory());
					Files.createDirectories(categoryBase);
					if (cnv instanceof BndWrapper) {
						BndWrapper bw = (BndWrapper) cnv;
						Path bndPath = categoryBase.resolve(cnv.getName() + ".bnd");
						Map<String, String> props = new TreeMap<>();
						for (Map.Entry<Object, Object> entry : ((BndWrapper) cnv).getBndProperties().entrySet()) {
							props.put(entry.getKey().toString(), entry.getValue().toString());
						}
						props.put(Constants.BUNDLE_SYMBOLICNAME, cnv.getName());
						props.put(Constants.BUNDLE_VERSION, cnv.getVersion());
						if (bw.getLicense() != null)
							props.put(Constants.BUNDLE_LICENSE, bw.getLicense().toString());
						else
							log.warn("No license for " + cnv);
						if (bw.getDoNotModify()) {
							props.put("SLC-Source-Original", "true");
						}
						// props.put("SLC-Category", cnv.getCategory());

						if (cnv instanceof MavenWrapper) {
							MavenWrapper mw = (MavenWrapper) cnv;
							String sourceCoords = mw.getSourceCoords();
							props.put("SLC-Source-M2", sourceCoords);
							Artifact mavenCnv = new DefaultArtifact(sourceCoords);
							if (mavenCnv.getArtifactId().equals(cnv.getName()))
								props.remove(Constants.BUNDLE_SYMBOLICNAME);
							if (mavenCnv.getVersion().equals(cnv.getVersion()))
								props.remove(Constants.BUNDLE_VERSION);
						} else if (cnv instanceof UriWrapper) {
							UriWrapper mw = (UriWrapper) cnv;
							props.put("SLC-Source-URI", mw.getEffectiveUri());
							if (mw.getUri() == null && mw.getBaseUri() != null) {
								log.warn("Base URI for " + cnv);
								props.put("SLC-Source-BaseURI", mw.getBaseUri());
								props.put("SLC-Source-VersionSeparator", mw.getVersionSeparator());
							}
						} else {
							log.warn("Unidentified BND wrapper " + cnv);
						}

						// write BND file
						try (Writer writer = Files.newBufferedWriter(bndPath)) {
							// writer.write("# " + cnv + "\n");
							props: for (String key : props.keySet()) {
								String value = props.get(key);
								if (Constants.EXPORT_PACKAGE.equals(key) && "*".equals(value.trim()))
									continue props;

								writer.write(key + ": " + value + '\n');
							}
							if (log.isTraceEnabled())
								log.trace("Wrote " + bndPath);
						}
					} else if (cnv instanceof ArchiveWrapperCNV) {
						ArchiveWrapperCNV onv = (ArchiveWrapperCNV) cnv;
						ArchiveWrapper aw = onv.getBuild();
						archiveWrappers.add(aw);
						// TODO specify and implement archive wrapper support
					} else {
						log.warn("Unsupported wrapper " + cnv.getClass() + " for " + cnv);
					}

				} else {
					log.error("Category required for " + nv + ", skipping...");
				}
			} catch (IOException e) {
				log.error("Could not process " + nv, e);
			}
		}
		if (log.isDebugEnabled()) {
			for (ArchiveWrapper aw : archiveWrappers) {
				log.debug("Archive wrapper " + aw.getUri() + ":");
				log.debug(" includes: " + aw.getIncludes());
				log.debug(" excludes: " + aw.getExcludes());
				log.debug(" beans   : " + aw.getWrappers());
			}
		}

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
