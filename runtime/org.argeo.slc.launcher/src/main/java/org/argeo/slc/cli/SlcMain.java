/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
import java.util.List;
import java.util.Properties;

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
public class SlcMain {
	/** Unique launch module */
	public static String UNIQUE_LAUNCH_MODULE_PROPERTY = "slc.launch.module";

	/** Unique launch flow */
	public static String UNIQUE_LAUNCH_FLOW_PROPERTY = "slc.launch.flow";

	// public enum Type {
	// standalone, agent, server
	// }

	// private static Boolean debug = true;

	// private final static String BOOTSTRAP_LOG4J_CONFIG =
	// "org/argeo/slc/cli/bootstrapLog4j.properties";
	// private final static String DEFAULT_AGENT_CONTEXT =
	// "classpath:org/argeo/slc/cli/spring-agent-default.xml";

	// private final static Option typeOpt = OptionBuilder.withLongOpt("mode")
	// .withArgName("mode").hasArg()
	// .withDescription("Execution type, one of: " + listTypeValues())
	// .create('t');
	//
	// private final static Option propertyOpt = OptionBuilder
	// .withLongOpt("property").withArgName("prop1=val1,prop2=val2")
	// .hasArgs().withValueSeparator(',')
	// .withDescription("use value for given property").create('p');
	//
	// private final static Option propertiesOpt = OptionBuilder
	// .withLongOpt("properties").withArgName("properties file").hasArgs()
	// .withValueSeparator(',')
	// .withDescription("load properties from file (-p has priority)")
	// .create('P');
	//
	// private final static Option moduleOpt =
	// OptionBuilder.withLongOpt("module")
	// .withArgName("module").hasArg().withDescription("Execution module")
	// .create('m');
	//
	// private final static Option flowsOpt = OptionBuilder.withLongOpt("flows")
	// .withArgName("flows").hasArg().withDescription("Flows to execute")
	// .create('f');
	//
	// private final static Option runtimeOpt = OptionBuilder
	// .withLongOpt("runtime").withArgName("runtime").hasArg()
	// .withDescription("Runtime URL").create('r');

	private final static Options options;

	private final static String commandName = "slc";

	// private static String bundlesToInstall = "/usr/share/osgi;in=*.jar";
	private static String bundlesToInstall = System.getProperty("user.home")
			+ "/dev/src/slc/runtime/org.argeo.slc.launcher/target/dependency;in=*.jar";

	// private static String bundlesToStart =
	// "org.springframework.osgi.extender,"
	// + "org.argeo.node.repofactory.jackrabbit,"
	// + "org.argeo.node.repo.jackrabbit," + "org.argeo.security.dao.os,"
	// + "org.argeo.slc.node.jackrabbit," + "org.argeo.slc.agent,"
	// + "org.argeo.slc.agent.jcr";
	private static String bundlesToStart = "org.springframework.osgi.extender,"
			+ "org.argeo.slc.agent";

	static {
		options = new Options();
		// options.addOption(typeOpt);
		// options.addOption(moduleOpt);
		// options.addOption(flowsOpt);
		// options.addOption(propertyOpt);
		// options.addOption(propertiesOpt);
		// options.addOption(runtimeOpt);
	}

	@SuppressWarnings({ "unchecked" })
	public static void main(String[] args) {
		// Type type = null;
		// Properties properties = new Properties();
		// String flows = null;
		// String urlStr = null;

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
			File dataDir = new File(slcDir, "data");
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

			// start runtime
			osgiBoot.startBundles(bundlesToStart);

			// Bundle bundle = (Bundle) osgiBoot.getBundlesBySymbolicName().get(
			// "org.argeo.slc.specs");
			// bundle.loadClass(Execu)
			//
			// // retrieve modulesManager
			// BundlesManager bundlesManager = new
			// BundlesManager(bundleContext);
			// ExecutionModulesManager modulesManager = bundlesManager
			// .getSingleService(ExecutionModulesManager.class, null, true);
			//
			// RealizedFlow realizedFlow = RealizedFlow.create(module, null,
			// flow,
			// null);
			// modulesManager.start(new BasicNameVersion(module, "0.0.0"));
			// modulesManager.execute(realizedFlow);

			// osgiBoot.bootstrap();
			// osgiBoot.bootstrap();

			// Mode
			// String typeStr = cl.getOptionValue(typeOpt.getOpt());
			// if (typeStr == null) {
			// type = Type.standalone;
			// } else {
			// try {
			// type = Type.valueOf(typeStr);
			// } catch (IllegalArgumentException e) {
			// throw new SlcException("Unrecognized mode '" + typeStr
			// + "'", e);
			// }
			// }
			//
			// // Script
			// if (type.equals(Type.standalone)) {
			// if (!cl.hasOption(moduleOpt.getOpt()))
			// throw new SlcException("Type " + Type.standalone
			// + " requires option '" + moduleOpt.getLongOpt()
			// + "'");
			// module = cl.getOptionValue(moduleOpt.getOpt());
			//
			// // Targets
			// if (cl.hasOption(flowsOpt.getOpt()))
			// flows = cl.getOptionValue(flowsOpt.getOpt());
			// }
			//
			// // Properties
			// if (cl.hasOption(propertiesOpt.getOpt())) {
			// for (String propertyFile : cl.getOptionValues(propertiesOpt
			// .getOpt())) {
			// loadPropertyFile(properties, propertyFile);
			// }
			// }
			// if (cl.hasOption(propertyOpt.getOpt())) {
			// for (String property : cl.getOptionValues(propertyOpt.getOpt()))
			// {
			// addProperty(properties, property);
			// }
			// }
			//
			// // Runtime
			// if (cl.hasOption(runtimeOpt.getOpt())) {
			// urlStr = cl.getOptionValue(runtimeOpt.getOpt());
			// }
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

		// if (debug) {
		// debug("Mode: " + type);
		// if (urlStr != null)
		// debug("Runtime: " + urlStr);
		// debug("User properties: " + properties);
		// if (module != null)
		// debug("Module: " + module);
		// if (flows != null)
		// debug("Flows: " + flows);
		// }
		//
		// // Standalone
		// if (type.equals(Type.standalone)) {
		// }
		// // Agent
		// else if (type.equals(Type.agent)) {
		// }
	}

	public static void printUsage() {
		new HelpFormatter().printHelp(commandName, options, true);
	}

	// private static String listTypeValues() {
	// StringBuffer buf = new StringBuffer("");
	// for (Type mode : Type.values()) {
	// buf.append(mode).append(", ");
	// }
	// String str = buf.toString();
	// // unsafe, but there will be at least one value in the enum
	// return str.substring(0, str.length() - 2);
	// }

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

	private static void badExit() {
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
