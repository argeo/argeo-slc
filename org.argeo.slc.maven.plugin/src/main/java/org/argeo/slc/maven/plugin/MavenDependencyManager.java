package org.argeo.slc.maven.plugin;

import java.io.File;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.MojoExecutionException;

public interface MavenDependencyManager {
	String ROLE = MavenDependencyManager.class.getName();

	public Artifact getResolvedArtifact(List remoteRepos,
			ArtifactRepository local, String groupId, String artifactId,
			String version, String type, String classifier, String scope)
			throws MojoExecutionException;

	public void unpackArtifact(Artifact artifact, File location)
			throws MojoExecutionException;

}
