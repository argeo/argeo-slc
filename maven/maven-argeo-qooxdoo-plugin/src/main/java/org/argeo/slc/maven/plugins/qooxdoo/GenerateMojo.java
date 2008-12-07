package org.argeo.slc.maven.plugins.qooxdoo;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.python.util.jython;

/**
 * Calls Qooxdoo python tool chain
 * 
 * @goal generate
 * @execute goal="env"
 */
public class GenerateMojo extends AbstractMojo {
	/**
	 * The Qooxdoo build target.
	 * 
	 * @parameter expression="${job}"
	 * @required
	 */
	private String job;

	/**
	 * The build directory.
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private File buildDirectory;

	/**
	 * The directory for the pom
	 * 
	 * @parameter expression="${basedir}"
	 * @required
	 */
	private File baseDir;

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			File jythonBase = new File(buildDirectory, "jython");
			jythonBase.mkdirs();
			System.setProperty("python.home", jythonBase.getCanonicalPath());

			//File generateScript = new File(baseDir, "generate.py");
			// String[] jobArray = jobs.split(" ");
			// String[] args = new String[jobArray.length + 1];
			// args[0] = generateScript.getCanonicalPath();
			// System.arraycopy(jobArray, 0, args, 1, jobArray.length);
			String[] args = { "generate.py", job };
			getLog().info("Running Qooxdoo job: " + job + " ...");
			jython.main(args);
			getLog().info("Finished Qooxdoo job: " + job);
		} catch (Exception e) {
			throw new MojoExecutionException(
					"Unexpected exception when running Jython", e);
		}

	}

}
