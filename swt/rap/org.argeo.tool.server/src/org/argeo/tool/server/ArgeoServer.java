package org.argeo.tool.server;

import org.argeo.cms.cli.ArgeoCli;
import org.argeo.tool.rap.cli.CmsRapCli;

/** Argeo command line tools. */
public class ArgeoServer extends ArgeoCli {
	public ArgeoServer(String commandName) {
		super(commandName);
		addCommandsCli(new CmsRapCli("cms"));
	}

	@Override
	public String getDescription() {
		return "Argeo server utilities";
	}

	public static void main(String[] args) {
		mainImpl(new ArgeoServer("argeo"), args);
	}

}
