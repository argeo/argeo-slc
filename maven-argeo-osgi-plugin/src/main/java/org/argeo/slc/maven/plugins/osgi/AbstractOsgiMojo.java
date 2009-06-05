package org.argeo.slc.maven.plugins.osgi;

import java.io.File;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

/**
 * Factorize common configuration
 */
public abstract class AbstractOsgiMojo extends AbstractMojo {
	public final static String PACKAGING_BUNDLE = "bundle";

	/**
	 * The maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * List of Remote Repositories used by the resolver
	 * 
	 * @parameter expression="${project.remoteArtifactRepositories}"
	 * @readonly
	 * @required
	 */
	protected java.util.List remoteRepos;
	/**
	 * Location of the local repository.
	 * 
	 * @parameter expression="${localRepository}"
	 * @readonly
	 * @required
	 */
	protected ArtifactRepository local;

	/**
	 * @parameter 
	 *            expression="${project.distributionManagementArtifactRepository}"
	 */
	protected ArtifactRepository deploymentRepository;

	/**
	 * The directory for the pom
	 * 
	 * @parameter expression="${basedir}"
	 * @required
	 */
	protected File baseDir;

	/**
	 * Directory containing the build files.
	 * 
	 * @parameter expression="${project.build.directory}"
	 */
	protected File buildDirectory;

	/** @component */
	protected org.apache.maven.artifact.factory.ArtifactFactory artifactFactory;

}
