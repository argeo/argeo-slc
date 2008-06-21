package org.argeo.slc.cli;

import java.io.File;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.SlcException;
import org.argeo.slc.logging.Log4jUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class SlcMain {
	public enum Mode {
		single, agent
	}

	private static Log log = null;

	private final static String BOOTSTRAP_LOG4J_CONFIG = "org/argeo/slc/cli/bootstrapLog4j.properties";

	private final static Option modeOpt = OptionBuilder.withLongOpt("mode")
			.withArgName("mode").hasArg().isRequired().withDescription(
					"SLC execution mode, one of: " + listModeValues()).create(
					'm');

	private final static Option propertyOpt = OptionBuilder.withLongOpt(
			"property").withArgName("prop1=val1,prop2=val2").hasArgs()
			.withValueSeparator(',').withDescription(
					"use value for given property").create('p');

	private final static Option scriptOpt = OptionBuilder.withLongOpt("script")
			.withArgName("script").hasArg().withDescription(
					"SLC script to execute").create('s');

	private final static Option targetsOpt = OptionBuilder.withLongOpt(
			"targets").withArgName("targets").hasArg().withDescription(
			"Targets to execute").create('t');

	private final static Option runtimeOpt = OptionBuilder.withLongOpt(
			"runtime").withArgName("runtime").hasArg().withDescription(
			"Runtime to use, either a full path or relative to slc app conf dir: "
					+ "<conf dir>/runtime/<runtime>/.xml").create('r');

	private final static Options options;

	private final static String commandName = "slc";

	static {
		options = new Options();
		options.addOption(modeOpt);
		options.addOption(scriptOpt);
		options.addOption(targetsOpt);
		options.addOption(propertyOpt);
		options.addOption(runtimeOpt);
	}

	public static void main(String[] args) {
		Mode mode = null;
		Properties properties = new Properties();
		String script = null;
		String targets = null;
		String runtimeStr = null;

		try {

			CommandLineParser clParser = new GnuParser();
			CommandLine cl = clParser.parse(options, args);

			// Mode
			String modeStr = cl.getOptionValue(modeOpt.getOpt());
			try {
				mode = Mode.valueOf(modeStr);
			} catch (IllegalArgumentException e) {
				throw new SlcException("Unrecognized mode '" + modeStr + "'", e);
			}

			// Script
			if (mode.equals(Mode.single)) {
				if (!cl.hasOption(scriptOpt.getOpt()))
					throw new SlcException("Mode " + Mode.single
							+ " requires option '" + scriptOpt.getLongOpt()
							+ "'");
				script = cl.getOptionValue(scriptOpt.getOpt());

				// Targets
				if (cl.hasOption(targetsOpt.getOpt()))
					targets = cl.getOptionValue(targetsOpt.getOpt());
			}

			// Properties
			if (cl.hasOption(propertyOpt.getOpt())) {
				for (String property : cl.getOptionValues(propertyOpt.getOpt())) {
					addProperty(properties, property);
				}
			}

			// Runtime
			if (cl.hasOption(runtimeOpt.getOpt())) {
				runtimeStr = cl.getOptionValue(runtimeOpt.getOpt());
			} else {
				runtimeStr = "default";
			}

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

		// Initializes logging and log arguments
		initLogging(properties);
		if (log.isDebugEnabled()) {
			log.debug("Mode: " + mode);
			log.debug("Runtime: " + runtimeStr);
			log.debug("User properties: " + properties);
			if (script != null)
				log.debug("Script: " + script);
			if (targets != null)
				log.debug("Targets: " + targets);
		}

		// Execution
		if (mode.equals(Mode.single)) {
			Resource scriptRes;
			if (new File(script).exists()) {
				scriptRes = new FileSystemResource(script);
			} else {
				scriptRes = new DefaultResourceLoader(SlcMain.class
						.getClassLoader()).getResource(script);
			}

			DefaultSlcRuntime runtime = new DefaultSlcRuntime();
			runtime.executeScript(runtimeStr, scriptRes, targets, properties,
					null);
		}
	}

	public static void printUsage() {
		new HelpFormatter().printHelp(commandName, options, true);
	}

	private static String listModeValues() {
		StringBuffer buf = new StringBuffer("");
		for (Mode mode : Mode.values()) {
			buf.append(mode).append(", ");
		}
		String str = buf.toString();
		// unsafe, but there will be at least one value in the enum
		return str.substring(0, str.length() - 2);
	}

	private static void addProperty(Properties properties, String property) {
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

	private static void initLogging(Properties userProperties) {
		System.setProperty("log4j.defaultInitOverride", "true");

		// Add log4j user properties to System properties
		for (String key : userProperties.stringPropertyNames()) {
			if (key.startsWith("log4j.")) {
				System.setProperty(key, userProperties.getProperty(key));
			}
		}
		Log4jUtils.initLog4j(System.getProperty("log4j.configuration",
				"classpath:" + BOOTSTRAP_LOG4J_CONFIG));
		log = LogFactory.getLog(SlcMain.class);

	}

	private static void badExit() {
		printUsage();
		System.exit(1);
	}
}
