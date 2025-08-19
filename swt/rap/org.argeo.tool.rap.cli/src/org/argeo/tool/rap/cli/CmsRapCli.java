package org.argeo.tool.rap.cli;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.argeo.api.acr.CrAttributeType;
import org.argeo.api.acr.spi.ProvidedRepository;
import org.argeo.api.cli.CLine;
import org.argeo.api.cli.CommandsCli;
import org.argeo.api.cli.DescribedCommand;
import org.argeo.api.cli.ValuedOpt;
import org.argeo.api.cms.CmsApp;
import org.argeo.api.cms.CmsContext;
import org.argeo.api.cms.CmsState;
import org.argeo.api.register.Component;
import org.argeo.api.register.ComponentRegister;
import org.argeo.cms.jetty.ee.CmsEeJettyServer;
import org.argeo.cms.runtime.StaticCms;
import org.argeo.cms.swt.app.CmsUserApp;
import org.argeo.cms.web.CmsWebApp;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;

public class CmsRapCli extends CommandsCli {

	public CmsRapCli(String commandName) {
		super(commandName);
		addCommand("user", new Launch());
	}

//	@Override
//	public String getDescription() {
//		return "Argeo CMS utilities.";
//	}

	public static void main(String[] args) {
		mainImpl(new CmsRapCli("web"), args);
	}

	static class Launch extends DescribedCommand<String> {
		enum Opt implements ValuedOpt {
			data, //
			ui, //
			;

			@Override
			public CrAttributeType type() {
				return switch (this) {
				case data -> CrAttributeType.ANY_URI;
				default -> CrAttributeType.BOOLEAN;
				};
			}

		}

		@Override
		protected Class<? extends Enum<?>> getOptClass() {
			return Opt.class;
		}

//		@Override
//		public Options getOptions() {
//			Options options = new Options();
//			dataOption = Option.builder().longOpt("data").hasArg().required()
//					.desc("path to the writable data area (mandatory)").build();
//			uiOption = Option.builder().longOpt("ui").desc("open a user interface").build();
//			options.addOption(dataOption);
//			options.addOption(uiOption);
//			return options;
//		}

		@Override
		public String execute(CLine cLine) {
			String dataPath = cLine.get(Opt.data, String.class).orElseThrow();
			boolean ui = cLine.flag(Opt.ui);

			Path instancePath = Paths.get(dataPath);
			System.setProperty("osgi.instance.area", instancePath.toUri().toString());
			System.setProperty("argeo.http.port", "0");

			StaticCms staticCms = new StaticCms() {
				@Override
				protected void addComponents(ComponentRegister register) {
					if (ui) {
						CmsUserApp cmsApp = new CmsUserApp();
						Component<CmsUserApp> cmsAppC = new Component.Builder<>(cmsApp) //
								.addType(CmsApp.class) //
								.addType(CmsUserApp.class) //
								.addDependency(register.getSingleton(CmsContext.class), cmsApp::setCmsContext, null) //
								.addDependency(register.getSingleton(ProvidedRepository.class),
										cmsApp::setContentRepository, null) //
								.build(register);

						CmsWebApp cmsWebApp = new CmsWebApp();
						Component<CmsWebApp> cmsWebAppC = new Component.Builder<>(cmsWebApp) //
								.addType(ApplicationConfiguration.class) //
								.addType(CmsWebApp.class) //
								.addDependency(cmsAppC.getType(CmsApp.class), cmsWebApp::setCmsApp, null) //
								.build(register);

						RapJettyServer rwtRunner = new RapJettyServer();
						Component<RapJettyServer> rwtRunnerC = new Component.Builder<>(rwtRunner) //
								.addActivation(rwtRunner::start) //
								.addDeactivation(rwtRunner::stop) //
								.addType(CmsEeJettyServer.class) //
								.addDependency(register.getSingleton(CmsState.class), rwtRunner::setCmsState, null) //
								.addDependency(cmsWebAppC.getType(CmsWebApp.class), rwtRunner::setCmsWebApp, null) //
								.build(register);
					}
				}

			};
			Runtime.getRuntime().addShutdownHook(new Thread(() -> staticCms.stop(), "Static CMS Shutdown"));
			staticCms.start();

			long jvmUptime = ManagementFactory.getRuntimeMXBean().getUptime();
			System.out.println("Static CMS available in " + jvmUptime + " ms.");

			if (ui) {
				try {
					// open browser in app mode
					Thread.sleep(2000);// wait for RWT to be ready
					String browserCommand = "google-chrome --app=http://localhost:"
							+ staticCms.getComponentRegister().getObject(CmsEeJettyServer.class).getHttpPort()
							+ "/data";
					Runtime.getRuntime().exec(browserCommand);
				} catch (InterruptedException | IOException e) {
					e.printStackTrace();
				}
			}

			staticCms.waitForStop();

			return null;
		}

//		@Override
//		public String getDescription() {
//			return "Launch a static CMS.";
//		}

	}
}
