package org.argeo.slc.ant;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
		p.executeTarget(p.getDefaultTarget());
		return p;
	}

	/** Executes all paths of the default target of the Ant file. */
	public static Project runAll(File antFile) {
		if (log.isDebugEnabled())
			log.debug("Runs all paths of Ant file " + antFile);
		Project p = new Project();
		p.setUserProperty("ant.file", antFile.getAbsolutePath());
		p.setBaseDir(antFile.getParentFile());
		p.init();
		ProjectHelper helper = new SlcProjectHelper();
		p.addReference(ProjectHelper.PROJECTHELPER_REFERENCE, helper);
		helper.parse(p, antFile);
		p.executeTarget(p.getDefaultTarget());
		return p;
	}
}
