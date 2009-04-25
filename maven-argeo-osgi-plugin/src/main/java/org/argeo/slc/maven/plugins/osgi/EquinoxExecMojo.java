package org.argeo.slc.maven.plugins.osgi;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.argeo.slc.maven.plugin.MavenDependencyManager;
import org.argeo.slc.maven.plugin.SystemCall;

/**
 * @goal equinox
 * */
public class EquinoxExecMojo extends AbstractOsgiMojo {
	/** @component */
	private MavenDependencyManager mavenDependencyManager;

	/**
	 * Equinox artifact id
	 * 
	 * @parameter expression="${equinoxArtifactId}"
	 *            default-value="org.eclipse.osgi"
	 * @required
	 */
	protected String equinoxArtifactId;

	/**
	 * OSGIBoot artifact id
	 * 
	 * @parameter expression="${osgiBootArtifactId}"
	 *            default-value="org.argeo.slc.osgiboot"
	 * @required
	 */
	protected String osgiBootArtifactId;

	/**
	 * Java executable
	 * 
	 * @parameter expression="${jvm}" default-value="${java.home}/bin/java"
	 * @required
	 */
	protected String jvm;

	/**
	 * JVM arguments
	 * 
	 * @parameter alias="${jvmArgs}"
	 */
	protected String[] jvmArgs;

	protected String[] defaultJvmArgs = { "-Xmx128m" };

	/**
	 * Equinox args
	 * 
	 * @parameter alias="${args}"
	 */
	protected String[] args;

	protected String[] defaultArgs = { "-console", "-configuration", "conf",
			"-data", "data" };

	/**
	 * JVM system properties
	 * 
	 * @parameter alias="${systemProperties}"
	 */
	protected Map systemProperties;

	/**
	 * Execution directory
	 * 
	 * @parameter expression="${execDir}"
	 *            default-value="${project.build.directory}/exec"
	 * @required
	 */
	protected File execDir;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if ("bundles".equals(project.getArtifact().getType())) {
			System.out.println("Skip artifact of type bundles "
					+ artifactToString(project.getArtifact()));
			return;
		}

		try {
			Artifact equinoxArtifact = null;
			Artifact osgiBootArtifact = null;

			Set dependencies = mavenDependencyManager
					.getTransitiveProjectDependencies(project, remoteRepos,
							local);

			StringBuffer osgiLocations = new StringBuffer();
			List bundleArtifacts = new ArrayList();
			boolean first = true;
			for (Iterator it = dependencies.iterator(); it.hasNext();) {
				Artifact depArtifact = (Artifact) it.next();
				printArtifact(depArtifact);

				if (depArtifact.getArtifactId().equals(equinoxArtifactId)) {
					equinoxArtifact = depArtifact;
				} else if (depArtifact.getArtifactId().equals(
						osgiBootArtifactId)) {
					osgiBootArtifact = depArtifact;
				} else {
					bundleArtifacts.add(depArtifact);

					if ("jar".equals(depArtifact.getType())) {
						// Add to OSGi locations
						if (first)
							first = false;
						else
							osgiLocations.append(File.pathSeparatorChar);

						osgiLocations.append(depArtifact.getFile()
								.getCanonicalPath().replace(File.separatorChar,
										'/'));
					}
				}
			}

			// Set defaults
			if (jvmArgs == null)
				jvmArgs = defaultJvmArgs;
			if (args == null)
				args = defaultArgs;
			if (systemProperties == null)
				systemProperties = new HashMap();

			if (!execDir.exists())
				execDir.mkdirs();

			// Build command
			List cmdList = new ArrayList();
			cmdList.add(jvm);
			cmdList.addAll(Arrays.asList(jvmArgs));
			if (!systemProperties.containsKey("osgi.bundles"))
				cmdList.add("-Dosgi.bundles="
						+ osgiBootArtifact.getFile().getCanonicalPath()
						+ "@start");
			if (!systemProperties.containsKey("slc.osgi.locations"))
				cmdList.add("-Dslc.osgi.locations=" + osgiLocations);
			for (Iterator keys = systemProperties.keySet().iterator(); keys
					.hasNext();) {
				Object key = keys.next();
				Object value = systemProperties.get(key);
				String strValue = null;
				if (value != null) {
					strValue = value.toString().trim();
					strValue = strValue.replaceAll("\n", "");
					strValue = strValue.replaceAll("\t", "");
				}
				cmdList.add("-D" + key + "=" + strValue);
			}
			cmdList.add("-jar");
			cmdList.add(equinoxArtifact.getFile().getCanonicalPath());
			cmdList.addAll(Arrays.asList(args));

			String[] cmd = (String[]) cmdList.toArray(new String[0]);

			System.out.println("\nExecute command:\n");
			for (int i = 0; i < cmd.length; i++) {
				boolean containsSpace = (cmd[i].indexOf(' ') >= 0)
						|| (cmd[i].indexOf('\t') >= 0);
				if (containsSpace)
					System.out.print('\"');
				System.out.print(cmd[i]);
				if (containsSpace)
					System.out.print('\"');
				System.out.print(' ');
			}
			System.out.println("\n");

			SystemCall systemCall = new SystemCall(execDir.getCanonicalPath(),
					cmd, true);
			systemCall.run();

		} catch (Exception e) {
			throw new MojoExecutionException("Cannot execute Equinox", e);
		}

	}

	protected static void printArtifact(Artifact artifact) {
		System.out.println(artifactToString(artifact));
	}

	protected static String artifactToString(Artifact artifact) {
		return artifact.getGroupId() + ":" + artifact.getArtifactId() + ":"
				+ artifact.getType() + ":" + artifact.getClassifier() + ":"
				+ artifact.getVersion() + " (" + artifact.getFile() + ")";
	}

}
