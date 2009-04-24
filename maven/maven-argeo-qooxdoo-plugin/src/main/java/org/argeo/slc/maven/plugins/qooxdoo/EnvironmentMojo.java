package org.argeo.slc.maven.plugins.qooxdoo;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Prepares Qooxdoo environment
 * 
 * @goal env
 */
public class EnvironmentMojo extends AbstractQooxdooMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		Artifact qxSdkArtifact = depManager.getResolvedArtifact(remoteRepos,
				local, sdkGroupId, sdkArtifactId, sdkVersion, sdkType,
				sdkClassifier, Artifact.SCOPE_COMPILE);
		if (!getSdkDir().exists())
			depManager.unpackArtifact(qxSdkArtifact, srcBase);
		else
			getLog().warn("Qooxdoo SDK already unpacked, skip unpacking...");
		getLog().info("Qooxdoo environment prepared");
	}
}
