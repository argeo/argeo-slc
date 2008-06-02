package org.argeo.slc.ui.launch.script;

import java.util.List;
import java.util.Vector;

import org.argeo.slc.ui.launch.DeployedSlcRuntime;
import org.argeo.slc.ui.launch.EmbeddedSlcRuntime;
import org.argeo.slc.ui.launch.SlcRuntime;
import org.argeo.slc.ui.launch.SlcUiLaunchPlugin;
import org.argeo.slc.ui.launch.preferences.SlcPreferencePage;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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

	private final static String ANT_MAIN = "org.apache.tools.ant.Main";

	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		IResource[] resources = configuration.getMappedResources();
		if (resources.length != 1) {
			throw new RuntimeException("Can only launch one script.");
		}
		if (!(resources[0] instanceof IFile)) {
			throw new RuntimeException("Can only launch file.");
		}
		IFile file = (IFile) resources[0];
		System.out.println("Launched " + file.getLocation().toFile());

		// Retrieve SLC Runtime
		SlcRuntime deployedSlc = null;

		IProject project = file.getProject();
		if (project.getNature("org.eclipse.jdt.core.javanature") != null) {
			IJavaProject javaProject = JavaCore.create(project);
			if (checkProjectForEmbedded(javaProject)) {
				deployedSlc = new EmbeddedSlcRuntime(javaProject);
			}
		}

		if (deployedSlc == null) {
			String slcRuntimePath = SlcUiLaunchPlugin.getDefault()
					.getPreferenceStore().getString(
							SlcPreferencePage.PREF_SLC_RUNTIME_LOCATION);
			if (slcRuntimePath == null || slcRuntimePath.equals("")) {
				showError("SLC Runtime path is not set. Set it in Windows > Preferences > SLC");
				return;
			}

			deployedSlc = new DeployedSlcRuntime(slcRuntimePath);
		}

		IVMRunner vmRunner = deployedSlc.getVmInstall().getVMRunner(mode);
		VMRunnerConfiguration vmConfig = new VMRunnerConfiguration(ANT_MAIN,
				deployedSlc.getClasspath());
		vmConfig.setVMArguments(getVmArguments(deployedSlc));
		vmConfig.setWorkingDirectory(file.getLocation().toFile().getParent());
		vmConfig.setProgramArguments(getProgramArguments(deployedSlc, file,
				mode));
		vmRunner.run(vmConfig, launch, null);
	}

	private String[] getVmArguments(SlcRuntime deployedSlc) {
		List<String> list = new Vector<String>();
		// list.add("-Dant.home=" + deployedSlc.getAntHome());
		if (deployedSlc.getJavaLibraryPath() != null)
			list.add("-Djava.library.path=" + deployedSlc.getJavaLibraryPath());
		return list.toArray(new String[list.size()]);
	}

	private String[] getProgramArguments(SlcRuntime deployedSlc, IFile file,
			String mode) {
		List<String> list = new Vector<String>();
		list.add("-f");
		list.add(file.getLocation().toFile().getAbsolutePath());
		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			list.add("-d");
		}
		return list.toArray(new String[list.size()]);
	}

	private void showError(String message) {
		Shell shell = SlcUiLaunchPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getShell();

		IStatus status = new Status(IStatus.ERROR, SlcUiLaunchPlugin.ID,
				message);
		ErrorDialog.openError(shell, "Error", "Cannot launch SLC script",
				status);
	}

	protected boolean checkProjectForEmbedded(IJavaProject project) {
		try {
			IType antmainType = project.findType(ANT_MAIN);
			if (antmainType == null)
				return false;
			else
				return true;
		} catch (JavaModelException e) {
			e.printStackTrace();
			return false;
		}
	}
}
