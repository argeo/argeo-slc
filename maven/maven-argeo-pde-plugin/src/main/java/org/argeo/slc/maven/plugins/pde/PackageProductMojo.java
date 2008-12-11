package org.argeo.slc.maven.plugins.pde;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Builds a product and adds it as main artifact.
 * 
 * @goal packageProduct
 * @phase package
 * @execute goal="buildProduct"
 */
public class PackageProductMojo extends AbstractMojo {
	/**
	 * The Maven project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * The build directory.
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
