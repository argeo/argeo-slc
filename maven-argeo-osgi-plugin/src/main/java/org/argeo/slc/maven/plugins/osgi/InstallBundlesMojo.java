package org.argeo.slc.maven.plugins.osgi;

import java.io.File;
import java.util.List;

import org.apache.maven.artifact.installer.ArtifactInstallationException;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal install-bundles
 * @phase install
 * @author mbaudier
 * 
 */
public class InstallBundlesMojo extends AbstractBundlesPackagerMojo {
	/** @component */
	private ArtifactInstaller installer;

	public void execute() throws MojoExecutionException, MojoFailureException {
		List bundlePackages = analyze();
		for (int i = 0; i < bundlePackages.size(); i++) {
			AbstractBundlesPackagerMojo.BundlePackage bundlePackage = (BundlePackage) bundlePackages
					.get(i);
			try {
				installer.install(bundlePackage.getPackageFile(), bundlePackage
						.getArtifact(), local);
			} catch (ArtifactInstallationException e) {
				throw new MojoExecutionException("Could not install bundle "
						+ bundlePackage.getBundleDir(), e);
			}
		}

		// Bundles pom
		try {
			installer.install(bundlesPomFile(), bundlesPomArtifact(), local);
			installer.install(new File(baseDir.getPath() + File.separator
					+ "pom.xml"), project.getArtifact(), local);
		} catch (ArtifactInstallationException e) {
			throw new MojoExecutionException("Could not install bundles POM", e);
		}

	}
}
