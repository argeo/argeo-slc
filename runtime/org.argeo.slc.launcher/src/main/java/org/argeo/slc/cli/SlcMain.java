package org.argeo.slc.cli;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.logging.Log4jUtils;
import org.argeo.slc.runtime.SlcExecutionContext;
import org.argeo.slc.runtime.SlcRuntime;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SlcMain {
	public enum Mode {
		single, agent
	}

	private static Log log = null;

	private final static String BOOTSTRAP_LOG4J_CONFIG = "org/argeo/slc/cli/bootstrapLog4j.properties";
	private final static String DEFAULT_AGENT_CONTEXT = "classpath:org/argeo/slc/cli/spring-agent-default.xml";

	private final static Option modeOpt = OptionBuilder.withLongOpt("mode")
			.withArgName("mode").hasArg().withDescription(
					"SLC execution mode, one of: " + listModeValues()).create(
					'm');

	private final static Option propertyOpt = OptionBuilder.withLongOpt(
			"property").withArgName("prop1=val1,prop2=val2").hasArgs()
			.withValueSeparator(',').withDescription(
					"use value for given property").create('p');

	private final static Option propertiesOpt = OptionBuilder.withLongOpt(
			"properties").withArgName("properties file").hasArgs()
			.withValueSeparator(',').withDescription(
					"load properties from file (-p has priority)").create('P');

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
		options.addOption(propertiesOpt);
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
			if (modeStr == null) {
				mode = Mode.single;
			} else {
				try {
					mode = Mode.valueOf(modeStr);
				} catch (IllegalArgumentException e) {
					throw new SlcException("Unrecognized mode '" + modeStr
							+ "'", e);
				}
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
			if (cl.hasOption(propertiesOpt.getOpt())) {
				for (String propertyFile : cl.getOptionValues(propertiesOpt
						.getOpt())) {
					loadPropertyFile(properties, propertyFile);
				}
			}
			if (cl.hasOption(propertyOpt.getOpt())) {
				for (String property : cl.getOptionValues(propertyOpt.getOpt())) {
					addProperty(properties, property);
				}
			}

			// Runtime
			if (cl.hasOption(runtimeOpt.getOpt())) {
				runtimeStr = cl.getOptionValue(runtimeOpt.getOpt());
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
			if (runtimeStr != null)
				log.debug("Runtime: " + runtimeStr);
			log.debug("User properties: " + properties);
			if (script != null)
				log.debug("Script: " + script);
			if (targets != null)
				log.debug("Targets: " + targets);
		}

		// Execution
		if (mode.equals(Mode.single)) {
			try {
				// DefaultSlcRuntime runtime = new DefaultSlcRuntime();
				// FIXME: inject this more cleanly
				ClassLoader cl = Thread.currentThread().getContextClassLoader();
				Class clss = cl.loadClass("org.argeo.slc.ant.AntSlcRuntime");
				SlcRuntime<? extends SlcExecutionContext> runtime = (SlcRuntime<? extends SlcExecutionContext>) clss
						.newInstance();
				runtime.executeScript(runtimeStr, script, targets, properties,
						null, null);
				// System.exit(0);
			} catch (Exception e) {
				log.error("SLC client terminated with an error: ", e);
				System.exit(1);
			}
		}
		// Agent
		else if (mode.equals(Mode.agent)) {
			final ConfigurableApplicationContext applicationContext;
			if (runtimeStr == null) {
				applicationContext = new ClassPathXmlApplicationContext(
						DEFAULT_AGENT_CONTEXT);
			} else {
				applicationContext = new FileSystemXmlApplicationContext(
						runtimeStr);
			}
			applicationContext.registerShutdownHook();
			applicationContext.start();
			log.info("SLC Agent context started.");
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

	private static void initLogging(Properties userProperties) {
		System.setProperty("log4j.defaultInitOverride", "true");

		// Add log4j user properties to System properties
		for (Object obj : userProperties.keySet()) {
			String key = obj.toString();
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
