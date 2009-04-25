package org.argeo.slc.maven.plugin;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;

/**
 * Abstract Maven plugin for interacting with dependencies
 */
public class DefaultMavenDependencyManager implements MavenDependencyManager {
	/**
	 * Used to look up Artifacts in the remote repository.
	 * 
	 * @parameter expression=
	 *            "${component.org.apache.maven.artifact.factory.ArtifactFactory}"
	 * @required
	 * @readonly
	 */
	protected org.apache.maven.artifact.factory.ArtifactFactory factory;

	/**
	 * Used to look up Artifacts in the remote repository.
	 * 
	 * @parameter expression=
	 *            "${component.org.apache.maven.artifact.resolver.ArtifactResolver}"
	 * @required
	 * @readonly
	 */
	protected org.apache.maven.artifact.resolver.ArtifactResolver resolver;

	/**
	 * To look up Archiver/UnArchiver implementations
	 * 
	 * @parameter expression=
	 *            "${component.org.codehaus.plexus.archiver.manager.ArchiverManager}"
	 * @required
	 * @readonly
	 */
	protected ArchiverManager archiverManager;

	/** @component */
	protected ArtifactMetadataSource artifactMetadataSource;

	public DefaultMavenDependencyManager() {
	}

	public Artifact getResolvedArtifact(List remoteRepos,
			ArtifactRepository local, String groupId, String artifactId,
			String version, String type, String classifier, String scope)
			throws MojoExecutionException {
		VersionRange vr;
		try {
			vr = VersionRange.createFromVersionSpec(version);
		} catch (InvalidVersionSpecificationException e1) {
			e1.printStackTrace();
			vr = VersionRange.createFromVersion(version);
		}

		Artifact qxSdkArtifact = factory.createDependencyArtifact(groupId,
				artifactId, vr, type, classifier, scope);
		try {
			resolver.resolve(qxSdkArtifact, remoteRepos, local);
		} catch (ArtifactResolutionException e) {
			throw new MojoExecutionException("Unable to resolve artifact.", e);
		} catch (ArtifactNotFoundException e) {
			throw new MojoExecutionException("Unable to find artifact.", e);
		}

		return qxSdkArtifact;
	}

	public void unpackArtifact(Artifact artifact, File location)
			throws MojoExecutionException {
		File file = artifact.getFile();
		try {
			UnArchiver unArchiver;
			unArchiver = archiverManager.getUnArchiver(file);
			unArchiver.setSourceFile(file);
			unArchiver.setDestDirectory(location);
			unArchiver.extract();
		} catch (NoSuchArchiverException e) {
			throw new MojoExecutionException("Unknown archiver type", e);
		} catch (ArchiverException e) {
			e.printStackTrace();
			throw new MojoExecutionException("Error unpacking file: " + file
					+ " to: " + location + "\r\n" + e.toString(), e);
		}
	}

	public Set getTransitiveProjectDependencies(MavenProject project,
			List remoteRepos, ArtifactRepository local)
			throws InvalidDependencyVersionException,
			ArtifactNotFoundException, ArtifactResolutionException {
		Set artifacts = project.createArtifacts(this.factory, null, null);

		ArtifactResolutionResult arr = resolver.resolveTransitively(artifacts,
				project.getArtifact(), local, remoteRepos,
				this.artifactMetadataSource, null);

		// Order, just for display
		Set dependencies = new TreeSet(new ArtifactComparator());
		dependencies.addAll(arr.getArtifacts());
		return dependencies;
	}

	protected static class ArtifactComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			Artifact a1 = (Artifact) o1;
			Artifact a2 = (Artifact) o2;

			if (!a1.getGroupId().equals(a2.getGroupId()))
				return a1.getGroupId().compareTo(a2.getGroupId());
			else
				return a1.getArtifactId().compareTo(a2.getArtifactId());
		}
	}

}
