package org.argeo.slc.ide.ui.launch.osgi;

import org.argeo.slc.ide.ui.SlcIdeUiPlugin;

/** Constants used by OSGi launch. */
public interface OsgiLauncherConstants {

	public final static String OSGI_BUNDLES = "osgi.bundles";
	public final static String ECLIPSE_APPLICATION = "eclipse.application";

	public final static String ARGEO_OSGI_START = "argeo.osgi.start";
	public final static String ARGEO_OSGI_BUNDLES = "argeo.osgi.bundles";
	public final static String ARGEO_OSGI_LOCATIONS = "argeo.osgi.locations";
	public final static String ARGEO_OSGI_DATA_DIR = "argeo.osgi.data.dir";
	public final static String VMS_PROPERTY_PREFIX = "slc.launch.vm";

	// Configuration
	public final static String ATTR_SYNC_BUNDLES = SlcIdeUiPlugin.ID
			+ ".syncBundles";
	public final static String ATTR_CLEAR_DATA_DIRECTORY = SlcIdeUiPlugin.ID
			+ ".clearDataDirectory";

	public final static String ATTR_DEFAULT_VM_ARGS = SlcIdeUiPlugin.ID
			+ ".defaultVmArgs";
	public final static String ATTR_ADDITIONAL_PROGRAM_ARGS = SlcIdeUiPlugin.ID
			+ ".additionalProgramArgs";
	public final static String ATTR_ADDITIONAL_VM_ARGS = SlcIdeUiPlugin.ID
			+ ".additionalVmArgs";
	public final static String ATTR_ADD_JVM_PATHS = SlcIdeUiPlugin.ID
			+ ".addJvmPaths";
	public final static String ATTR_DATADIR = SlcIdeUiPlugin.ID + ".dataDir";
}
