package org.argeo.slc.ide.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;

public class DeployedSlcSystem implements SlcSystem {
	private File baseDir;
	private String relLibDir = "lib";

	public DeployedSlcSystem(String baseDirPath) {
		try {
			this.baseDir = new File(baseDirPath).getCanonicalFile();
		} catch (IOException e) {
			throw new RuntimeException("Cannot get path for " + baseDirPath, e);
		}
	}

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

	public IVMInstall getVmInstall() throws CoreException {
		return JavaRuntime.getDefaultVMInstall();
	}

	public String getAntHome() {
		return baseDir.getPath();
	}

	public String getJavaLibraryPath() {
		return baseDir.getPath() + File.separator + "bin";
	}

}
