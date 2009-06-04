package org.argeo.slc.maven.plugins.osgi;

import java.io.File;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.installer.ArtifactInstallationException;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.artifact.ProjectArtifactMetadata;

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
		List bundlePackages = analyze(false);
		for (int i = 0; i < bundlePackages.size(); i++) {
			AbstractBundlesPackagerMojo.BundlePackage bundlePackage = (BundlePackage) bundlePackages
					.get(i);
			try {
				Artifact artifact = bundlePackage.getArtifact();
				ProjectArtifactMetadata metadata = new ProjectArtifactMetadata(
						artifact, bundlePackage.getPomFile());
				artifact.addMetadata(metadata);
				installer.install(bundlePackage.getPackageFile(), artifact,
						local);
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
