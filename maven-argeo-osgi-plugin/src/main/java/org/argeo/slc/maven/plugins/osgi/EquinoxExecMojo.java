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
import org.eclipse.core.runtime.adaptor.EclipseStarter;

/**
 * Starts Equinox runtime
 * 
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

	/**
	 * JVM arguments to append
	 * 
	 * @parameter alias="${jvmArgsToAppend}"
	 */
	protected String[] jvmArgsToAppend;

	protected String[] defaultJvmArgs = { "-Xmx128m" };

	/**
	 * Debug port (0 deactivate)
	 * 
	 * @parameter expression="${debug}" default-value="0"
	 * @required
	 */
	protected String debug;

	/**
	 * Equinox args
	 * 
	 * @parameter alias="${args}"
	 */
	protected String[] args;

	/**
	 * Equinox args to append
	 * 
	 * @parameter alias="${argsToAppend}"
	 */
	protected String[] argsToAppend;

	protected String[] defaultArgs = { "-console", "-configuration",
			"target/slc/conf", "-data", "target/slc/data" };

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

	/**
	 * Whether to create a new JVM
	 * 
	 * @parameter expression="${fork}" default-value="false"
	 * @required
	 */
	protected boolean fork;

	/**
	 * Whether to wait for the runtime to exit
	 * 
	 * @parameter expression="${wait}" default-value="true"
	 * @required
	 */
	protected boolean wait;

	/**
	 * Number of milliseconds to pause after having started the server (when
	 * ${wait}=false)
	 * 
	 * @parameter expression="${pause}" default-value="0"
	 * @required
	 */
	protected long pause;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if ("bundles".equals(project.getArtifact().getType())) {
			System.out.println("Skip artifact of type bundles "
					+ artifactToString(project.getArtifact()));
			return;
		}

		String originalUserDir = System.getProperty("user.dir");
		try {
			LocationsStruct locationsStruct = listOsgiLocations();
			if (fork)
				execForked(locationsStruct);
			else
				execNonForked(locationsStruct);
		} catch (Exception e) {
			throw new MojoExecutionException("Cannot execute OSGi runtime", e);
		} finally {
			System.setProperty("user.dir", originalUserDir);
		}
	}

	protected LocationsStruct listOsgiLocations() throws Exception {
		LocationsStruct locationsStruct = new LocationsStruct();

		Set dependencies = mavenDependencyManager
				.getTransitiveProjectDependencies(project, remoteRepos, local);

		StringBuffer osgiLocations = new StringBuffer();
		List bundleArtifacts = new ArrayList();
		boolean first = true;
		for (Iterator it = dependencies.iterator(); it.hasNext();) {
			Artifact depArtifact = (Artifact) it.next();
			printArtifact(depArtifact);

			if (depArtifact.getArtifactId().equals(equinoxArtifactId)) {
				locationsStruct.equinoxArtifact = depArtifact;
			} else if (depArtifact.getArtifactId().equals(osgiBootArtifactId)) {
				locationsStruct.osgiBootArtifact = depArtifact;
			} else {
				bundleArtifacts.add(depArtifact);

				if ("jar".equals(depArtifact.getType())) {
					// Add to OSGi locations
					if (first)
						first = false;
					else
						osgiLocations.append(File.pathSeparatorChar);

					osgiLocations.append(depArtifact.getFile()
							.getCanonicalPath()
							.replace(File.separatorChar, '/'));
				}
			}
		}
		locationsStruct.osgiLocations = osgiLocations.toString();
		return locationsStruct;
	}

	protected void execNonForked(LocationsStruct locationsStruct)
			throws Exception {
		// Set defaults
		if (args == null)
			args = defaultArgs;

		// if (!execDir.exists())
		// execDir.mkdirs();
		// System.setProperty("user.dir", execDir.getCanonicalPath());

		// Build command
		List cmdList = new ArrayList();

		// System properties
		if (!systemProperties.containsKey("osgi.bundles")) {
			if (locationsStruct.osgiBootArtifact == null)
				throw new Exception("No SLC OSGi boot bundle available.");
			System.setProperty("osgi.bundles", locationsStruct.osgiBootArtifact
					.getFile().getCanonicalPath()
					+ "@start");
		}

		if (!systemProperties.containsKey("slc.osgi.locations"))
			System.setProperty("slc.osgi.locations",
					locationsStruct.osgiLocations);

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
			System.setProperty(key.toString(), strValue);
		}

		// Program arguments
		cmdList.addAll(Arrays.asList(args));
		if (argsToAppend != null)
			cmdList.addAll(Arrays.asList(argsToAppend));

		String[] cmd = (String[]) cmdList.toArray(new String[0]);
		System.out.println("Equinox arguments (non forked):");
		printCommand(cmd);

		EclipseStarter.startup(cmd, null);

		if (wait) {
			while (EclipseStarter.isRunning()) {
				Thread.sleep(500);
			}
		} else {
			Thread.sleep(pause);
		}
	}

	protected void execForked(LocationsStruct locationsStruct) throws Exception {
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
		// JVM
		cmdList.add(jvm);
		// JVM arguments
		cmdList.addAll(Arrays.asList(jvmArgs));

		if (!"0".equals(debug))
			cmdList
					.add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address="
							+ debug);

		if (jvmArgsToAppend != null)
			cmdList.addAll(Arrays.asList(jvmArgsToAppend));

		// System properties
		if (!systemProperties.containsKey("osgi.bundles"))
			cmdList.add("-Dosgi.bundles="
					+ locationsStruct.osgiBootArtifact.getFile()
							.getCanonicalPath() + "@start");
		if (!systemProperties.containsKey("slc.osgi.locations"))
			cmdList
					.add("-Dslc.osgi.locations="
							+ locationsStruct.osgiLocations);
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

		// Equinox jar
		cmdList.add("-jar");
		cmdList.add(locationsStruct.equinoxArtifact.getFile()
				.getCanonicalPath());

		// Program arguments
		cmdList.addAll(Arrays.asList(args));
		if (argsToAppend != null)
			cmdList.addAll(Arrays.asList(argsToAppend));

		String[] cmd = (String[]) cmdList.toArray(new String[0]);
		System.out.println("Execute Equinox command (forked):");
		printCommand(cmd);

		SystemCall systemCall = new SystemCall(execDir.getCanonicalPath(), cmd,
				true);
		if (wait) {
			systemCall.run();
		} else {
			new Thread(systemCall).start();
			Thread.sleep(pause);
		}
	}

	protected void printArtifact(Artifact artifact) {
		if (getLog().isDebugEnabled())
			getLog().debug(artifactToString(artifact));
	}

	protected void printCommand(String[] cmd) {
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
	}

	protected static String artifactToString(Artifact artifact) {
		return artifact.getGroupId() + ":" + artifact.getArtifactId() + ":"
				+ artifact.getType() + ":" + artifact.getClassifier() + ":"
				+ artifact.getVersion() + " (" + artifact.getFile() + ")";
	}

	protected class LocationsStruct {
		protected Artifact equinoxArtifact = null;
		protected Artifact osgiBootArtifact = null;
		protected String osgiLocations = null;
	}
}
