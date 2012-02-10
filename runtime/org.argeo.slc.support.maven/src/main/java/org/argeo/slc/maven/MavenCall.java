package org.argeo.slc.maven;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.plexus.PlexusContainer;

/** A Maven execution. */
public class MavenCall implements Runnable {
	private final static Log log = LogFactory.getLog(MavenCall.class);
	private String basedir;
	private String settings;
	private List<String> goals;
	private List<String> profiles;
	private Map<String, String> properties;

	public void run() {
		Thread.currentThread().setContextClassLoader(
				getClass().getClassLoader());
		List<String> args = new ArrayList<String>();
		args.add("-e");
		if (settings != null) {
			args.add("--settings");
			args.add(settings);
		}
		args.add("-f");
		args.add(basedir + "/pom.xml");
		if (goals != null)
			args.addAll(goals);
		if (profiles != null)
			for (String profile : profiles)
				args.add("-P" + profile);
		if (properties != null)
			for (String key : properties.keySet())
				args.add("-D" + key + "=\"" + properties.get(key) + "\"");

		// String[] goals = { "-o", "-e", "-f", basedir + "/pom.xml", "clean",
		// "install" };

		// String m2Home = "/opt/apache-maven-3.0.1";
		// System.setProperty("classworlds.conf", m2Home + "/bin/m2.conf");
		// System.setProperty("maven.home", m2Home);
		//
		// Launcher.main(goals);

		CustomCli mavenCli = new CustomCli();
		mavenCli.doMain(args.toArray(new String[args.size()]), basedir,
				System.out, System.err);

		PlexusContainer plexusContainer = mavenCli.getContainer();
		if (log.isDebugEnabled())
			log.debug(plexusContainer.getContext().getContextData());
		plexusContainer.dispose();
	}

	public void setBasedir(String basedir) {
		this.basedir = basedir;
	}

	public void setSettings(String settings) {
		this.settings = settings;
	}

	public void setGoals(List<String> goals) {
		this.goals = goals;
	}

	public void setProfiles(List<String> profiles) {
		this.profiles = profiles;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

}
