package org.argeo.slc.maven.plugins.qooxdoo;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;

/**
 * Reset Qooxdoo context: removes SDK, clean cache, etc.
 * 
 * @goal reset
 */
public class ResetMojo extends AbstractQooxdooMojo {
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (getSdkDir().exists()) {
			delete(getSdkDir());
		}

		if (cache.exists()) {
			delete(cache);
		}

	}

	protected void delete(File dir) throws MojoExecutionException {
		try {
			FileUtils.deleteDirectory(dir);
			getLog().info("Deleted directory " + dir);
		} catch (IOException e) {
			throw new MojoExecutionException("Cannot delete " + dir, e);
		}
	}
}
