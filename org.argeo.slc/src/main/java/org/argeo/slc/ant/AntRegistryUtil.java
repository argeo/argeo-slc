package org.argeo.slc.ant;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import org.argeo.slc.core.structure.StructurePath;
import org.argeo.slc.core.structure.StructureRegistry;

/** Utilities to manipulate the structure registry in SLC Ant. */
public class AntRegistryUtil {
	private static Log log = LogFactory.getLog(AntRegistryUtil.class);

	/** Read a structure registry from an Ant file without executing it. */
	public static StructureRegistry readRegistry(File antFile) {

		Project p = new Project();
		p.setUserProperty("ant.file", antFile.getAbsolutePath());
		p.setBaseDir(antFile.getParentFile());
		p.init();
		ProjectHelper helper = new SlcProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, antFile);

		StructureRegistry registry = (StructureRegistry) p
				.getReference(SlcProjectHelper.REF_STRUCTURE_REGISTRY);
		registry.setMode(StructureRegistry.READ);

		p.executeTarget(p.getDefaultTarget());
		return registry;
	}

	/** Execute only the active paths of the Ant file. */
	public static void runActive(File antFile, List<StructurePath> activePaths) {

		Project p = new Project();
		p.setUserProperty("ant.file", antFile.getAbsolutePath());
		p.setBaseDir(antFile.getParentFile());
		p.init();
		ProjectHelper helper = new SlcProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, antFile);

		StructureRegistry registry = (StructureRegistry) p
				.getReference(SlcProjectHelper.REF_STRUCTURE_REGISTRY);
		registry.setMode(StructureRegistry.ACTIVE);
		registry.setActivePaths(activePaths);
		p.executeTarget(p.getDefaultTarget());
	}

	/** Execute all paths of the default target of the Ant file. */
	public static void runAll(File antFile) {
		Project p = new Project();
		p.setUserProperty("ant.file", antFile.getAbsolutePath());
		p.setBaseDir(antFile.getParentFile());
		p.init();
		ProjectHelper helper = new SlcProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, antFile);
		p.executeTarget(p.getDefaultTarget());
	}

	public static void main(String[] args) {
		File antFile = new File(
				"C:/dev/workspaces/default/org.argeo.slc/src/test/slc/root/Category1/SubCategory2/build.xml");
		StructureRegistry registry = AntRegistryUtil.readRegistry(antFile);

		StringBuffer buf = new StringBuffer("");

		int count = 0;
		List<StructurePath> activePaths = new Vector<StructurePath>();
		for (StructurePath path : registry.listPaths()) {
			buf.append(path);
			if (count != 0 && count % 3 == 0) {
				// skip
			} else {
				activePaths.add(path);
				buf.append(" <");
			}
			buf.append('\n');
			count++;
		}
		log.info(buf);

		runActive(antFile, activePaths);

	}
}
