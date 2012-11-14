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
package org.argeo.slc.lib.linux.rpmfactory;

import java.io.File;

import org.argeo.slc.core.execution.tasks.SystemCall;

/** Install an SRPM into a working copy */
public class ImportSrpm implements Runnable {
	private File baseDir;
	private File srpmFile;
	private RpmBuildEnvironment rpmBuildEnvironment;

	public void run() {
		SystemCall rpmQuery = new SystemCall(
				"rpm --queryformat '%{NAME}\n' -qp " + srpmFile);
		String packageName = rpmQuery.function();

		File topdir = new File(baseDir, packageName);

		// prepare SVN
		// TODO: do it with SVNKit
		topdir.mkdirs();
		new SystemCall("svn add " + topdir).run();
		new SystemCall("svn propset svn:ignore rpm*\nBUILD\nSRPMS\nRPMS " + topdir).run();
		File sourcesDir = new File(topdir, "SOURCES");
		sourcesDir.mkdirs();
		new SystemCall("svn add " + sourcesDir).run();
		new SystemCall("svn propset svn:ignore *gz\n*bz2\n*.zip\n*.jar " + sourcesDir).run();
		File specsDir = new File(topdir, "SPECS");
		specsDir.mkdirs();
		new SystemCall("svn add " + specsDir).run();

		// Write rpm config files
		File rpmmacroFile = new File(topdir, "rpmmacros");
		File rpmrcFile = new File(topdir, "rpmrc");
		rpmBuildEnvironment.writeRpmbuildConfigFiles(topdir, rpmmacroFile,
				rpmrcFile);

		// Install SRPM
		SystemCall installSrpm = new SystemCall();
		installSrpm.arg("rpm");
		installSrpm.arg("-Uvh");
		installSrpm.arg("--rcfile=" + rpmrcFile.getAbsolutePath());
		installSrpm.arg(srpmFile.getAbsolutePath());
		installSrpm.setExecDir(topdir.getAbsolutePath());
		installSrpm.setLogCommand(true);
		installSrpm.run();
	}

	public void setBaseDir(File basedir) {
		this.baseDir = basedir;
	}

	public void setSrpmFile(File srpmFile) {
		this.srpmFile = srpmFile;
	}

	public void setRpmBuildEnvironment(RpmBuildEnvironment rpmBuildEnvironment) {
		this.rpmBuildEnvironment = rpmBuildEnvironment;
	}

}
