package org.argeo.slc.maven.plugins.osgi;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.argeo.slc.maven.plugin.SystemCall;

/**
 * @goal equinox-exec
 * */
public class EquinoxExecMojo extends AbstractOsgiMojo {
	/**
	 * Used to look up Artifacts in the remote repository.
	 * 
	 * @parameter expression=
	 *            "${component.org.apache.maven.artifact.resolver.ArtifactResolver}"
	 * @required
	 * @readonly
	 */
	protected org.apache.maven.artifact.resolver.ArtifactResolver resolver;

	/**
	 * Used to look up Artifacts in the remote repository.
	 * 
	 * @parameter expression=
	 *            "${component.org.apache.maven.artifact.factory.ArtifactFactory}"
	 * @required
	 * @readonly
	 */
	protected org.apache.maven.artifact.factory.ArtifactFactory factory;

	/** @component */
	private ArtifactMetadataSource artifactMetadataSource;

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
	 * @parameter expression="${jvm}" default-value="java"
	 * @required
	 */
	protected String jvm;

	/**
	 * JVM arguments
	 * 
	 * @parameter alias="${jvmArgs}"
	 */
	protected String[] jvmArgs;

	protected String[] defaultJvmArgs = { "-Xmx256m" };

	/**
	 * Equinox args
	 * 
	 * @parameter alias="${args}"
	 */
	protected String[] args;

	protected String[] defaultArgs = { "-clean", "-console", "-configuration",
			"conf" };

	/**
	 * Execution directory
	 * 
	 * @parameter expression="${execDir}"
	 * @required
	 */
	protected File execDir;

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			Artifact equinoxArtifact = null;
			Artifact osgiBootArtifact = null;

			Set artifacts = project.createArtifacts(this.factory, null, null);

			ArtifactResolutionResult arr = resolver.resolveTransitively(
					artifacts, project.getArtifact(), local, remoteRepos,
					artifactMetadataSource, null);
			// Order, just for display
			Set dependencies = new TreeSet(new Comparator() {
				public int compare(Object o1, Object o2) {
					Artifact a1 = (Artifact) o1;
					Artifact a2 = (Artifact) o2;

					if (!a1.getGroupId().equals(a2.getGroupId()))
						return a1.getGroupId().compareTo(a2.getGroupId());
					else
						return a1.getArtifactId().compareTo(a2.getArtifactId());
				}
			});
			dependencies.addAll(arr.getArtifacts());

			StringBuffer osgiLocations = new StringBuffer();
			List bundleArtifacts = new ArrayList();
			boolean first = true;
			// Set dependencies = project.getArtifacts();
			for (Iterator it = dependencies.iterator(); it.hasNext();) {
				Artifact depArtifact = (Artifact) it.next();
				System.out.println(depArtifact.getGroupId() + ":"
						+ depArtifact.getArtifactId() + ":"
						+ depArtifact.getType() + ":"
						+ depArtifact.getVersion() + " ("
						+ depArtifact.getFile() + ")");
				if (depArtifact.getArtifactId().equals(equinoxArtifactId))
					equinoxArtifact = depArtifact;
				else if (depArtifact.getArtifactId().equals(osgiBootArtifactId))
					osgiBootArtifact = depArtifact;
				else {
					bundleArtifacts.add(depArtifact);

					if (first)
						first = false;
					else
						osgiLocations.append(File.pathSeparatorChar);
					osgiLocations.append(depArtifact.getFile()
							.getCanonicalPath()
							.replace(File.separatorChar, '/'));
				}
			}


			// Set defaults
			if(jvmArgs==null)
				jvmArgs=defaultJvmArgs;
			if(args==null)
				args=defaultArgs;

			
			List cmdList = new ArrayList();
			cmdList.add(jvm);
			cmdList.addAll(Arrays.asList(jvmArgs));
			cmdList.add("-Dosgi.bundles="
					+ osgiBootArtifact.getFile().getCanonicalPath() + "@start");
			cmdList.add("-Dslc.osgi.locations=" + osgiLocations);
			cmdList.add("-jar");
			cmdList.add(equinoxArtifact.getFile().getCanonicalPath());
			cmdList.addAll(Arrays.asList(args));
			
			String[] cmd = (String[])cmdList.toArray(new String[0]);

//			String cmdOld = jvm + " " + jvmArgs + " " + "-Dosgi.bundles="
//					+ osgiBootArtifact.getFile().getCanonicalPath() + "@start "
//					+ "-Dslc.osgi.locations=" + osgiLocations + " -jar "
//					+ equinoxArtifact.getFile().getCanonicalPath() + " " + args;
			for(int i=0;i<cmd.length;i++)
				System.out.print(cmd[i]);
			System.out.print('\n');
			
			SystemCall systemCall = new SystemCall(execDir.getCanonicalPath(),
					cmd, true);
			systemCall.run();

		} catch (Exception e) {
			throw new MojoExecutionException("Cannot execute Equinox", e);
		}

	}
}
