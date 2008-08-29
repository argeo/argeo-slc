package org.argeo.slc.ui.launch.script;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.argeo.slc.ui.launch.DeployedSlcSystem;
import org.argeo.slc.ui.launch.EmbeddedSlcSystem;
import org.argeo.slc.ui.launch.SlcSystem;
import org.argeo.slc.ui.launch.SlcUiLaunchPlugin;
import org.argeo.slc.ui.launch.preferences.SlcPreferencePage;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;

public class SlcScriptLaunchDelegate extends
		AbstractJavaLaunchConfigurationDelegate {
	public static final String ID = "org.argeo.slc.launch.slcScriptLaunchType";

	public final static String ANT_MAIN = "org.apache.tools.ant.Main";
	public final static String SLC_MAIN = "org.argeo.slc.cli.SlcMain";

	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		if (!saveBeforeLaunch(configuration, mode, monitor))
			return;

		String scriptLocation = configuration.getAttribute(
				SlcScriptUtils.ATTR_SCRIPT, "");
		if (scriptLocation.equals(""))
			abort("Script has to be provided", null, 1);

		IStringVariableManager manager = VariablesPlugin.getDefault()
				.getStringVariableManager();
		scriptLocation = manager.performStringSubstitution(scriptLocation);
		IPath path = new Path(scriptLocation);
		IFile[] files = ResourcesPlugin.getWorkspace().getRoot()
				.findFilesForLocation(path);

		if (files.length == 0)
			abort("Coulkd not find related file", null, 1);

		IFile file = (IFile) files[0];
		DebugPlugin
				.logMessage("Launching " + file.getLocation().toFile(), null);

		boolean pre093 = configuration.getAttribute(SlcScriptUtils.ATTR_PRE093,
				false);

		// Retrieve SLC Runtime
		SlcSystem slcSystem = findSlcSystem(file, pre093);
		if (slcSystem == null)
			return;

		IVMRunner vmRunner = slcSystem.getVmInstall().getVMRunner(mode);
		final VMRunnerConfiguration vmConfig;
		if (pre093) {
			vmConfig = createPre093Config(slcSystem, file, mode);
		} else {
			vmConfig = createConfig(slcSystem, file, mode, configuration);
		}
		vmRunner.run(vmConfig, launch, monitor);
	}

	protected SlcSystem findSlcSystem(IFile file, boolean pre093)
			throws CoreException {
		SlcSystem slcSystem = null;

		IProject project = file.getProject();
		if (project.getNature("org.eclipse.jdt.core.javanature") != null) {
			IJavaProject javaProject = JavaCore.create(project);
			if (checkProjectForEmbedded(javaProject, pre093)) {
				slcSystem = new EmbeddedSlcSystem(javaProject);
			}
		}

		if (slcSystem == null) {
			String slcRuntimePath = SlcUiLaunchPlugin.getDefault()
					.getPreferenceStore().getString(
							SlcPreferencePage.PREF_SLC_RUNTIME_LOCATION);
			if (slcRuntimePath == null || slcRuntimePath.equals("")) {
				showError("SLC Runtime path is not set. Set it in Windows > Preferences > SLC");
				return null;
			}

			slcSystem = new DeployedSlcSystem(slcRuntimePath);
		}

		return slcSystem;
	}

	protected boolean checkProjectForEmbedded(IJavaProject project,
			boolean pre093) {
		try {
			IType mainType = null;
			if (pre093)
				mainType = project.findType(ANT_MAIN);
			else
				mainType = project.findType(SLC_MAIN);

			if (mainType == null)
				return false;
			else
				return true;
		} catch (JavaModelException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Regular SLC
	protected VMRunnerConfiguration createConfig(SlcSystem deployedSlc,
			IFile file, String mode, ILaunchConfiguration configuration)
			throws CoreException {
		VMRunnerConfiguration vmConfig = new VMRunnerConfiguration(SLC_MAIN,
				deployedSlc.getClasspath());
		vmConfig.setVMArguments(getVmArguments(deployedSlc));
		vmConfig.setWorkingDirectory(file.getLocation().toFile().getParent());
		vmConfig.setProgramArguments(getProgramArguments(deployedSlc, file,
				mode, configuration));
		return vmConfig;
	}

	protected String[] getVmArguments(SlcSystem deployedSlc) {
		List<String> list = new Vector<String>();
		if (deployedSlc.getJavaLibraryPath() != null)
			list.add("-Djava.library.path=" + deployedSlc.getJavaLibraryPath());
		return list.toArray(new String[list.size()]);
	}

	protected String[] getProgramArguments(SlcSystem deployedSlc, IFile file,
			String mode, ILaunchConfiguration configuration)
			throws CoreException {
		List<String> list = new Vector<String>();

		list.add("--mode");
		list.add("single");

		// Script
		list.add("--script");
		list.add(file.getLocation().toFile().getAbsolutePath());

		// Runtime
		String runtime = configuration.getAttribute(
				SlcScriptUtils.ATTR_RUNTIME, "");
		if (!runtime.equals("")) {
			list.add("--runtime");
			list.add(runtime);
		}

		// Targets
		String targets = configuration.getAttribute(
				SlcScriptUtils.ATTR_TARGETS, "");
		if (!targets.equals("")) {
			list.add("--targets");
			list.add(targets);
		}

		// Properties
		Properties properties = new Properties();
		String str = configuration.getAttribute(SlcScriptUtils.ATTR_PROPERTIES,
				"");
		ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes());
		try {
			properties.load(in);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read properties", e);
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					// silent
				}
		}

		for (Object key : properties.keySet()) {
			list.add("-p");
			StringBuffer buf = new StringBuffer("");
			buf.append(key).append('=').append(properties.get(key));
			list.add(buf.toString());
		}

		// Debug mode
		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			list.add("--property");
			list.add("log4j.logger.org.argeo.slc=DEBUG");
		}
		return list.toArray(new String[list.size()]);
	}

	// Pre SLC v0.9.3
	protected VMRunnerConfiguration createPre093Config(SlcSystem deployedSlc,
			IFile file, String mode) throws CoreException {
		VMRunnerConfiguration vmConfig = new VMRunnerConfiguration(ANT_MAIN,
				deployedSlc.getClasspath());
		vmConfig.setVMArguments(getPre093VmArguments(deployedSlc));
		vmConfig.setWorkingDirectory(file.getLocation().toFile().getParent());
		vmConfig.setProgramArguments(getPre093ProgramArguments(deployedSlc,
				file, mode));
		return vmConfig;
	}

	protected String[] getPre093VmArguments(SlcSystem deployedSlc) {
		List<String> list = new Vector<String>();
		// list.add("-Dant.home=" + deployedSlc.getAntHome());
		if (deployedSlc.getJavaLibraryPath() != null)
			list.add("-Djava.library.path=" + deployedSlc.getJavaLibraryPath());
		return list.toArray(new String[list.size()]);
	}

	protected String[] getPre093ProgramArguments(SlcSystem deployedSlc,
			IFile file, String mode) {
		List<String> list = new Vector<String>();
		list.add("-f");
		list.add(file.getLocation().toFile().getAbsolutePath());
		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			list.add("-d");
		}
		return list.toArray(new String[list.size()]);
	}

	// Utilities
	private static void showError(String message) {
		Shell shell = SlcUiLaunchPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getShell();

		IStatus status = new Status(IStatus.ERROR, SlcUiLaunchPlugin.ID,
				message);
		ErrorDialog.openError(shell, "Error", "Cannot launch SLC script",
				status);
	}

}
