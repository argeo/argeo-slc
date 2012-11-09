package org.argeo.slc.ide.ui.launch.osgi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import org.argeo.slc.ide.ui.SlcIdeUiPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.service.resolver.BundleDescription;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.eclipse.pde.internal.build.IPDEBuildConstants;
import org.eclipse.pde.launching.IPDELauncherConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Most of the actual logic is concentrated in this class which manipulates
 * {@link ILaunchConfigurationWorkingCopy}. Static method are used since the
 * shortcut and launch configuration classes are already extending PDE classes.
 */
@SuppressWarnings("restriction")
public class OsgiLaunchHelper implements OsgiLauncherConstants {
	private static Boolean debug = true;

	private final static String DEFAULT_DATA_DIR = "data";
	private final static String DEFAULT_EXEC_DIR = "exec";
	private final static String DEFAULT_VMARGS = "-Xmx256m";
	private final static String DEFAULT_PROGRAM_ARGS = "-console";

	/** Sets default values on this configuration. */
	public static void setDefaults(ILaunchConfigurationWorkingCopy wc,
			Boolean isEclipse) {
		try {
			if (isEclipse) {
				wc.setAttribute(IPDELauncherConstants.USE_DEFAULT, false);
				wc.setAttribute(IPDELauncherConstants.USE_PRODUCT, false);
			}

			wc.setAttribute(ATTR_ADD_JVM_PATHS, false);
			wc.setAttribute(ATTR_ADDITIONAL_VM_ARGS, DEFAULT_VMARGS);
			wc.setAttribute(ATTR_ADDITIONAL_PROGRAM_ARGS, DEFAULT_PROGRAM_ARGS);

			// Defaults
			String originalVmArgs = wc.getAttribute(
					IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "");
			wc.setAttribute(ATTR_DEFAULT_VM_ARGS, originalVmArgs);

			// do NOT use custom features (both must be set)
			wc.setAttribute(IPDELauncherConstants.USE_CUSTOM_FEATURES, false);
			wc.setAttribute(IPDELauncherConstants.USE_DEFAULT, true);

			// clear config area by default
			wc.setAttribute(IPDELauncherConstants.CONFIG_CLEAR_AREA, true);
		} catch (CoreException e) {
			Shell shell = Display.getCurrent().getActiveShell();
			ErrorDialog.openError(shell, "Error",
					"Cannot execute initalize configuration", e.getStatus());
		}
	}

	/** Find the working directory based on this properties file. */
	public static String findWorkingDirectory(IFile propertiesFile) {
		try {
			IProject project = propertiesFile.getProject();
			IPath parent = propertiesFile.getProjectRelativePath()
					.removeLastSegments(1);
			IFolder execFolder = project.getFolder(parent
					.append(DEFAULT_EXEC_DIR));
			if (!execFolder.exists())
				execFolder.create(true, true, null);
			IFolder launchFolder = project.getFolder(execFolder
					.getProjectRelativePath().append(
							extractName(propertiesFile)));
			if (!launchFolder.exists())
				launchFolder.create(true, true, null);
			return "${workspace_loc:"
					+ launchFolder.getFullPath().toString().substring(1) + "}";
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot create working directory", e);
		}
	}

	/** Extract the launch configuration name from the properties file. */
	public static String extractName(IFile propertiesFile) {
		IPath path = propertiesFile.getFullPath();
		IPath pathNoExt = path.removeFileExtension();
		return pathNoExt.segment(pathNoExt.segmentCount() - 1);

	}

	/** Expects properties file to be set as mapped resources */
	public static void updateLaunchConfiguration(
			ILaunchConfigurationWorkingCopy wc, Boolean isEclipse) {
		try {
			// Finds the properties file and load it
			IFile propertiesFile = (IFile) wc.getMappedResources()[0];
			propertiesFile.refreshLocal(IResource.DEPTH_ONE, null);
			Properties properties = readProperties(propertiesFile);

			// Extract information from the properties file
			Map<String, Integer> bundlesToStart = new TreeMap<String, Integer>();
			Map<String, String> systemPropertiesToAppend = new HashMap<String, String>();
			String applicationId = interpretProperties(properties,
					bundlesToStart, systemPropertiesToAppend);

			if (applicationId != null)
				wc.setAttribute(IPDELauncherConstants.APPLICATION,
						applicationId);
			else {
				if (isEclipse)
					throw new Exception("No application defined,"
							+ " please set the 'eclipse.application' property"
							+ " in the properties file");
			}

			// Define directories
			File workingDir = getWorkingDirectory(wc);
			File dataDir = new File(workingDir, DEFAULT_DATA_DIR);

			// Update the launch configuration accordingly
			updateLaunchConfiguration(wc, bundlesToStart,
					systemPropertiesToAppend, dataDir.getAbsolutePath(),
					isEclipse);
		} catch (Exception e) {
			e.printStackTrace();
			Shell shell = SlcIdeUiPlugin.getDefault().getWorkbench()
					.getActiveWorkbenchWindow().getShell();
			// Shell shell= Display.getCurrent().getActiveShell();
			ErrorDialog.openError(shell, "Error",
					"Cannot prepare launch configuration",
					new Status(IStatus.ERROR, SlcIdeUiPlugin.ID,
							e.getMessage(), e));
			return;
		}
	}

	/**
	 * Actually modifies the launch configuration in order to reflect the
	 * current state read from the properties file and the launch configuration
	 * UI.
	 */
	protected static void updateLaunchConfiguration(
			ILaunchConfigurationWorkingCopy wc,
			Map<String, Integer> bundlesToStart,
			Map<String, String> systemPropertiesToAppend, String dataDir,
			Boolean isEclipse) throws CoreException {
		// Convert bundle lists
		final String targetBundles;
		final String wkSpaceBundles;
		if (wc.getAttribute(ATTR_SYNC_BUNDLES, true)) {
			StringBuffer tBuf = new StringBuffer();
			for (IPluginModelBase model : PluginRegistry.getExternalModels()) {
				tBuf.append(model.getBundleDescription().getSymbolicName());
				tBuf.append(',');
			}
			targetBundles = tBuf.toString();
			StringBuffer wBuf = new StringBuffer();
			models: for (IPluginModelBase model : PluginRegistry
					.getWorkspaceModels()) {
				if (model.getBundleDescription() == null) {
					System.err.println("No bundle description for " + model);
					continue models;
				}
				wBuf.append(model.getBundleDescription().getSymbolicName());
				wBuf.append(',');
			}
			wkSpaceBundles = wBuf.toString();
		} else {
			targetBundles = wc.getAttribute(targetBundlesAttr(isEclipse), "");
			wkSpaceBundles = wc.getAttribute(workspaceBundlesAttr(isEclipse),
					"");
		}
		wc.setAttribute(targetBundlesAttr(isEclipse),
				convertBundleList(bundlesToStart, targetBundles));

		wc.setAttribute(workspaceBundlesAttr(isEclipse),
				convertBundleList(bundlesToStart, wkSpaceBundles));

		// Update other default information
		wc.setAttribute(IPDELauncherConstants.DEFAULT_AUTO_START, false);

		// do NOT use custom features (both must be set)
		wc.setAttribute(IPDELauncherConstants.USE_CUSTOM_FEATURES, false);
		wc.setAttribute(IPDELauncherConstants.USE_DEFAULT, true);

		// VM arguments (system properties)
		String defaultVmArgs = wc.getAttribute(
				OsgiLauncherConstants.ATTR_DEFAULT_VM_ARGS, "");
		StringBuffer vmArgs = new StringBuffer(defaultVmArgs);

		// Data dir system property
		if (dataDir != null) {
			addSysProperty(vmArgs, OsgiLauncherConstants.ARGEO_OSGI_DATA_DIR,
					dataDir);
			if (isEclipse) {
				wc.setAttribute(IPDELauncherConstants.LOCATION, dataDir);
			}
		}

		// Add locations of JVMs
		if (wc.getAttribute(ATTR_ADD_JVM_PATHS, false))
			addVms(vmArgs);

		// Add other system properties
		for (String key : systemPropertiesToAppend.keySet())
			addSysProperty(vmArgs, key, systemPropertiesToAppend.get(key));

		vmArgs.append(" ").append(wc.getAttribute(ATTR_ADDITIONAL_VM_ARGS, ""));

		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
				vmArgs.toString());

		// Program arguments
		StringBuffer progArgs = new StringBuffer("");
		if (dataDir != null) {
			progArgs.append("-data ");
			progArgs.append(surroundSpaces(dataDir));

			if (wc.getAttribute(ATTR_CLEAR_DATA_DIRECTORY, false)) {
				File dataDirFile = new File(dataDir);
				deleteDir(dataDirFile);
				dataDirFile.mkdirs();
			}
		}
		String additionalProgramArgs = wc.getAttribute(
				OsgiLauncherConstants.ATTR_ADDITIONAL_PROGRAM_ARGS, "");
		progArgs.append(' ').append(additionalProgramArgs);
		wc.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
				progArgs.toString());
	}

	/** The launch configuration attribute to use for target bundles */
	protected static String targetBundlesAttr(Boolean isEclipse) {
		return isEclipse ? IPDELauncherConstants.SELECTED_TARGET_PLUGINS
				: IPDELauncherConstants.TARGET_BUNDLES;
	}

	/** The launch configuration attribute to use for workspace bundles */
	protected static String workspaceBundlesAttr(Boolean isEclipse) {
		return isEclipse ? IPDELauncherConstants.SELECTED_WORKSPACE_PLUGINS
				: IPDELauncherConstants.WORKSPACE_BUNDLES;
	}

	/**
	 * Interprets special properties and register the others as system
	 * properties to append.
	 * 
	 * @return the application id defined by
	 *         {@link OsgiLauncherConstants#ECLIPSE_APPLICATION}, or null if not
	 *         found
	 */
	protected static String interpretProperties(Properties properties,
			Map<String, Integer> bundlesToStart,
			Map<String, String> systemPropertiesToAppend) {
		// String argeoOsgiStart = properties
		// .getProperty(OsgiLauncherConstants.ARGEO_OSGI_START);
		// if (argeoOsgiStart != null) {
		// StringTokenizer st = new StringTokenizer(argeoOsgiStart, ",");
		// while (st.hasMoreTokens())
		// bundlesToStart.add(st.nextToken());
		// }

		computeBundlesToStart(bundlesToStart, properties, null);

		String applicationId = null;
		propKeys: for (Object keyObj : properties.keySet()) {
			String key = keyObj.toString();
			if (OsgiLauncherConstants.ARGEO_OSGI_START.equals(key))
				continue propKeys;
			if (key.startsWith(OsgiLauncherConstants.ARGEO_OSGI_START + "."))
				continue propKeys;
			else if (OsgiLauncherConstants.ARGEO_OSGI_BUNDLES.equals(key))
				continue propKeys;
			else if (OsgiLauncherConstants.ARGEO_OSGI_LOCATIONS.equals(key))
				continue propKeys;
			else if (OsgiLauncherConstants.OSGI_BUNDLES.equals(key))
				continue propKeys;
			else if (OsgiLauncherConstants.ECLIPSE_APPLICATION.equals(key))
				applicationId = properties.getProperty(key);
			else
				systemPropertiesToAppend.put(key, properties.getProperty(key));
		}
		return applicationId;
	}

	/** Adds a regular system property. */
	protected static void addSysProperty(StringBuffer vmArgs, String key,
			String value) {
		surroundSpaces(value);
		String str = "-D" + key + "=" + value;
		vmArgs.append(' ').append(str);
	}

	/** Adds JVMS registered in the workspace as special system properties. */
	protected static void addVms(StringBuffer vmArgs) {
		addVmSysProperty(vmArgs, "default", JavaRuntime.getDefaultVMInstall());
		IVMInstallType[] vmTypes = JavaRuntime.getVMInstallTypes();
		for (IVMInstallType vmType : vmTypes) {
			for (IVMInstall vmInstall : vmType.getVMInstalls()) {
				// printVm("", vmInstall);
				// properties based on name
				addVmSysProperty(vmArgs, vmInstall.getName(), vmInstall);
				if (vmInstall instanceof IVMInstall2) {
					// properties based on version
					IVMInstall2 vmInstall2 = (IVMInstall2) vmInstall;
					String version = vmInstall2.getJavaVersion();
					addVmSysProperty(vmArgs, version, vmInstall);

					List<String> tokens = new ArrayList<String>();
					StringTokenizer st = new StringTokenizer(version, ".");
					while (st.hasMoreTokens())
						tokens.add(st.nextToken());
					if (tokens.size() >= 2)
						addVmSysProperty(vmArgs,
								tokens.get(0) + "." + tokens.get(1), vmInstall);
				}
			}
		}

	}

	/** Adds a special system property pointing to one of the registered JVMs. */
	protected static void addVmSysProperty(StringBuffer vmArgs, String suffix,
			IVMInstall vmInstall) {
		addSysProperty(vmArgs, OsgiLauncherConstants.VMS_PROPERTY_PREFIX + "."
				+ suffix, vmInstall.getInstallLocation().getPath());
	}

	/** Surround the string with quotes if it contains spaces. */
	protected static String surroundSpaces(String str) {
		if (str.indexOf(' ') >= 0)
			return '\"' + str + '\"';
		else
			return str;
	}

	/**
	 * Reformat the bundle list in order to reflect which bundles have to be
	 * started.
	 */
	protected static String convertBundleList(
			Map<String, Integer> bundlesToStart, String original) {
		if (debug)
			debug("Original bundle list: " + original);

		StringTokenizer stComa = new StringTokenizer(original, ",");
		// sort by bundle symbolic name
		Set<String> bundleIds = new TreeSet<String>();
		bundles: while (stComa.hasMoreTokens()) {

			String bundleId = stComa.nextToken();
			if (bundleId.indexOf('*') >= 0)
				throw new RuntimeException(
						"Bundle id "
								+ bundleId
								+ " not properly formatted, clean your workspace projects");

			int indexAt = bundleId.indexOf('@');
			if (indexAt >= 0) {
				bundleId = bundleId.substring(0, indexAt);
			}

			// We can now rely on bundleId value

			if (bundleId.endsWith(".source")) {
				debug("Skip source bundle " + bundleId);
				continue bundles;
			} else if (bundleId
					.equals(IPDEBuildConstants.BUNDLE_SIMPLE_CONFIGURATOR)) {
				// skip simple configurator in order to avoid side-effects
				continue bundles;
			}
			bundleIds.add(bundleId);
		}

		StringBuffer bufBundles = new StringBuffer(1024);
		boolean first = true;
		for (String bundleId : bundleIds) {
			if (first)
				first = false;
			else
				bufBundles.append(',');
			boolean modified = false;
			if (bundlesToStart.containsKey(bundleId)) {
				Integer startLevel = bundlesToStart.get(bundleId);
				String startLevelStr = startLevel != null ? startLevel
						.toString() : "default";
				bufBundles.append(bundleId).append('@').append(startLevelStr)
						.append(":true");
				modified = true;
				debug("Will start " + bundleId + " at level " + startLevelStr);
			}

			if (!modified)
				bufBundles.append(bundleId);

		}
		String output = bufBundles.toString();
		return output;
	}

	// UTILITIES
	/** Recursively deletes a directory tree. */
	private static void deleteDir(File dir) {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory())
				deleteDir(file);
			else
				file.delete();
		}
		dir.delete();
	}

	/** Loads a properties file. */
	private static Properties readProperties(IFile file) throws CoreException {
		Properties props = new Properties();

		InputStream in = null;
		try {
			in = file.getContents();
			props.load(in);
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR,
					SlcIdeUiPlugin.ID, "Cannot read properties file", e));
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					// silent
				}
		}
		return props;
	}

	/** Determines the start levels for the bundles */
	private static void computeBundlesToStart(
			Map<String, Integer> bundlesToStart, Properties properties,
			Integer defaultStartLevel) {

		// default (and previously, only behaviour)
		appendBundlesToStart(bundlesToStart, defaultStartLevel,
				properties.getProperty(OsgiLauncherConstants.ARGEO_OSGI_START,
						""));

		// list argeo.osgi.start.* system properties
		Iterator<Object> keys = properties.keySet().iterator();
		final String prefix = OsgiLauncherConstants.ARGEO_OSGI_START + ".";
		while (keys.hasNext()) {
			String key = (String) keys.next();
			if (key.startsWith(prefix)) {
				Integer startLevel;
				String suffix = key.substring(prefix.length());
				String[] tokens = suffix.split("\\.");
				if (tokens.length > 0 && !tokens[0].trim().equals(""))
					try {
						// first token is start level
						startLevel = new Integer(tokens[0]);
					} catch (NumberFormatException e) {
						startLevel = defaultStartLevel;
					}
				else
					startLevel = defaultStartLevel;

				// append bundle names
				String bundleNames = properties.getProperty(key);
				appendBundlesToStart(bundlesToStart, startLevel, bundleNames);
			}
		}
	}

	/** Append a comma-separated list of bundles to the start levels. */
	private static void appendBundlesToStart(
			Map<String, Integer> bundlesToStart, Integer startLevel, String str) {
		if (str == null || str.trim().equals(""))
			return;

		String[] bundleNames = str.split(",");
		for (int i = 0; i < bundleNames.length; i++) {
			if (bundleNames[i] != null && !bundleNames[i].trim().equals(""))
				bundlesToStart.put(bundleNames[i], startLevel);
		}
	}

	/*
	 * HACKED UTILITIES
	 */
	// Hacked from
	// org.eclipse.pde.internal.ui.launcher.LaunchArgumentsHelper.getWorkingDirectory(ILaunchConfiguration)
	private static File getWorkingDirectory(ILaunchConfiguration configuration)
			throws CoreException {
		String working;
		try {
			working = configuration.getAttribute(
					IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
					new File(".").getCanonicalPath()); //$NON-NLS-1$
		} catch (IOException e) {
			working = "${workspace_loc}/../"; //$NON-NLS-1$
		}
		File dir;
		try {
			dir = new File(getSubstitutedString(working));
		} catch (Exception e) {
			// the directory was most probably deleted
			IFile propertiesFile = (IFile) configuration.getMappedResources()[0];
			working = findWorkingDirectory(propertiesFile);
			dir = new File(getSubstitutedString(working));
		}
		if (!dir.exists())
			dir.mkdirs();
		return dir;
	}

	// Hacked from
	// org.eclipse.pde.internal.ui.launcher.LaunchArgumentsHelper.getSubstitutedString(String)
	private static String getSubstitutedString(String text)
			throws CoreException {
		if (text == null)
			return ""; //$NON-NLS-1$
		IStringVariableManager mgr = VariablesPlugin.getDefault()
				.getStringVariableManager();
		return mgr.performStringSubstitution(text);
	}

	/**
	 * Not used anymore, but kept because this routine may be useful in the
	 * future.
	 */
	protected void addSelectedProjects(StringBuffer name, ISelection selection,
			List<String> bundlesToStart) {
		Assert.isNotNull(selection);

		Map<String, IPluginModelBase> bundleProjects = new HashMap<String, IPluginModelBase>();
		for (IPluginModelBase modelBase : PluginRegistry.getWorkspaceModels()) {
			IProject bundleProject = modelBase.getUnderlyingResource()
					.getProject();
			bundleProjects.put(bundleProject.getName(), modelBase);
		}

		IStructuredSelection sSelection = (IStructuredSelection) selection;
		for (Iterator<?> it = sSelection.iterator(); it.hasNext();) {
			Object obj = it.next();
			if (obj instanceof IProject) {
				IProject project = (IProject) obj;
				if (bundleProjects.containsKey(project.getName())) {
					IPluginModelBase modelBase = bundleProjects.get(project
							.getName());

					BundleDescription bundleDescription = null;
					if (modelBase.isFragmentModel()) {
						BundleDescription[] hosts = modelBase
								.getBundleDescription().getHost().getHosts();
						for (BundleDescription bd : hosts) {
							if (debug)
								System.out.println("Host for "
										+ modelBase.getBundleDescription()
												.getSymbolicName() + ": "
										+ bd.getSymbolicName());
							bundleDescription = bd;
						}
					} else {
						bundleDescription = modelBase.getBundleDescription();
					}

					if (bundleDescription != null) {
						String symbolicName = bundleDescription
								.getSymbolicName();
						String bundleName = bundleDescription.getName();

						bundlesToStart.add(symbolicName);

						if (name.length() > 0)
							name.append(" ");
						if (bundleName != null)
							name.append(bundleName);
						else
							name.append(symbolicName);
					}
				}
			}
		}
	}

	static void debug(Object obj) {
		if (debug)
			System.out.println(obj);
	}

}
