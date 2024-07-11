package org.argeo.slc.internal.runtime.osgi;

import org.argeo.api.init.RuntimeManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class SlcInitActivator implements BundleActivator {
//	private final static CmsLog log = CmsLog.getLog(SlcInitActivator.class);

	private ServiceTracker<RuntimeManager, RuntimeManager> runtimeManagerSt;

	@Override
	public void start(BundleContext context) throws Exception {
//		Path userHome = Paths.get(System.getProperty("user.home"));

//		{
//			EquinoxFactory equinoxFactory = new EquinoxFactory();
//			Map<String, String> config = new HashMap<>();
//			config.put("osgi.console", "host1:2023");
//			config.put("osgi.frameworkParentClassloader", "app");
//			config.put("osgi.parentClassLoader", "app");
//			RuntimeManager.loadConfig(Paths.get("/usr/local/etc/argeo/user/cms/test3"), config);
//			Framework framework = equinoxFactory.newFramework(config);
//			framework.start();
//			OsgiBoot osgiBoot = new OsgiBoot(framework.getBundleContext());
//			osgiBoot.bootstrap(config);
//		}

		// OsgiCmsDeployment.test();

		runtimeManagerSt = new ServiceTracker<>(context, RuntimeManager.class, null) {

			@Override
			public RuntimeManager addingService(ServiceReference<RuntimeManager> reference) {
				RuntimeManager runtimeManager = super.addingService(reference);
				new Thread() {
					public void run() {
//						try {
//							Thread.sleep(5000);
//						} catch (InterruptedException e) {
//							return;
//						}

						runtimeManager.startRuntime("rcp/test1", (config) -> {
							config.put("osgi.console", "host1:2023");
							config.put("argeo.osgi.start.6", "org.argeo.swt.minidesktop");
//							config.put(CmsDeployProperty.SSHD_PORT.getProperty(), "2222");
//							config.put(CmsDeployProperty.HTTP_PORT.getProperty(), "7070");
//							config.put(CmsDeployProperty.HOST.getProperty(), "host1");
						});

						runtimeManager.startRuntime("rap/test2", (config) -> {
							config.put("osgi.console", "host2:2023");
							config.put("argeo.sshd.port", "2222");
							config.put("argeo.http.port", "7070");
							config.put("argeo.host", "host2");
							config.put("argeo.osgi.start.6",
									"argeo.osgi.start.6=org.example.suite.theme,org.example.suite.core,org.example.suite.ui,org.example.suite.ui.rap");
////							config.put("argeo.directory", "ipa:///");
						});
					}
				}.start();

				return runtimeManager;
			}

		};
		runtimeManagerSt.open(false);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

}
