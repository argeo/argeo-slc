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
import java.util.StringTokenizer;

import org.argeo.slc.ide.ui.SlcIdeUiPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
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
import org.eclipse.pde.ui.launcher.IPDELauncherConstants;
import org.eclipse.swt.widgets.Display;

public class OsgiLaunchHelper implements OsgiLauncherConstants {
	private static Boolean debug = false;

	/** Expects properties file to be set as mapped resources */
	public static void updateLaunchConfiguration(
			ILaunchConfigurationWorkingCopy configuration) {
		try {
			// Finds the properties file and load it
			IFile propertiesFile = (IFile) configuration.getMappedResources()[0];
			propertiesFile.refreshLocal(IResource.DEPTH_ONE, null);
			Properties properties = readProperties(propertiesFile);

			// Extract information from the properties file
			List<String> bundlesToStart = new ArrayList<String>();
			Map<String, String> systemPropertiesToAppend = new HashMap<String, String>();
			interpretProperties(properties, bundlesToStart,
					systemPropertiesToAppend);

			// Define directories
			File workingDir = getWorkingDirectory(configuration);
			File dataDir = new File(workingDir, "data");

			// Update the launch configuration accordingly
			updateLaunchConfiguration(configuration, bundlesToStart,
					systemPropertiesToAppend, dataDir.getAbsolutePath());
		} catch (Exception e) {
			ErrorDialog.openError(Display.getCurrent().getActiveShell(),
					"Error", "Cannot read properties",
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
			ILaunchConfigurationWorkingCopy configuration,
			List<String> bundlesToStart,
			Map<String, String> systemPropertiesToAppend, String dataDir)
			throws CoreException {
		// Convert bundle lists
		final String targetBundles;
		final String wkSpaceBundles;
		if (configuration.getAttribute(ATTR_SYNC_BUNDLES, true)) {
			StringBuffer tBuf = new StringBuffer();
			for (IPluginModelBase model : PluginRegistry.getExternalModels()) {
				tBuf.append(model.getBundleDescription().getSymbolicName());
				tBuf.append(',');
			}
			targetBundles = tBuf.toString();
			StringBuffer wBuf = new StringBuffer();
			for (IPluginModelBase model : PluginRegistry.getWorkspaceModels()) {
				wBuf.append(model.getBundleDescription().getSymbolicName());
				wBuf.append(',');
			}
			wkSpaceBundles = wBuf.toString();
		} else {
			targetBundles = configuration.getAttribute(
					IPDELauncherConstants.TARGET_BUNDLES, "");
			wkSpaceBundles = configuration.getAttribute(
					IPDELauncherConstants.WORKSPACE_BUNDLES, "");
		}
		configuration.setAttribute(IPDELauncherConstants.TARGET_BUNDLES,
				convertBundleList(bundlesToStart, targetBundles));

		configuration.setAttribute(IPDELauncherConstants.WORKSPACE_BUNDLES,
				convertBundleList(bundlesToStart, wkSpaceBundles));

		// Update other default information
		configuration.setAttribute(IPDELauncherConstants.DEFAULT_AUTO_START,
				false);

		// VM arguments (system properties)
		String defaultVmArgs = configuration.getAttribute(
				OsgiLauncherConstants.ATTR_DEFAULT_VM_ARGS, "");
		StringBuffer vmArgs = new StringBuffer(defaultVmArgs);

		// Data dir system property
		if (dataDir != null)
			addSysProperty(vmArgs, OsgiLauncherConstants.ARGEO_OSGI_DATA_DIR,
					dataDir);
		// Add locations of JVMs
		if (configuration.getAttribute(ATTR_ADD_JVM_PATHS, false))
			addVms(vmArgs);

		// Add other system properties
		for (String key : systemPropertiesToAppend.keySet())
			addSysProperty(vmArgs, key, systemPropertiesToAppend.get(key));

		vmArgs.append(" ").append(
				configuration.getAttribute(ATTR_ADDITIONAL_VM_ARGS, ""));

		configuration.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArgs
						.toString());

		// Program arguments
		StringBuffer progArgs = new StringBuffer("");
		if (dataDir != null) {
			progArgs.append("-data ");
			progArgs.append(surroundSpaces(dataDir));

			if (configuration.getAttribute(ATTR_CLEAR_DATA_DIRECTORY, false)) {
				File dataDirFile = new File(dataDir);
				deleteDir(dataDirFile);
				dataDirFile.mkdirs();
			}
		}
		String additionalProgramArgs = configuration.getAttribute(
				OsgiLauncherConstants.ATTR_ADDITIONAL_PROGRAM_ARGS, "");
		progArgs.append(' ').append(additionalProgramArgs);
		configuration.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
				progArgs.toString());
	}

	/**
	 * Interprets special properties and register the others as system
	 * properties to append.
	 */
	protected static void interpretProperties(Properties properties,
			List<String> bundlesToStart,
			Map<String, String> systemPropertiesToAppend) {
		String argeoOsgiStart = properties
				.getProperty(OsgiLauncherConstants.ARGEO_OSGI_START);
		if (argeoOsgiStart != null) {
			StringTokenizer st = new StringTokenizer(argeoOsgiStart, ",");
			while (st.hasMoreTokens())
				bundlesToStart.add(st.nextToken());
		}

		propKeys: for (Object keyObj : properties.keySet()) {
			String key = keyObj.toString();
			if (OsgiLauncherConstants.ARGEO_OSGI_START.equals(key))
				continue propKeys;
			else if (OsgiLauncherConstants.ARGEO_OSGI_BUNDLES.equals(key))
				continue propKeys;
			else if (OsgiLauncherConstants.ARGEO_OSGI_LOCATIONS.equals(key))
				continue propKeys;
			else if (OsgiLauncherConstants.OSGI_BUNDLES.equals(key))
				continue propKeys;
			else
				systemPropertiesToAppend.put(key, properties.getProperty(key));
		}

	}

	/** Adds a regular system property. */
	protected static void addSysProperty(StringBuffer vmArgs, String key,
			String value) {
		surroundSpaces(value);
		String str = "-D" + key + "=" + value;
		// surroundSpaces(str);
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
						addVmSysProperty(vmArgs, tokens.get(0) + "."
								+ tokens.get(1), vmInstall);
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
	protected static String convertBundleList(List<String> bundlesToStart,
			String original) {
		if (debug)
			System.out.println("Original bundle list: " + original);

		StringBuffer bufBundles = new StringBuffer(1024);
		StringTokenizer stComa = new StringTokenizer(original, ",");
		boolean first = true;
		bundles: while (stComa.hasMoreTokens()) {
			if (first)
				first = false;
			else
				bufBundles.append(',');

			String bundleId = stComa.nextToken();
			if (bundleId.indexOf('*') >= 0)
				throw new RuntimeException(
						"Bundle id "
								+ bundleId
								+ " not properly formatted, clean your workspace projects");

			int indexAt = bundleId.indexOf('@');
			boolean modified = false;
			if (indexAt >= 0) {
				bundleId = bundleId.substring(0, indexAt);
			}

			if (bundleId.endsWith(".source")) {
				if (debug)
					System.out.println("Skip source bundle " + bundleId);
				continue bundles;
			}

			if (bundlesToStart.contains(bundleId)) {
				bufBundles.append(bundleId).append('@').append("default:true");
				modified = true;
				if (debug)
					System.out.println("Will start " + bundleId);
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
		} catch (IOException e) {
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
		File dir = new File(getSubstitutedString(working));
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

}
