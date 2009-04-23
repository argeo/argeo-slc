package org.argeo.slc.maven.plugins.qooxdoo;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;

/**
 * Prepares Qooxdoo environment
 * 
 * @goal env
 */
public class EnvironmentMojo extends AbstractMojo {
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
	 * Location of the local repository.
	 * 
	 * @parameter expression="${localRepository}"
	 * @readonly
	 * @required
	 */
	protected org.apache.maven.artifact.repository.ArtifactRepository local;

	/**
	 * List of Remote Repositories used by the resolver
	 * 
	 * @parameter expression="${project.remoteArtifactRepositories}"
	 * @readonly
	 * @required
	 */
	protected java.util.List remoteRepos;
	/**
	 * To look up Archiver/UnArchiver implementations
	 * 
	 * @parameter expression=
	 *            "${component.org.codehaus.plexus.archiver.manager.ArchiverManager}"
	 * @required
	 * @readonly
	 */
	protected ArchiverManager archiverManager;

	/**
	 * Source base where Qooxdoo SDK will be unpacked
	 * 
	 * @parameter expression="${srcBase}" default-value="src"
	 * @required
	 */
	private File srcBase;

	private String qxSdkDirName = "qooxdoo-sdk";

	private String qxSdkGroupId = "org.argeo.dep.dist";
	private String qxSdkArtifactId = "qooxdoo-sdk";
	private String qxSdkClassifier = "dist";
	private String qxSdkVersion = "0.8.1.argeo.1";
	private String qxSdkType = "zip";

	public void execute() throws MojoExecutionException, MojoFailureException {
		Artifact qxSdkArtifact = getQxSdkArtifact();
		File qxSdkDir = new File(srcBase.getPath() + File.separator
				+ qxSdkDirName);
		if (!qxSdkDir.exists())
			unpackArtifact(qxSdkArtifact, srcBase);
		else
			getLog()
					.warn("Qooxdoo SDK already unpacked, skipping unpacking...");
		getLog().info("Qooxdoo environment prepared");
	}

	protected Artifact getQxSdkArtifact() throws MojoExecutionException {
		VersionRange vr;
		try {
			vr = VersionRange.createFromVersionSpec(qxSdkVersion);
		} catch (InvalidVersionSpecificationException e1) {
			e1.printStackTrace();
			vr = VersionRange.createFromVersion(qxSdkVersion);
		}

		Artifact qxSdkArtifact = factory.createDependencyArtifact(qxSdkGroupId,
				qxSdkArtifactId, vr, qxSdkType, qxSdkClassifier,
				Artifact.SCOPE_COMPILE);
		try {
			resolver.resolve(qxSdkArtifact, remoteRepos, local);
		} catch (ArtifactResolutionException e) {
			throw new MojoExecutionException("Unable to resolve artifact.", e);
		} catch (ArtifactNotFoundException e) {
			throw new MojoExecutionException("Unable to find artifact.", e);
		}

		return qxSdkArtifact;
	}

	protected void unpackArtifact(Artifact artifact, File location)
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
