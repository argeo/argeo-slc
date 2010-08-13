package org.argeo.slc.lib.linux.rpmfactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.tasks.SystemCall;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

/** Generates an SRPM from a spec file */
public class CreateSrpm implements Runnable {
	private final static Log log = LogFactory.getLog(CreateSrpm.class);

	private File topdir;

	/** Directory where to cache downloaded dsitributions. */
	private File distributionCache;

	private Resource specFile;

	private RpmBuildEnvironment rpmBuildEnvironment;

	private Boolean overwriteSources = false;

	private File srpmFile;

	public void run() {
		File sourcesDir = new File(topdir, "SOURCES");
		sourcesDir.mkdirs();
		File specsDir = new File(topdir, "SPECS");
		File srpmsDir = new File(topdir, "SRPMS");

		try {
			// Parse spec file and copy required resources
			RpmSpecFile spec = new RpmSpecFile(specFile);
			copyToSources(spec, sourcesDir);

			// Copy spec file
			File targetFile = new File(specsDir, specFile.getFilename())
					.getCanonicalFile();
			copyResourceToFile(specFile, targetFile);

			// Generate rpmbuild config files
			File rpmmacroFile = new File(topdir, "rpmmacros");
			File rpmrcFile = new File(topdir, "rpmrc");
			rpmBuildEnvironment.writeRpmbuildConfigFiles(topdir, rpmmacroFile,
					rpmrcFile);

			// Build SRPM
			srpmsDir.mkdirs();
			SystemCall packageSrpm = new SystemCall();
			packageSrpm.arg("rpmbuild");
			packageSrpm.arg("-bs").arg("--nodeps");
			packageSrpm.arg("--rcfile=" + rpmrcFile.getName());
			// buildSrpm.arg("-D", "_topdir " + topdir.getCanonicalPath() + "");
			packageSrpm.arg("SPECS/" + specFile.getFilename());
			packageSrpm.setExecDir(topdir.getCanonicalPath());
			packageSrpm.setLogCommand(true);

			// Execute
			String answer = packageSrpm.function();

			// Extract generated SRPM path
			// TODO: make it safer
			String srpmPath = answer.split(":")[1].trim();
			srpmFile = new File(srpmPath);
		} catch (IOException e) {
			throw new SlcException("Cannot generate SRPM from " + specFile, e);
		}

	}

	protected void copyToSources(RpmSpecFile spec, File sourcesDir) {
		try {
			List<Resource> toCopyToSources = new ArrayList<Resource>();
			for (String file : spec.getSources().values()) {
				try {
					Resource res;
					try {
						res = specFile.createRelative("../SOURCES/" + file);
						if (!res.exists())
							res = new UrlResource(file);

					} catch (Exception e) {
						res = new UrlResource(file);
					}
					toCopyToSources.add(res);
				} catch (Exception e) {
					log.error("Cannot interpret " + file, e);
				}
			}
			for (String file : spec.getPatches().values()) {
				try {
					Resource res;
					try {
						res = specFile.createRelative("../SOURCES/" + file);
						if (!res.exists()) {
							res = new UrlResource(file);
						}
					} catch (Exception e) {
						res = new UrlResource(file);
					}
					toCopyToSources.add(res);
				} catch (Exception e) {
					log.error("Cannot interpret " + file, e);
				}
			}

			// FIXME: we may have missed some files here
			copySources: for (Resource res : toCopyToSources) {
				File targetDir;
				if (distributionCache != null) {
					if (distributionCache.exists())
						distributionCache.mkdirs();
					targetDir = distributionCache;
				} else
					targetDir = sourcesDir;
				File targetFile = new File(targetDir, res.getFilename())
						.getCanonicalFile();
				if (!targetFile.exists() || overwriteSources)
					copyResourceToFile(res, targetFile);
				if (!targetDir.equals(sourcesDir)) {
					File fileInSourcesDir = new File(sourcesDir, targetFile
							.getName());
					if (!fileInSourcesDir.exists()
							|| !(fileInSourcesDir.length() == targetFile
									.length()))
						FileUtils.copyFile(targetFile, fileInSourcesDir);
				}
			}
		} catch (Exception e) {
			throw new SlcException("Cannot copy to " + sourcesDir, e);
		}
	}

	private static void copyResourceToFile(Resource res, File targetFile) {
		try {
			if (targetFile.equals(res.getFile())) {
				if (log.isDebugEnabled())
					log.debug("Target identical to source, skipping... "
							+ targetFile + " <=> " + res);
				return;
			}
		} catch (IOException e1) {
			// silent
		}

		OutputStream out = null;
		InputStream in = null;
		try {
			out = FileUtils.openOutputStream(targetFile);
			in = res.getInputStream();
			IOUtils.copy(in, out);
			if (log.isDebugEnabled())
				log.debug("Copied " + targetFile + " from " + res);
		} catch (Exception e) {
			throw new SlcException("Cannot copy " + res + " to " + targetFile,
					e);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}

	}

	public void setSpecFile(Resource specFile) {
		this.specFile = specFile;
	}

	public void setTopdir(File topdir) {
		this.topdir = topdir;
	}

	public void setOverwriteSources(Boolean overwriteSources) {
		this.overwriteSources = overwriteSources;
	}

	public File getSrpmFile() {
		return srpmFile;
	}

	public void setRpmBuildEnvironment(RpmBuildEnvironment rpmBuildEnvironment) {
		this.rpmBuildEnvironment = rpmBuildEnvironment;
	}

	public void setDistributionCache(File distributionCache) {
		this.distributionCache = distributionCache;
	}

}
