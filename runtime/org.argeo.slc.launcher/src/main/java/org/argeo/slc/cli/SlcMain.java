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
package org.argeo.slc.cli;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.UUID;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;

import org.argeo.osgi.boot.OsgiBoot;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

/** Configures an SLC runtime and runs a process. */
public class SlcMain implements PrivilegedAction<String> {
	public final static String NIX = "NIX";
	public final static String WINDOWS = "WINDOWS";
	public final static String SOLARIS = "SOLARIS";

	public final static String os;
	public final static String slcDirName = ".slc";
	final static File homeDir = new File(System.getProperty("user.home"));

	static {
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Win"))
			os = WINDOWS;
		else if (osName.startsWith("Solaris"))
			os = SOLARIS;
		else
			os = NIX;
	}

	private Long timeout = 30 * 1000l;
	private final String[] args;
	private final File confDir;
	private final File dataDir;
	private final File modulesDir;

	private final List<String> bundlesToStart = new ArrayList<String>();

	public SlcMain(String[] args, File confDir, File dataDir, File modulesDir) {
		this.args = args;
		this.confDir = confDir;
		this.dataDir = dataDir;
		this.modulesDir = modulesDir;
		bundlesToStart.add("org.springframework.osgi.extender");
		bundlesToStart.add("org.argeo.node.repo.jackrabbit");
		bundlesToStart.add("org.argeo.security.dao.os");
		bundlesToStart.add("org.argeo.slc.node.jackrabbit");
		bundlesToStart.add("org.argeo.slc.agent");
		bundlesToStart.add("org.argeo.slc.agent.jcr");
		if (args.length == 0)
			bundlesToStart.add("org.argeo.slc.support.equinox");
		// bundlesToStart.add("org.argeo.slc.agent.cli");
	}

	public String run() {
		long begin = System.currentTimeMillis();

		Framework framework = null;
		try {
			info("## Date : " + new Date());
			info("## Data : " + dataDir.getCanonicalPath());

			// Start Equinox
			ServiceLoader<FrameworkFactory> ff = ServiceLoader
					.load(FrameworkFactory.class);
			FrameworkFactory frameworkFactory = ff.iterator().next();
			Map<String, String> configuration = new HashMap<String, String>();
			configuration.put("osgi.configuration.area",
					confDir.getCanonicalPath());
			configuration.put("osgi.instance.area", dataDir.getCanonicalPath());
			if (args.length == 0) {
				// configuration.put("osgi.clean", "true");
				configuration.put("osgi.console", "");
			}

			// Spring configs currently require System properties
			System.getProperties().putAll(configuration);

			framework = frameworkFactory.newFramework(configuration);
			framework.start();
			BundleContext bundleContext = framework.getBundleContext();

			// OSGi bootstrap
			OsgiBoot osgiBoot = new OsgiBoot(bundleContext);

			// working copy modules
			if (modulesDir.exists())
				osgiBoot.installUrls(osgiBoot.getBundlesUrls(modulesDir
						.getCanonicalPath() + ";in=*;ex=.gitignore"));

			// system modules
			if (System.getProperty(OsgiBoot.PROP_ARGEO_OSGI_BUNDLES) != null)
				osgiBoot.installUrls(osgiBoot.getBundlesUrls(System
						.getProperty(OsgiBoot.PROP_ARGEO_OSGI_BUNDLES)));
			else
				osgiBoot.installUrls(osgiBoot.getBundlesUrls(System
						.getProperty("user.home") + "/.slc/modules/;in=**"));

			// Start runtime
			osgiBoot.startBundles(bundlesToStart);

			// Find SLC Agent
			ServiceReference sr = null;
			while (sr == null) {
				sr = bundleContext
						.getServiceReference("org.argeo.slc.execution.SlcAgentCli");
				if (System.currentTimeMillis() - begin > timeout)
					throw new RuntimeException("Cannot find SLC agent CLI");
				Thread.sleep(100);
			}
			Object agentCli = bundleContext.getService(sr);

			// Initialization completed
			long duration = System.currentTimeMillis() - begin;
			info("[[ Initialized in " + (duration / 1000) + "s "
					+ (duration % 1000) + "ms ]]");

			if (args.length == 0)
				return null;// console mode

			// Subject.doAs(Subject.getSubject(AccessController.getContext()),
			// new AgentCliCall(agentCli));
			Class<?>[] parameterTypes = { String[].class };
			Method method = agentCli.getClass().getMethod("process",
					parameterTypes);
			Object[] methodArgs = { args };
			Object ret = method.invoke(agentCli, methodArgs);

			// Shutdown OSGi runtime
			framework.stop();
			framework.waitForStop(60 * 1000);

			return ret.toString();
		} catch (Exception e) {
			// Shutdown OSGi runtime
			if (framework != null)
				try {
					framework.stop();
					framework.waitForStop(15 * 1000);
				} catch (Exception silent) {
				}
			throw new RuntimeException("Cannot run SLC command line", e);
		} finally {

		}
	}

	public static void main(String[] args) {
		try {
			// Prepare directories
			File executionDir = new File(System.getProperty("user.dir"));
			File slcDir;
			Boolean isTransient = false;
			if (isTransient) {
				File tempDir = new File(System.getProperty("java.io.tmpdir")
						+ "/" + System.getProperty("user.name"));
				slcDir = new File(tempDir, "slc-"
						+ UUID.randomUUID().toString());
				slcDir.mkdirs();
				System.setProperty("argeo.node.repo.configuration",
						"osgibundle:repository-memory.xml");
			} else {
				slcDir = findSlcDir(executionDir);
				if (slcDir == null) {
					slcDir = new File(executionDir, slcDirName);
					slcDir.mkdirs();
					info("## Creating an SLC node at " + slcDir + " ...");
				}
			}

			File dataDir = new File(slcDir, "data");
			if (!dataDir.exists())
				dataDir.mkdirs();

			File confDir = new File(slcDir, "conf");
			if (!confDir.exists())
				confDir.mkdirs();

			File modulesDir = new File(slcDir, "modules");

			// JAAS
			File jaasFile = new File(confDir, "jaas.config");
			if (!jaasFile.exists())
				copyResource("/org/argeo/slc/cli/jaas.config", jaasFile);
			System.setProperty("java.security.auth.login.config",
					jaasFile.getCanonicalPath());

			// log4j
			File log4jFile = new File(confDir, "log4j.properties");
			if (!log4jFile.exists())
				copyResource("/org/argeo/slc/cli/log4j.properties", log4jFile);
			System.setProperty("log4j.configuration",
					"file://" + log4jFile.getCanonicalPath());
			// Run as a privileged action
			LoginContext lc = new LoginContext(os);
			lc.login();

			Subject subject = Subject.getSubject(AccessController.getContext());
			Subject.doAs(subject, new SlcMain(args, confDir, dataDir,
					modulesDir));

			if (args.length != 0)
				System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Recursively look in parent directories for a directory named
	 * {@link #slcDirName}
	 */
	protected static File findSlcDir(File currentDir) {
		File slcDir = new File(currentDir, slcDirName);
		if (slcDir.exists() && slcDir.isDirectory())
			return slcDir;
		File parentDir = currentDir.getParentFile();
		if (parentDir == null)
			return null;
		try {
			// ~/.slc reserved for agent
			if (parentDir.getCanonicalPath().equals(homeDir.getCanonicalPath()))
				return null;
		} catch (IOException e) {
			throw new RuntimeException("Cannot check home directory", e);
		}
		return findSlcDir(parentDir);
	}

	protected static void copyResource(String resource, File targetFile) {
		InputStream input = null;
		FileOutputStream output = null;
		try {
			input = SlcMain.class.getResourceAsStream(resource);
			output = new FileOutputStream(targetFile);
			byte[] buf = new byte[8192];
			while (true) {
				int length = input.read(buf);
				if (length < 0)
					break;
				output.write(buf, 0, length);
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot write " + resource + " file to "
					+ targetFile, e);
		} finally {
			try {
				input.close();
			} catch (Exception ignore) {
			}
			try {
				output.close();
			} catch (Exception ignore) {
			}
		}

	}

	protected static void info(Object msg) {
		System.out.println(msg);
	}

	protected static void err(Object msg) {
		System.err.println(msg);
	}

	protected static void debug(Object msg) {
		System.out.println(msg);
	}

}

// private String bundlesToInstall = System.getProperty("user.home")
// +
// "/dev/src/slc/dep/org.argeo.slc.dep.minimal/target/dependency;in=*.jar,"
// + System.getProperty("user.home")
// + "/dev/src/slc/demo/modules;in=*;ex=pom.xml;ex=.svn";

// ServiceTracker agentTracker = new ServiceTracker(bundleContext,
// "org.argeo.slc.execution.SlcAgentCli", null);
// agentTracker.open();
// final Object agentCli = agentTracker.waitForService(30 * 1000);
// if (agentCli == null)
// throw new RuntimeException("Cannot find SLC agent CLI");

// protected class AgentCliCall implements PrivilegedAction<String> {
// private final Object agentCli;
//
// public AgentCliCall(Object agentCli) {
// super();
// this.agentCli = agentCli;
// }
//
// public String run() {
// try {
// Class<?>[] parameterTypes = { String[].class };
// Method method = agentCli.getClass().getMethod("process",
// parameterTypes);
// Object[] methodArgs = { args };
// Object ret = method.invoke(agentCli, methodArgs);
// return ret.toString();
// } catch (Exception e) {
// throw new RuntimeException("Cannot run "
// + Arrays.toString(args) + " on " + agentCli, e);
// }
// }
//
// }

