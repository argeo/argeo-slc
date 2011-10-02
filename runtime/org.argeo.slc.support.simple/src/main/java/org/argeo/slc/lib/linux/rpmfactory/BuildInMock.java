package org.argeo.slc.lib.linux.rpmfactory;

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

/** Rebuild an SRPM in mock. (Historical) Replaces the build-mock.sh script. */
public class BuildInMock implements Runnable {
	private final static Log log = LogFactory.getLog(BuildInMock.class);

	/** Mock flavour provided by the EPEL repository */
	public final static String EPEL = "EPEL";
	/** Mock flavour provided by CentOS until v5 */
	public final static String CENTOS = "CENTOS";

	public final static String NOARCH = "noarch";

	private String mockVar = "/var/lib/mock";

	private String mockFlavour = EPEL;
	private String mockConfig = null;

	private String repository;
	private String release = null;
	private String level = null;
	private String arch = NOARCH;

	private String srpm;

	private Boolean mkdirs = true;

	private RpmBuildEnvironment buildEnvironment;
	private Executor executor;

	private String debuginfoDirName = "debuginfo";

	public void run() {
		// TODO check if caller is in mock group

		String cfg = mockConfig != null ? mockConfig : repository + "-"
				+ release + "-" + level + "-" + arch;

		// prepare mock call
		SystemCall mock = new SystemCall();
		if (arch != null)
			mock.arg("setarch").arg(arch);
		mock.arg("mock");
		if (mockFlavour.equals(EPEL))
			mock.arg("-v");
		else if (mockFlavour.equals(CENTOS))
			mock.arg("--debug");
		if (arch != null)
			mock.arg("--arch=" + arch);
		mock.arg("-r").arg(cfg);
		mock.arg(srpm);

		mock.setLogCommand(true);

		// mock command execution
		mock.setExecutor(executor);
		mock.run();

		File repoDir = new File(buildEnvironment.getStagingBase() + "/"
				+ repository + "/" + level + "/" + release);
		File srpmDir = new File(repoDir, "SRPMS");
		if (mkdirs)
			srpmDir.mkdirs();
		File archDir = null;
		File debuginfoDir = null;
		if (!arch.equals(NOARCH)) {
			archDir = new File(repoDir, arch);
			debuginfoDir = new File(archDir, debuginfoDirName);
			debuginfoDir.mkdirs();
		}

		// copy RPMs
		Set<File> reposToRecreate = new HashSet<File>();
		File resultDir = new File(mockVar + "/" + cfg + "/result");
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
				for (String arch : buildEnvironment.getArchs())
					dirs.add(new File(repoDir, arch));
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

	public void setMockFlavour(String mockFlavour) {
		this.mockFlavour = mockFlavour;
	}

	public void setMockConfig(String mockConfig) {
		this.mockConfig = mockConfig;
	}

	public void setRepository(String repo) {
		this.repository = repo;
	}

	public void setRelease(String release) {
		this.release = release;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public void setArch(String arch) {
		this.arch = arch;
	}

	public void setSrpm(String srpm) {
		this.srpm = srpm;
	}

	public void setMockVar(String mockVar) {
		this.mockVar = mockVar;
	}

	public void setMkdirs(Boolean mkdirs) {
		this.mkdirs = mkdirs;
	}

	public void setBuildEnvironment(RpmBuildEnvironment buildEnvironment) {
		this.buildEnvironment = buildEnvironment;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

}
