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
import org.argeo.slc.core.SlcException;
import org.springframework.core.io.FileSystemResource;

public class SlcMain {
	public enum Mode {
		single, agent
	}

	public final static String MODE_SINGLE = "single";
	public final static String MODE_AGENT = "agent";

	private final static Option modeOpt = OptionBuilder.withLongOpt("mode")
			.withArgName("mode").hasArg().isRequired().withDescription(
					"SLC execution mode, one of: " + listModeValues()).create(
					'm');

	private final static Option propertyOpt = OptionBuilder.withLongOpt(
			"property").withArgName("prop1=val1,prop2=val2").hasArgs()
			.withValueSeparator(',').withDescription(
					"use value for given property").create('p');

	private final static Option scriptOpt = OptionBuilder.withLongOpt("script")
			.withArgName("script").hasArg().withType(File.class)
			.withDescription("SLC script to execute").create('s');

	private final static Options options;

	private final static String commandName = "slc";

	static {
		options = new Options();
		options.addOption(modeOpt);
		options.addOption(scriptOpt);
		options.addOption(propertyOpt);
	}

	public static void main(String[] args) {
		Mode mode = null;
		Properties properties = new Properties();
		File script = null;

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
			System.out.println("Mode: " + mode);

			// Script
			if (mode.equals(Mode.single)) {
				if (!cl.hasOption(scriptOpt.getOpt()))
					throw new SlcException("Mode " + Mode.single
							+ " requires option '" + scriptOpt.getLongOpt()
							+ "'");
				script = (File) cl.getOptionObject(scriptOpt.getOpt());
			}
			System.out.println("Script: " + script.getAbsolutePath());

			// Properties
			if (cl.hasOption(propertyOpt.getOpt())) {
				for (String property : cl.getOptionValues(propertyOpt.getOpt())) {
					addProperty(properties, property);
				}
			}
			System.out.print("Properties: " + properties);
		} catch (ParseException e) {
			System.err.println("Problem with command line arguments. "
					+ e.getMessage());
			printUsage();
		} catch (SlcException e) {
			System.err.println(e.getMessage());
			printUsage();
		}

		if (mode.equals(Mode.single)) {
			DefaultSlcRuntime runtime = new DefaultSlcRuntime();
			runtime.executeScript(new FileSystemResource(script), properties,
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
}
