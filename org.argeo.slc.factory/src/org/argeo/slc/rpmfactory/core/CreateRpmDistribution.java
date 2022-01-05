package org.argeo.slc.rpmfactory.core;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.StringTokenizer;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.argeo.api.cms.CmsLog;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.rpmfactory.RpmFactory;
import org.argeo.slc.runtime.tasks.SystemCall;

/**
 * Gather RPMs from various sources (local builds or third party) into a
 * consistent distributable set (typically to be used to generate an ISO).
 */
public class CreateRpmDistribution implements Runnable {
	private final static CmsLog log = CmsLog
			.getLog(CreateRpmDistribution.class);

	private RpmFactory rpmFactory;
	private RpmDistribution rpmDistribution;

	private String arch = "x86_64";

	private String repoqueryExecutable = "/usr/bin/repoquery";

	@Override
	public void run() {
		Session session = null;
		// Reader reader = null;
		try {
			Node baseFolder = rpmFactory.newDistribution(rpmDistribution
					.getId());
			session = baseFolder.getSession();
			Node targetFolder = baseFolder.addNode(arch, NodeType.NT_FOLDER);

			SystemCall repoquery = new SystemCall();
			repoquery.arg(repoqueryExecutable);

			File yumConfigFile = rpmFactory.getYumRepoFile(arch);
			repoquery.arg("-c", yumConfigFile.getAbsolutePath());
			repoquery.arg("--requires");
			repoquery.arg("--resolve");
			repoquery.arg("--location");
			repoquery.arg("--archlist=" + arch);

			for (String rpmPackage : rpmDistribution.getPackages())
				repoquery.arg(rpmPackage);

			if (log.isDebugEnabled())
				log.debug("Command:\n" + repoquery.asCommand());

			String output = repoquery.function();

			if (log.isDebugEnabled())
				log.debug(output + "\n");
			// reader = new StringReader(output);
			StringTokenizer lines = new StringTokenizer(output, "\n");
			// List<String> dependencies = IOUtils.readLines(reader);
			dependencies: while (lines.hasMoreTokens()) {
				String urlStr = lines.nextToken();
				InputStream in = null;
				try {
					URL url = new URL(urlStr);
					String fileName = FilenameUtils.getName(url.getFile());
					String[] tokens = fileName.split("-");
					if (tokens.length < 3)
						continue dependencies;
					StringBuilder buf = new StringBuilder();
					for (int i = 0; i < tokens.length - 2; i++) {
						if (i != 0)
							buf.append('-');
						buf.append(tokens[i]);

					}
					String packageName = buf.toString();
					for (RpmPackageSet excluded : rpmDistribution
							.getExcludedPackages()) {
						if (excluded.contains(packageName)) {
							if (log.isDebugEnabled())
								log.debug("Skipped " + packageName);
							continue dependencies;// skip
						}
					}
					in = url.openStream();
					JcrUtils.copyStreamAsFile(targetFolder, fileName, in);
					targetFolder.getSession().save();
					if (log.isDebugEnabled())
						log.debug("Copied  " + packageName);
				} catch (Exception e) {
					log.error("Cannot copy " + urlStr, e);
				} finally {
					IOUtils.closeQuietly(in);
				}
			}

			// createrepo
			File workspaceDir = rpmFactory.getWorkspaceDir(rpmDistribution
					.getId());
			SystemCall createrepo = new SystemCall();
			createrepo.arg("createrepo");
			createrepo.arg("-q");
			createrepo.arg("-d");
			File archDir = new File(workspaceDir.getPath()
					+ targetFolder.getPath());
			createrepo.arg(archDir.getAbsolutePath());
			createrepo.run();
		} catch (Exception e) {
			throw new SlcException("Cannot generate distribution "
					+ rpmDistribution.getId(), e);
		} finally {
			JcrUtils.logoutQuietly(session);
			// IOUtils.closeQuietly(reader);
		}
	}

	public void setRpmDistribution(RpmDistribution rpmDistribution) {
		this.rpmDistribution = rpmDistribution;
	}

	public void setRpmFactory(RpmFactory rpmFactory) {
		this.rpmFactory = rpmFactory;
	}

	public void setArch(String arch) {
		this.arch = arch;
	}

	public void setRepoqueryExecutable(String yumdownloaderExecutable) {
		this.repoqueryExecutable = yumdownloaderExecutable;
	}

}
