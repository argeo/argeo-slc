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
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.argeo.osgi.boot.OsgiBoot;
import org.argeo.slc.SlcException;
import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

@SuppressWarnings("restriction")
public class SlcMain implements Runnable {
	/** Unique launch module */
	public final static String UNIQUE_LAUNCH_MODULE_PROPERTY = "slc.launch.module";

	/** Unique launch flow */
	public final static String UNIQUE_LAUNCH_FLOW_PROPERTY = "slc.launch.flow";

	/** Unique launch flow */
	public final static String UNIQUE_LAUNCH_ARGS_PROPERTY_BASE = "slc.launch.args";

	private final Options options = new Options();
	private final String[] args;

	private final String commandName = "slc";

	// private static String bundlesToInstall = "/usr/share/osgi;in=*.jar";
	private String bundlesToInstall = System.getProperty("user.home")
			+ "/dev/src/slc/dep/org.argeo.slc.dep.minimal/target/dependency;in=*.jar,"
			+ System.getProperty("user.home")
			+ "/dev/src/slc/demo/modules;in=*;ex=pom.xml;ex=.svn";

	private final List<String> bundlesToStart = new ArrayList<String>();

	public SlcMain(String[] args) {
		this.args = args;
		// bundlesToStart.add("org.springframework.osgi.extender");
		// bundlesToStart.add("org.argeo.slc.agent");

		bundlesToStart.add("org.springframework.osgi.extender");
		bundlesToStart.add("org.argeo.node.repo.jackrabbit");
		bundlesToStart.add("org.argeo.security.dao.os");
		bundlesToStart.add("org.argeo.slc.node.jackrabbit");
		bundlesToStart.add("org.argeo.slc.agent");
		bundlesToStart.add("org.argeo.slc.agent.jcr");
	}

	@SuppressWarnings("unchecked")
	public void run() {
		String module = null;
		String moduleUrl = null;
		String flow = null;

		try {

			CommandLineParser clParser = new GnuParser();
			CommandLine cl = clParser.parse(options, args);

			List<String> arguments = cl.getArgList();
			if (arguments.size() == 0) {
				// TODO default behaviour
			} else {
				module = arguments.get(0);
				File moduleFile = new File(module);
				if (moduleFile.exists()) {
					if (moduleFile.isDirectory()) {
						moduleUrl = "reference:file:"
								+ moduleFile.getCanonicalPath();
					} else {
						moduleUrl = "file:" + moduleFile.getCanonicalPath();
					}
				}

				if (arguments.size() == 1) {
					// TODO module info
				} else {
					flow = arguments.get(1);
				}
			}

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

			BundleContext bundleContext = null;
			try {
				String[] osgiRuntimeArgs = { "-configuration",
						confDir.getCanonicalPath(), "-data",
						dataDir.getCanonicalPath(), "-console", "-clean" };
				bundleContext = EclipseStarter.startup(osgiRuntimeArgs, null);
			} catch (Exception e) {
				throw new RuntimeException("Cannot start Equinox.", e);
			}

			// OSGi bootstrap
			OsgiBoot osgiBoot = new OsgiBoot(bundleContext);
			osgiBoot.installUrls(osgiBoot.getBundlesUrls(bundlesToInstall));

			if (moduleUrl != null) {
				Bundle bundle = osgiBoot.installUrl(moduleUrl);
				module = bundle.getSymbolicName();
				// TODO deal with version
			}

			System.setProperty(UNIQUE_LAUNCH_MODULE_PROPERTY, module);
			System.setProperty(UNIQUE_LAUNCH_FLOW_PROPERTY, flow);
			System.setProperty("log4j.configuration", "file:./log4j.properties");
			System.setProperty("argeo.node.repo.configuration",
					"osgibundle:repository-memory.xml");
			// start runtime
			osgiBoot.startBundles(bundlesToStart);

		} catch (ParseException e) {
			System.err.println("Problem with command line arguments. "
					+ e.getMessage());
			badExit();
		} catch (SlcException e) {
			System.err.println(e.getMessage());
			badExit();
		} catch (Exception e) {
			System.err.println("Unexpected exception when bootstrapping.");
			e.printStackTrace();
			badExit();
		}
	}

	public static void main(String[] args) {
		new SlcMain(args).run();
	}

	public void printUsage() {
		new HelpFormatter().printHelp(commandName, options, true);
	}

	protected static void addProperty(Properties properties, String property) {
		int eqIndex = property.indexOf('=');
		if (eqIndex == 0)
			throw new SlcException("Badly formatted property " + property);

		if (eqIndex > 0) {
			String key = property.substring(0, eqIndex);
			String value = property.substring(eqIndex + 1);
			properties.setProperty(key, value);

		} else {
			properties.setProperty(property, "true");
		}
	}

	protected static void loadPropertyFile(Properties properties,
			String propertyFile) {
		FileInputStream in = null;
		try {
			in = new FileInputStream(propertyFile);
			properties.load(in);
		} catch (Exception e) {
			throw new SlcException("Could not load proeprty file "
					+ propertyFile);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	private void badExit() {
		printUsage();
		System.exit(1);
	}

	protected static void info(Object msg) {
		System.out.println(msg);
	}

	protected static void debug(Object msg) {
		System.out.println(msg);
	}
}
