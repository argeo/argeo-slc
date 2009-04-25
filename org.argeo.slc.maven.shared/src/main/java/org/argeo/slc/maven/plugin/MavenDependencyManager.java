package org.argeo.slc.maven.plugin;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;

public interface MavenDependencyManager {
	String ROLE = MavenDependencyManager.class.getName();

	public Artifact getResolvedArtifact(List remoteRepos,
			ArtifactRepository local, String groupId, String artifactId,
			String version, String type, String classifier, String scope)
			throws MojoExecutionException;

	public void unpackArtifact(Artifact artifact, File location)
			throws MojoExecutionException;

	public Set getTransitiveProjectDependencies(MavenProject project,
			List remoteRepos, ArtifactRepository local)
			throws InvalidDependencyVersionException,
			ArtifactNotFoundException, ArtifactResolutionException;
}
