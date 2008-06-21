package org.argeo.slc.ant;

public interface SlcAntConstants {
	// ANT
	/** The Ant reference to the Spring application context used. */
	public static final String REF_ROOT_CONTEXT = "slcApplicationContext";
	/** The Ant reference to the SLC structure registry used. */
	public static final String REF_STRUCTURE_REGISTRY = "slcStructureRegistry";
	/** The Ant reference to the <code>TreePath</code> of the current project */
	public static final String REF_PROJECT_PATH = "slcProjectPath";
	/**
	 * Resource path to the property file listing the SLC specific Ant tasks:
	 * /org/argeo/slc/ant/taskdefs.properties
	 */
	public static final String SLC_TASKDEFS_RESOURCE_PATH = "/org/argeo/slc/ant/taskdefs.properties";
	/**
	 * Resource path to the property file listing the SLC specific Ant types:
	 * /org/argeo/slc/ant/typedefs.properties
	 */
	public static final String SLC_TYPEDEFS_RESOURCE_PATH = "/org/argeo/slc/ant/typedefs.properties";
	public static final String REF_SLC_EXECUTION = "slcExecution";

	// SLC EXECUTION
	public static final String EXECTYPE_ANT = "org.apache.tools.ant";
	public static final String EXECTYPE_SLC_ANT = "org.argeo.slc.ant";

	public final static String EXECATTR_RUNTIME = "slc.runtime";
	public final static String EXECATTR_ANT_FILE = "ant.file";
	public final static String EXECATTR_ANT_TARGETS = "ant.targets";

	// PROPERTIES
	/** Property for the root dir (SLC root property file). */
	public final static String ROOT_DIR_PROPERTY = "slc.rootDir";
	/** Property for the conf dir (SLC root property file). */
	public final static String CONF_DIR_PROPERTY = "slc.confDir";
	/** Property for the work dir (SLC root property file). */
	public final static String WORK_DIR_PROPERTY = "slc.workDir";
	/** Name of the Spring bean used by default */
	public final static String DEFAULT_TEST_RUN_PROPERTY = "slc.defaultTestRun";

	// LOG4J
	public final static String MDC_ANT_PROJECT = "slc.ant.project";

}
