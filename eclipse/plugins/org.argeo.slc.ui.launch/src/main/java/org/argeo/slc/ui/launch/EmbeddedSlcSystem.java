package org.argeo.slc.ui.launch;

import org.argeo.slc.ui.launch.preferences.SlcPreferencePage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;

public class EmbeddedSlcSystem implements SlcSystem {
	private final IJavaProject project;

	public EmbeddedSlcSystem(IJavaProject project) {
		this.project = project;
	}

	public String[] getClasspath() throws CoreException {
		return JavaRuntime.computeDefaultRuntimeClassPath(project);
	}

	public String getJavaLibraryPath() {
		String javaLibPath = SlcUiLaunchPlugin.getDefault()
				.getPreferenceStore().getString(
						SlcPreferencePage.PREF_SLC_RUNTIME_LOCATION);
		if (javaLibPath == null || javaLibPath.equals(""))
			return null;
		else
			return javaLibPath;
	}

	public IVMInstall getVmInstall() throws CoreException {
		return JavaRuntime.getVMInstall(project);
	}

}
