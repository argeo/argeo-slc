package org.argeo.slc.rpmfactory.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.exec.Executor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.tasks.SystemCall;

/** Releases the content of staging to a public repository. */
public class ReleaseStaging implements Runnable {
	private final static Log log = LogFactory.getLog(ReleaseStaging.class);

	private RpmFactory rpmFactory;
	private Executor executor;

	private String debuginfoDirName = "debuginfo";

	@Override
	public void run() {
		File stagingDir = rpmFactory.getWorkspaceDir(rpmFactory
				.getStagingWorkspace());
		// TODO deal with testing
		File targetRepoDir = rpmFactory.getWorkspaceDir(rpmFactory
				.getStableWorkspace());
		List<File> reposToRecreate = new ArrayList<File>();

		stagingChildren: for (File dir : stagingDir.listFiles()) {
			if (!dir.isDirectory())
				continue stagingChildren;
			if (dir.getName().equals("lost+found"))
				continue stagingChildren;

			File targetDir = new File(targetRepoDir, dir.getName());
			try {
				FileUtils.copyDirectory(dir, targetDir);
				if (log.isDebugEnabled())
					log.debug(dir + " => " + targetDir);
			} catch (IOException e) {
				throw new SlcException(stagingDir
						+ " could not be copied properly, check it manually."
						+ " Metadata have NOT been updated.", e);
			}

			reposToRecreate.add(dir);
			reposToRecreate.add(targetDir);
			File debugInfoDir = new File(dir, debuginfoDirName);
			if (debugInfoDir.exists())
				reposToRecreate.add(debugInfoDir);
			File targetDebugInfoDir = new File(targetDir, debuginfoDirName);
			if (targetDebugInfoDir.exists())
				reposToRecreate.add(targetDebugInfoDir);

		}

		// clear staging
		for (File dir : stagingDir.listFiles()) {
			try {
				if (dir.getName().equals("lost+found"))
					continue;
				if (dir.isDirectory())
					FileUtils.deleteDirectory(dir);
			} catch (IOException e) {
				log.error("Could not delete " + dir + ". " + e);
			}
		}

		// recreate changed repos
		for (File repoToRecreate : reposToRecreate) {
			repoToRecreate.mkdirs();
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

	public void setRpmFactory(RpmFactory rpmFactory) {
		this.rpmFactory = rpmFactory;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	public void setDebuginfoDirName(String debuginfoDirName) {
		this.debuginfoDirName = debuginfoDirName;
	}

}
