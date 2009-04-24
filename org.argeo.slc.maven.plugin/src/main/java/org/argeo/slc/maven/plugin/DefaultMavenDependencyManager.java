package org.argeo.slc.maven.plugin;

import java.io.File;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.MojoExecutionException;
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
}
