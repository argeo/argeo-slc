package org.argeo.slc.ide.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.launching.IVMInstall;

public interface SlcSystem {
	public String[] getClasspath() throws CoreException;
	public String getJavaLibraryPath();
	public IVMInstall getVmInstall() throws CoreException;
}
