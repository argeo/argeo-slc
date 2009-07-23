package org.argeo.slc.cli;

import java.io.FileInputStream;
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
import org.argeo.slc.SlcException;
import org.argeo.slc.server.client.impl.SlcServerHttpClientImpl;

@SuppressWarnings("static-access")
public class SlcMain {
	public enum Type {
		standalone, agent, server
	}

	private static Boolean debug = true;

	// private final static String BOOTSTRAP_LOG4J_CONFIG =
	// "org/argeo/slc/cli/bootstrapLog4j.properties";
	// private final static String DEFAULT_AGENT_CONTEXT =
	// "classpath:org/argeo/slc/cli/spring-agent-default.xml";

	private final static Option typeOpt = OptionBuilder.withLongOpt("mode")
			.withArgName("mode").hasArg().withDescription(
					"Execution type, one of: " + listTypeValues()).create('t');

	private final static Option propertyOpt = OptionBuilder.withLongOpt(
			"property").withArgName("prop1=val1,prop2=val2").hasArgs()
			.withValueSeparator(',').withDescription(
					"use value for given property").create('p');

	private final static Option propertiesOpt = OptionBuilder.withLongOpt(
			"properties").withArgName("properties file").hasArgs()
			.withValueSeparator(',').withDescription(
					"load properties from file (-p has priority)").create('P');

	private final static Option moduleOpt = OptionBuilder.withLongOpt("module")
			.withArgName("module").hasArg().withDescription("Execution module")
			.create('m');

	private final static Option flowsOpt = OptionBuilder.withLongOpt("flows")
			.withArgName("flows").hasArg().withDescription("Flows to execute")
			.create('f');

	private final static Option runtimeOpt = OptionBuilder.withLongOpt(
			"runtime").withArgName("runtime").hasArg().withDescription(
			"Runtime URL").create('r');

	private final static Options options;

	private final static String commandName = "slc";

	static {
		options = new Options();
		options.addOption(typeOpt);
		options.addOption(moduleOpt);
		options.addOption(flowsOpt);
		options.addOption(propertyOpt);
		options.addOption(propertiesOpt);
		options.addOption(runtimeOpt);
	}

	public static void main(String[] args) {
		Type type = null;
		Properties properties = new Properties();
		String module = null;
		String flows = null;
		String urlStr = null;

		try {

			CommandLineParser clParser = new GnuParser();
			CommandLine cl = clParser.parse(options, args);

			// Mode
			String typeStr = cl.getOptionValue(typeOpt.getOpt());
			if (typeStr == null) {
				type = Type.standalone;
			} else {
				try {
					type = Type.valueOf(typeStr);
				} catch (IllegalArgumentException e) {
					throw new SlcException("Unrecognized mode '" + typeStr
							+ "'", e);
				}
			}

			// Script
			if (type.equals(Type.standalone)) {
				if (!cl.hasOption(moduleOpt.getOpt()))
					throw new SlcException("Type " + Type.standalone
							+ " requires option '" + moduleOpt.getLongOpt()
							+ "'");
				module = cl.getOptionValue(moduleOpt.getOpt());

				// Targets
				if (cl.hasOption(flowsOpt.getOpt()))
					flows = cl.getOptionValue(flowsOpt.getOpt());
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
				urlStr = cl.getOptionValue(runtimeOpt.getOpt());
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

		if (debug) {
			debug("Mode: " + type);
			if (urlStr != null)
				debug("Runtime: " + urlStr);
			debug("User properties: " + properties);
			if (module != null)
				debug("Module: " + module);
			if (flows != null)
				debug("Flows: " + flows);
		}

		// Standalone
		if (type.equals(Type.standalone)) {
		}
		// Agent
		else if (type.equals(Type.agent)) {
		}
		// Server
		else if (type.equals(Type.server)) {
			SlcServerHttpClientImpl slcServerHttpClient = new SlcServerHttpClientImpl();
			slcServerHttpClient.setBaseUrl(urlStr);
		}
	}

	public static void printUsage() {
		new HelpFormatter().printHelp(commandName, options, true);
	}

	private static String listTypeValues() {
		StringBuffer buf = new StringBuffer("");
		for (Type mode : Type.values()) {
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
