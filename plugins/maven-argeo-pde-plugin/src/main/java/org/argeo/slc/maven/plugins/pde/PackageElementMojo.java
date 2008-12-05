package org.argeo.slc.maven.plugins.pde;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Builds an element and adds it as main artifact.
 * 
 * @goal packageElement
 * @phase package
 * @execute goal="buildElement"
 */
public class PackageElementMojo extends AbstractMojo {
	/**
	 * The Maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * The directory for the generated JAR.
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private String buildDirectory;

	public void execute() throws MojoExecutionException {
		File file = new File(buildDirectory, project.getArtifactId() + ".zip");
		project.getArtifact().setFile(file);
	}
}
