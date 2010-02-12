package org.argeo.slc.ide.ui.launch.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.argeo.slc.ide.ui.SlcIdeUiPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.pde.ui.launcher.IPDELauncherConstants;

public class OsgiLaunchHelper {
	private static Boolean debug = false;

	public static void updateLaunchConfiguration(
			ILaunchConfigurationWorkingCopy configuration,
			List<String> bundlesToStart,
			Map<String, String> systemPropertiesToAppend, String dir)
			throws CoreException {
		// Convert bundle lists
		String targetBundles = configuration.getAttribute(
				IPDELauncherConstants.TARGET_BUNDLES, "");
		configuration.setAttribute(IPDELauncherConstants.TARGET_BUNDLES,
				convertBundleList(bundlesToStart, targetBundles));

		String wkSpaceBundles = configuration.getAttribute(
				IPDELauncherConstants.WORKSPACE_BUNDLES, "");
		configuration.setAttribute(IPDELauncherConstants.WORKSPACE_BUNDLES,
				convertBundleList(bundlesToStart, wkSpaceBundles));

		// Update other default information
		configuration.setAttribute(IPDELauncherConstants.DEFAULT_AUTO_START,
				false);
		configuration.setAttribute(IPDELauncherConstants.CONFIG_CLEAR_AREA,
				true);
		String defaultVmArgs = configuration.getAttribute(
				OsgiLauncherConstants.ATTR_DEFAULT_VM_ARGS, "");
		StringBuffer vmArgs = new StringBuffer(defaultVmArgs);
		vmArgs.append(" -Xmx256m");

		// Add locations of JVMs
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

		// Add other system properties
		for (String key : systemPropertiesToAppend.keySet())
			addSysProperty(vmArgs, key, systemPropertiesToAppend.get(key));

		configuration.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArgs
						.toString());

		// String dir = findWorkingDirectory();
		if (dir != null)
			configuration.setAttribute(
					IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
					dir);

	}

	protected static void addVmSysProperty(StringBuffer vmArgs, String suffix,
			IVMInstall vmInstall) {
		addSysProperty(vmArgs, OsgiLauncherConstants.VMS_PROPERTY_PREFIX + "."
				+ suffix, vmInstall.getInstallLocation().getPath());
	}

	protected static void addSysProperty(StringBuffer vmArgs, String key,
			String value) {
		String str = "-D" + key + "=" + value;
		if (str.contains(" "))
			str = "\"" + str + "\"";
		vmArgs.append(" " + str);
	}

	protected static String convertBundleList(List<String> bundlesToStart,
			String original) {
		StringBuffer bufBundles = new StringBuffer(1024);
		StringTokenizer stComa = new StringTokenizer(original, ",");
		boolean first = true;
		while (stComa.hasMoreTokens()) {
			if (first)
				first = false;
			else
				bufBundles.append(',');

			String tkComa = stComa.nextToken();
			int indexAt = tkComa.indexOf('@');
			boolean modified = false;
			if (indexAt >= 0) {
				String bundelId = tkComa.substring(0, indexAt);

				if (bundlesToStart.contains(bundelId)) {
					bufBundles.append(bundelId).append('@').append(
							"default:true");
					modified = true;
					if (debug)
						System.out.println("Will start " + bundelId);
				}
			}

			if (!modified)
				bufBundles.append(tkComa);
		}
		String output = bufBundles.toString();
		return output;
	}

	protected static Properties readProperties(IFile file) throws CoreException {
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

	protected static void interpretProperties(Properties properties,
			List<String> bundlesToStart,
			Map<String, String> systemPropertiesToAppend) {
		String argeoOsgiStart = properties
				.getProperty(OsgiLauncherConstants.ARGEO_OSGI_START);
		StringTokenizer st = new StringTokenizer(argeoOsgiStart, ",");
		while (st.hasMoreTokens())
			bundlesToStart.add(st.nextToken());

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
}
