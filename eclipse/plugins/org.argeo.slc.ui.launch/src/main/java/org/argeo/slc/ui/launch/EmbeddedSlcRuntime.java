package org.argeo.slc.ui.launch;

import org.argeo.slc.ui.launch.preferences.SlcPreferencePage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;

public class EmbeddedSlcRuntime implements SlcRuntime {
	private final IJavaProject project;

	public EmbeddedSlcRuntime(IJavaProject project) {
		this.project = project;
	}

	@Override
	public String[] getClasspath() throws CoreException {
		return JavaRuntime.computeDefaultRuntimeClassPath(project);
	}

	@Override
	public String getJavaLibraryPath() {
		String javaLibPath = SlcUiLaunchPlugin.getDefault()
				.getPreferenceStore().getString(
						SlcPreferencePage.PREF_SLC_RUNTIME_LOCATION);
		if (javaLibPath == null || javaLibPath.equals(""))
			return null;
		else
			return javaLibPath;
	}

	@Override
	public IVMInstall getVmInstall() throws CoreException {
		return JavaRuntime.getVMInstall(project);
	}

}
