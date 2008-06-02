package org.argeo.slc.ui.launch;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;

public class DeployedSlcRuntime implements SlcRuntime {
	private File baseDir;
	private String relLibDir = "lib";

	public DeployedSlcRuntime(String baseDirPath) {
		try {
			this.baseDir = new File(baseDirPath).getCanonicalFile();
		} catch (IOException e) {
			throw new RuntimeException("Cannot get path for " + baseDirPath, e);
		}
	}

	@Override
	public String[] getClasspath() throws CoreException {
		List<String> classpath = new Vector<String>();
		File libDir = new File(baseDir.getPath() + File.separator + relLibDir);
		File[] files = libDir.listFiles();
		for (File file : files) {
			try {
				classpath.add(file.getCanonicalPath());
			} catch (IOException e) {
				throw new RuntimeException("Cannot get path for " + file, e);
			}
		}
		return classpath.toArray(new String[classpath.size()]);
	}

	@Override
	public IVMInstall getVmInstall() throws CoreException {
		return JavaRuntime.getDefaultVMInstall();
	}

	public String getAntHome() {
		return baseDir.getPath();
	}

	@Override
	public String getJavaLibraryPath() {
		return baseDir.getPath() + File.separator + "bin";
	}

}
