package org.argeo.slc.tool.swt;

import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.argeo.cms.cli.CommandsCli;
import org.argeo.cms.cli.DescribedCommand;
import org.argeo.minidesktop.MiniDesktopManager;

public class MiniDesktopCli extends CommandsCli {

	public MiniDesktopCli(String commandName) {
		super(commandName);
		addCommand("launch", new Launch());
	}

	@Override
	public String getDescription() {
		return "A minimalistic desktop manager based on Java and Eclipse SWT.";
	}

	public static void main(String[] args) {
		mainImpl(new MiniDesktopCli("minidesktop"), args);
	}

	static class Launch implements DescribedCommand<String> {
		@Override
		public Options getOptions() {
			Options options = new Options();
			options.addOption(Option.builder().longOpt("fullscreen")
					.desc("take control of the whole screen (default is to run in a window)").build());
			options.addOption(Option.builder().longOpt("stacking")
					.desc("open apps as tabs (default is to create new windows)").build());
			return options;
		}

		@Override
		public String apply(List<String> args) {
			CommandLine cl = toCommandLine(args);
			boolean fullscreen = cl.hasOption("fullscreen");
			boolean stacking = cl.hasOption("stacking");

			MiniDesktopManager desktopManager = new MiniDesktopManager(fullscreen, stacking);
			desktopManager.init();
			desktopManager.run();
			desktopManager.dispose();
			return null;
		}

		@Override
		public String getDescription() {
			return "Launch a minidesktop manager.";
		}

	}
}
