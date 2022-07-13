package org.argeo.tool.desktop;

import org.argeo.cms.cli.ArgeoCli;
import org.argeo.cms.swt.rcp.cli.CmsCli;

/** Argeo command line tools. */
public class ArgeoDesktop extends ArgeoCli {
	public ArgeoDesktop(String commandName) {
		super(commandName);
		addCommandsCli(new CmsCli("cms"));
		addCommandsCli(new MiniDesktopCli("minidesktop"));
	}

	@Override
	public String getDescription() {
		return "Argeo desktop utilities";
	}

	public static void main(String[] args) {
		mainImpl(new ArgeoDesktop("argeo-desktop"), args);
	}

}
