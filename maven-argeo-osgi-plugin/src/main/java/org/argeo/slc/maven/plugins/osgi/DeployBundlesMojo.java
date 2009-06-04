package org.argeo.slc.maven.plugins.osgi;

import java.io.File;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.deployer.ArtifactDeployer;
import org.apache.maven.artifact.deployer.ArtifactDeploymentException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.artifact.ProjectArtifactMetadata;

/**
 * @goal deploy-bundles
 * @phase deploy
 * @author mbaudier
 * 
 */
public class DeployBundlesMojo extends AbstractBundlesPackagerMojo {
	/** @component */
	private ArtifactDeployer deployer;

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
				deployer.deploy(bundlePackage.getPackageFile(), artifact,
						deploymentRepository, local);
			} catch (ArtifactDeploymentException e) {
				throw new MojoExecutionException("Could not deploy bundle "
						+ bundlePackage.getBundleDir(), e);
			}
		}

		// bundles POM
		try {
			deployer.deploy(bundlesPomFile(), bundlesPomArtifact(),
					deploymentRepository, local);
			deployer.deploy(new File(baseDir.getPath() + File.separator
					+ "pom.xml"), project.getArtifact(), deploymentRepository,
					local);
		} catch (ArtifactDeploymentException e) {
			throw new MojoExecutionException("Could not deploy bundles POM", e);
		}

	}
}
