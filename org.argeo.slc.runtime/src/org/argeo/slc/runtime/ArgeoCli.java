package org.argeo.slc.runtime;

import org.apache.commons.cli.Option;
import org.argeo.cms.cli.CommandsCli;
import org.argeo.slc.cli.posix.PosixCommands;

/** Argeo command line tools. */
public class ArgeoCli extends CommandsCli {

	public ArgeoCli(String commandName) {
		super(commandName);
		// Common options
		options.addOption(Option.builder("v").hasArg().argName("verbose").desc("verbosity").build());
		options.addOption(
				Option.builder("D").hasArgs().argName("property=value").desc("use value for given property").build());

		addCommandsCli(new PosixCommands("posix"));
//		addCommandsCli(new FsCommands("fs"));
//		addCommandsCli(new JcrCommands("jcr"));
	}

	@Override
	public String getDescription() {
		return "Argeo command line utilities";
	}

	public static void main(String[] args) {
		mainImpl(new ArgeoCli("argeo"), args);
	}

}
