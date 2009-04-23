package org.argeo.slc.maven.plugins.qooxdoo;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;
import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 * Calls Qooxdoo python tool chain
 * 
 * @goal generate
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
	 * Location of the qooxdoo sdk.
	 * 
	 * @parameter expression="${qooxdooSdk}" default-value="src/qooxdoo-sdk"
	 */
	private String qooxdooSdk;

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
			File generateScript = new File(baseDir.getPath() + File.separator
					+ qooxdooSdk + File.separator + "tool" + File.separator
					+ "bin", "generator.py");
			getLog().info("Running Qooxdoo job: " + job + " ...");

			Commandline cl = new Commandline();

			cl.setExecutable("python");// python needs to be installed
			cl.setWorkingDirectory(baseDir.getCanonicalPath());
			cl.createArgument().setValue(generateScript.getCanonicalPath());
			cl.createArgument().setValue(job);

			StreamConsumer stdout = new StdoutConsumer(getLog());
			StreamConsumer stderr = new StderrConsumer(getLog());
			try {
				int result = CommandLineUtils.executeCommandLine(cl, stdout,
						stderr);
				if (result != 0) {
					throw new MojoExecutionException("Qooxdoo job returned: \'"
							+ result + "\'.");
				}
			} catch (CommandLineException e) {
				throw new MojoExecutionException("Unable to run Qooxdoo job", e);
			}
			getLog().info("Finished Qooxdoo job: " + job);

		} catch (Exception e) {
			throw new MojoExecutionException(
					"Unexpected exception when running Python", e);
		}

	}

	/**
	 * Consumer to receive lines sent to stdout. The lines are logged as info.
	 */
	private class StdoutConsumer implements StreamConsumer {
		/** Logger to receive the lines. */
		private Log logger;

		/**
		 * Constructor.
		 * 
		 * @param log
		 *            The logger to receive the lines
		 */
		public StdoutConsumer(Log log) {
			logger = log;
		}

		/**
		 * Consume a line.
		 * 
		 * @param string
		 *            The line to consume
		 */
		public void consumeLine(String string) {
			logger.info(string);
		}
	}

	/**
	 * Consumer to receive lines sent to stderr. The lines are logged as
	 * warnings.
	 */
	private class StderrConsumer implements StreamConsumer {
		/** Logger to receive the lines. */
		private Log logger;

		/**
		 * Constructor.
		 * 
		 * @param log
		 *            The logger to receive the lines
		 */
		public StderrConsumer(Log log) {
			logger = log;
		}

		/**
		 * Consume a line.
		 * 
		 * @param string
		 *            The line to consume
		 */
		public void consumeLine(String string) {
			logger.warn(string);
		}
	}

}
