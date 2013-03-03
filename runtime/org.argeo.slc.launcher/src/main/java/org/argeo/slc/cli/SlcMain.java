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
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.UUID;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;

import org.argeo.osgi.boot.OsgiBoot;
import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.util.tracker.ServiceTracker;

/** Configures an SLC runtime and runs a process. */
public class SlcMain implements Runnable {
	private final String[] args;

	// private static String bundlesToInstall = "/usr/share/osgi;in=*.jar";
	private String bundlesToInstall = System.getProperty("user.home")
			+ "/dev/src/slc/dep/org.argeo.slc.dep.minimal/target/dependency;in=*.jar,"
			+ System.getProperty("user.home")
			+ "/dev/src/slc/demo/modules;in=*;ex=pom.xml;ex=.svn";

	private final List<String> bundlesToStart = new ArrayList<String>();

	public SlcMain(String[] args) {
		this.args = args;
		bundlesToStart.add("org.springframework.osgi.extender");
		bundlesToStart.add("org.argeo.node.repo.jackrabbit");
		bundlesToStart.add("org.argeo.security.dao.os");
		bundlesToStart.add("org.argeo.slc.node.jackrabbit");
		bundlesToStart.add("org.argeo.slc.agent");
		bundlesToStart.add("org.argeo.slc.agent.jcr");
	}

	public void run() {
		final LoginContext lc;
		try {
			// Authenticate
			lc = new LoginContext("NIX");
			lc.login();

			// Prepare directories
			String executionDir = System.getProperty("user.dir");
			File slcDir = new File(executionDir, "target/.slc");
			File tempDir = new File(System.getProperty("java.io.tmpdir"));

			File dataDir = new File(tempDir, "slc-data-"
					+ UUID.randomUUID().toString());
			if (!dataDir.exists())
				dataDir.mkdirs();

			File confDir = new File(slcDir, "conf");
			if (!confDir.exists())
				confDir.mkdirs();

			System.setProperty("log4j.configuration", "file:./log4j.properties");
			System.setProperty("argeo.node.repo.configuration",
					"osgibundle:repository-memory.xml");

			// Start Equinox
			ServiceLoader<FrameworkFactory> ff = ServiceLoader
					.load(FrameworkFactory.class);
			FrameworkFactory frameworkFactory = ff.iterator().next();
			Map<String, String> configuration = new HashMap<String, String>();
			configuration.put("osgi.configuration.area",
					confDir.getCanonicalPath());
			configuration.put("osgi.instance.area", dataDir.getCanonicalPath());
			configuration.put("osgi.clean", "true");

			// Spring configs currently require System properties
			System.getProperties().putAll(configuration);

			Framework framework = frameworkFactory.newFramework(configuration);
			framework.start();
			BundleContext bundleContext = framework.getBundleContext();
			// String[] osgiRuntimeArgs = { "-configuration",
			// confDir.getCanonicalPath(), "-data",
			// dataDir.getCanonicalPath(), "-clean" };
			// BundleContext bundleContext = EclipseStarter.startup(
			// osgiRuntimeArgs, null);

			// OSGi bootstrap
			OsgiBoot osgiBoot = new OsgiBoot(bundleContext);
			osgiBoot.installUrls(osgiBoot.getBundlesUrls(bundlesToInstall));

			// Start runtime
			osgiBoot.startBundles(bundlesToStart);

			// Find SLC Agent
			ServiceTracker agentTracker = new ServiceTracker(bundleContext,
					"org.argeo.slc.execution.SlcAgentCli", null);
			agentTracker.open();
			final Object agentCli = agentTracker.waitForService(30 * 1000);

			// Run as a privileged action
			Subject.doAs(Subject.getSubject(AccessController.getContext()),
					new PrivilegedAction<String>() {

						public String run() {
							try {
								Class<?>[] parameterTypes = { String[].class };
								Method method = agentCli.getClass().getMethod(
										"process", parameterTypes);
								Object[] methodArgs = { args };
								Object ret = method
										.invoke(agentCli, methodArgs);
								return ret.toString();
							} catch (Exception e) {
								throw new RuntimeException("Cannot run "
										+ Arrays.toString(args) + " on "
										+ agentCli, e);
							}
						}

					});

			// Shutdown OSGi runtime
			framework.stop();
			framework.waitForStop(60 * 1000);

			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		new SlcMain(args).run();
	}

	protected static void info(Object msg) {
		System.out.println(msg);
	}

	protected static void debug(Object msg) {
		System.out.println(msg);
	}

}
