/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.rpmfactory.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.exec.Executor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.tasks.SystemCall;

/** Build an RPM in mock. */
public class BuildInMock implements Runnable {
	private final static Log log = LogFactory.getLog(BuildInMock.class);
	private final static String NOARCH = "noarch";

	private String rpmPackage = null;
	private String branch = null;
	private String arch = NOARCH;

	private RpmFactory factory;
	private Executor executor;

	private String debuginfoDirName = "debuginfo";

	public void run() {
		String mockCfg = factory.getMockConfig(arch);
		File mockConfigFile = factory.getMockConfigFile(arch, branch);

		// prepare mock call
		SystemCall mock = new SystemCall();
		if (arch != null)
			mock.arg("setarch").arg(arch);
		mock.arg("mock");
		mock.arg("-v");
		mock.arg("--configdir=" + mockConfigFile.getAbsoluteFile().getParent());
		if (arch != null)
			mock.arg("--arch=" + arch);
		mock.arg("-r").arg(mockCfg);
		mock.arg("--scm-enable");
		// mock.arg("--scm-option");
		// mock.arg("git_get='git clone " + (branch != null ? "-b " + branch :
		// "")
		// + " " + factory.getGitBaseUrl() + "/SCM_PKG SCM_PKG'");
		mock.arg("--scm-option").arg("package=" + rpmPackage);

		mock.setLogCommand(true);
		mock.setExecutor(executor);

		//
		// mock command execution
		//
		mock.run();
		//

		File stagingDir = factory
				.getWorkspaceDir(factory.getStagingWorkspace());
		File srpmDir = new File(stagingDir, "SRPMS");
		srpmDir.mkdirs();
		File archDir = null;
		File debuginfoDir = null;
		if (!arch.equals(NOARCH)) {
			archDir = new File(stagingDir, arch);
			debuginfoDir = new File(archDir, debuginfoDirName);
			debuginfoDir.mkdirs();
		}

		// copy RPMs
		Set<File> reposToRecreate = new HashSet<File>();
		File resultDir = factory.getResultDir(arch);
		rpms: for (File file : resultDir.listFiles()) {
			if (file.isDirectory())
				continue rpms;

			File[] targetDirs;
			if (file.getName().contains(".src.rpm"))
				targetDirs = new File[] { srpmDir };
			else if (file.getName().contains("-debuginfo-"))
				targetDirs = new File[] { debuginfoDir };
			else if (!arch.equals(NOARCH)
					&& file.getName().contains("." + arch + ".rpm"))
				targetDirs = new File[] { archDir };
			else if (file.getName().contains(".noarch.rpm")) {
				List<File> dirs = new ArrayList<File>();
				for (String arch : factory.getArchs())
					dirs.add(new File(stagingDir, arch));
				targetDirs = dirs.toArray(new File[dirs.size()]);
			} else if (file.getName().contains(".rpm"))
				throw new SlcException("Don't know where to copy " + file);
			else {
				if (log.isTraceEnabled())
					log.trace("Skip " + file);
				continue rpms;
			}

			reposToRecreate.addAll(Arrays.asList(targetDirs));
			copyToDirs(file, targetDirs);
		}

		// recreate changed repos
		for (File repoToRecreate : reposToRecreate) {
			SystemCall createrepo = new SystemCall();
			createrepo.arg("createrepo");
			// sqllite db
			createrepo.arg("-d");
			// debuginfo
			if (!repoToRecreate.getName().equals(debuginfoDirName))
				createrepo.arg("-x").arg(debuginfoDirName + "/*");
			// quiet
			createrepo.arg("-q");
			createrepo.arg(repoToRecreate.getAbsolutePath());

			createrepo.setExecutor(executor);
			createrepo.run();
			log.info("Updated repo " + repoToRecreate);
		}

		// index staging workspace
		factory.indexWorkspace(factory.getStagingWorkspace());
	}

	protected void copyToDirs(File file, File[] dirs) {
		for (File dir : dirs) {
			try {
				FileUtils.copyFileToDirectory(file, dir);
				if (log.isDebugEnabled())
					log.debug(file + " => " + dir);
			} catch (IOException e) {
				throw new SlcException("Cannot copy " + file + " to " + dir, e);
			}
		}
	}

	public void setArch(String arch) {
		this.arch = arch;
	}

	public void setRpmPackage(String rpmPackage) {
		this.rpmPackage = rpmPackage;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public void setFactory(RpmFactory env) {
		this.factory = env;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}
}