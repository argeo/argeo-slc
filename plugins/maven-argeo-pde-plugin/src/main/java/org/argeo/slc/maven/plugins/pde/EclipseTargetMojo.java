package org.argeo.slc.maven.plugins.pde;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Forks a eclipseTarget lifecycle.
 * 
 * @goal eclipseTarget
 * @execute lifecycle="eclipseTarget" phase="initialize"
 */
public class EclipseTargetMojo extends AbstractMojo {
	public void execute() throws MojoExecutionException {
		getLog().info("EclipseTargetMojo done");
	}
}
