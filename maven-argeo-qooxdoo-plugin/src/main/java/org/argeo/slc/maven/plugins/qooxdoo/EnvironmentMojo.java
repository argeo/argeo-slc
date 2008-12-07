package org.argeo.slc.maven.plugins.qooxdoo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Prepares Jython / Qooxdoo environment
 * 
 * @goal env
 * @execute lifecycle="env" phase="initialize"
 */
public class EnvironmentMojo extends AbstractMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Qooxdoo environment prepared");
	}

}
