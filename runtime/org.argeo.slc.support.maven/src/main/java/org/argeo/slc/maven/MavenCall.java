/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.codehaus.plexus.PlexusContainer;

/** A Maven execution. */
public class MavenCall implements Runnable {
	private final static Log log = LogFactory.getLog(MavenCall.class);
	private String basedir;
	private String settings;
	/** Raw command lines arguments */
	private String cl;
	private List<String> goals;
	private List<String> profiles;
	private Map<String, String> properties;

	private Boolean success = null;

	public void run() {
		Thread.currentThread().setContextClassLoader(
				getClass().getClassLoader());
		List<String> args = new ArrayList<String>();
		args.add("-e");
		if (settings != null && !settings.trim().equals("")) {
			args.add("--settings");
			args.add(settings);
		}
		args.add("-f");
		args.add(getBasedirFile().getPath() + "/pom.xml");
		// FIXME manages \" \". Use Commons CLI?
		if (cl != null) {
			String[] clArgs = cl.split(" ");
			args.addAll(Arrays.asList(clArgs));
		}

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
		log.info("Maven call: " + args);

		CustomCli mavenCli = new CustomCli();
		int exitCode = mavenCli.doMain(args.toArray(new String[args.size()]),
				getBasedirFile().getPath(), System.out, System.err);
		if (log.isDebugEnabled())
			log.debug("Maven exit code: " + exitCode);
		if (exitCode == 0)
			success = true;
		else
			success = false;

		PlexusContainer plexusContainer = mavenCli.getContainer();
		if (log.isDebugEnabled())
			log.debug(plexusContainer.getContext().getContextData());
		plexusContainer.dispose();
	}

	/** Removes 'file:' prefix if present */
	protected File getBasedirFile() {
		if (basedir == null)
			throw new SlcException("basedir not set");
		File dir;
		if (basedir.startsWith("file:"))
			dir = new File(basedir.substring("file:".length()));
		else
			dir = new File(basedir);
		return dir;
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

	public void setCl(String cl) {
		this.cl = cl;
	}

	public Boolean getSuccess() {
		return success == null ? false : success;
	}

}
