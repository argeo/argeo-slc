package org.argeo.slc.maven.plugins.osgi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Update the manifests based on the POM
 * 
 * @goal update-manifests
 * @phase package
 * @author mbaudier
 * 
 */
public class UpdateManifestsMojo extends AbstractBundlesPackagerMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (!PACKAGING_BUNDLE.equals(project.getPackaging())) {
			getLog().info(
					"Project is not of packaging type " + PACKAGING_BUNDLE
							+ ", skipping...");
		}

		int sIndex = snapshotIndex();
		String versionMf = null;
		if (sIndex >= 0) {// SNAPSHOT
			versionMf = versionMain(sIndex) + ".SNAPSHOT";
		} else {
			throw new MojoExecutionException("Can only modify on SNAPSHOT");
		}

		File[] bundleDirs = getBundleDirectory().listFiles(bundleFileFilter());
		for (int i = 0; i < bundleDirs.length; i++) {
			OutputStream out = null;
			try {
				File dir = bundleDirs[i];
				File originalMf = manifestFileFromDir(dir);
				Manifest manifest = readManifest(originalMf);
				manifest.getMainAttributes().putValue("Bundle-Version",
						versionMf);
				manifest.getMainAttributes().put(
						Attributes.Name.MANIFEST_VERSION, "1.0");

				out = new FileOutputStream(originalMf);
				manifest.write(out);
				getLog().info(
						"Update MANIFEST of bundle " + dir + " with version "
								+ versionMf);
			} catch (IOException e) {
				throw new MojoExecutionException(
						"Could not modify manifets. WARNING: some manifests may already have been modified! Check your sources.",
						e);
			} finally {
				IOUtils.closeQuietly(out);
			}
		}

	}
}
