package org.argeo.slc.ant;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import org.argeo.slc.core.structure.StructurePath;
import org.argeo.slc.core.structure.StructureRegistry;

/** Utilities to manipulate the structure registry in SLC Ant. */
public class AntRegistryUtil {
	private static Log log = LogFactory.getLog(AntRegistryUtil.class);

	/** Reads a structure registry from an Ant file without executing it. */
	public static StructureRegistry readRegistry(File antFile) {
		if (log.isDebugEnabled())
			log.debug("Reads registry for Ant file " + antFile);
		Project p = new Project();
		p.setUserProperty("ant.file", antFile.getAbsolutePath());
		p.setBaseDir(antFile.getParentFile());
		p.init();
		ProjectHelper helper = new SlcProjectHelper();
		p.addReference(ProjectHelper.PROJECTHELPER_REFERENCE, helper);
		helper.parse(p, antFile);

		StructureRegistry registry = (StructureRegistry) p
				.getReference(SlcProjectHelper.REF_STRUCTURE_REGISTRY);
		registry.setMode(StructureRegistry.READ);

		p.executeTarget(p.getDefaultTarget());
		return registry;
	}

	/** Executes only the active paths of the Ant file. */
	public static Project runActive(File antFile,
			List<StructurePath> activePaths) {
		if (log.isDebugEnabled())
			log.debug("Runs the " + activePaths.size()
					+ " provided active paths of Ant file " + antFile);
		Project p = new Project();
		p.setUserProperty("ant.file", antFile.getAbsolutePath());
		p.setBaseDir(antFile.getParentFile());
		p.init();
		ProjectHelper helper = new SlcProjectHelper();
		p.addReference(ProjectHelper.PROJECTHELPER_REFERENCE, helper);
		helper.parse(p, antFile);

		StructureRegistry registry = (StructureRegistry) p
				.getReference(SlcProjectHelper.REF_STRUCTURE_REGISTRY);
		registry.setMode(StructureRegistry.ACTIVE);
		registry.setActivePaths(activePaths);

		runProject(p, null);
		return p;
	}

	/** Executes all paths of the provided target of the Ant file. */
	public static Project runAll(File antFile, String target) {
		if (log.isDebugEnabled())
			log.debug("Runs all paths of Ant file " + antFile);
		Project p = new Project();
		p.setUserProperty("ant.file", antFile.getAbsolutePath());
		p.setBaseDir(antFile.getParentFile());
		p.init();
		ProjectHelper helper = new SlcProjectHelper();
		p.addReference(ProjectHelper.PROJECTHELPER_REFERENCE, helper);
		helper.parse(p, antFile);

		runProject(p, target);
		return p;
	}

	/** Executes all paths of the provided target of the Ant URL. */
	public static Project runAll(URL url, String target, Properties properties) {
		if (log.isDebugEnabled())
			log.debug("Runs all paths of Ant URL " + url);
		Project p = new Project();
		p.setUserProperty("ant.file", url.toString());
		// p.setBaseDir(url.toString());
		p.init();
		ProjectHelper helper = new SlcProjectHelper();
		p.addReference(ProjectHelper.PROJECTHELPER_REFERENCE, helper);
		helper.parse(p, url);

		if (properties != null) {
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				p.setUserProperty(entry.getKey().toString(), entry.getValue()
						.toString());
			}
		}

		runProject(p, target);
		return p;
	}

	/** Executes all paths of the default target of the Ant file. */
	public static Project runAll(File antFile) {
		return runAll(antFile, null);
	}

	protected static void runProject(Project p, String target) {
		p.fireBuildStarted();
		Throwable exception = null;
		try {
			p.executeTarget(target != null ? target : p.getDefaultTarget());
		} catch (Throwable e) {
			exception = e;
			log.error("Exception when running Ant: ",e);
		} finally {
			p.fireBuildFinished(exception);
		}
	}
}
