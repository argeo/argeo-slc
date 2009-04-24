package org.argeo.slc.maven.plugins.qooxdoo;

import java.io.File;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.argeo.slc.maven.plugin.MavenDependencyManager;

/**
 * Factorize common configuration
 */
public abstract class AbstractQooxdooMojo extends AbstractMojo {
	/**
	 * List of Remote Repositories used by the resolver
	 * 
	 * @parameter expression="${project.remoteArtifactRepositories}"
	 * @readonly
	 * @required
	 */
	protected java.util.List<ArtifactRepository> remoteRepos;
	/**
	 * Location of the local repository.
	 * 
	 * @parameter expression="${localRepository}"
	 * @readonly
	 * @required
	 */
	protected org.apache.maven.artifact.repository.ArtifactRepository local;

	/**
	 * The directory for the pom
	 * 
	 * @parameter expression="${basedir}"
	 * @required
	 */
	protected File baseDir;

	/**
	 * Dependency manager
	 * 
	 * @component
	 */
	protected MavenDependencyManager depManager;

	/**
	 * Source base where Qooxdoo SDK will be unpacked
	 * 
	 * @parameter expression="${srcBase}" default-value="src"
	 * @required
	 */
	protected File srcBase;

	/**
	 * Qooxdoo cache location
	 * 
	 * @parameter expression="${cache}" default-value="cache"
	 * @required
	 */
	protected File cache;

	/**
	 * Name of the SDK directory (the base dire in the unpacked distribution)
	 * 
	 * @parameter expression="${sdkDirName}" default-value="qooxdoo-sdk"
	 * @required
	 */
	protected String sdkDirName;

	/**
	 * SDK maven groupId
	 * 
	 * @parameter expression="${sdkGroupId}" default-value="org.argeo.dep.dist"
	 * @required
	 */
	protected String sdkGroupId;

	/**
	 * SDK maven artifactId
	 * 
	 * @parameter expression="${sdkArtifactId}" default-value="qooxdoo-sdk"
	 * @required
	 */
	protected String sdkArtifactId;

	/**
	 * SDK maven classifier
	 * 
	 * @parameter expression="${sdkClassifier}" default-value="dist"
	 * @required
	 */
	protected String sdkClassifier;

	/**
	 * SDK maven version
	 * 
	 * @parameter expression="${sdkVersion}" default-value="0.8.1.argeo.1"
	 * @required
	 */
	protected String sdkVersion;

	/**
	 * SDK maven type
	 * 
	 * @parameter expression="${sdkType}" default-value="zip"
	 * @required
	 */
	protected String sdkType;

	protected File getSdkDir() {
		return new File(srcBase.getPath() + File.separator + sdkDirName);
	}
}
