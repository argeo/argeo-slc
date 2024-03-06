package org.argeo.slc.init.osgi;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.argeo.api.cms.CmsLog;
import org.argeo.api.init.InitConstants;
import org.argeo.api.init.RuntimeManager;
import org.argeo.cms.CmsDeployProperty;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class SlcInitActivator implements BundleActivator {
	private final static CmsLog log = CmsLog.getLog(SlcInitActivator.class);

	private ServiceTracker<RuntimeManager, RuntimeManager> runtimeManagerSt;

	@Override
	public void start(BundleContext context) throws Exception {
		Path userHome = Paths.get(System.getProperty("user.home"));
//		OsgiCmsDeployment.main(new String[0]);
		runtimeManagerSt = new ServiceTracker<>(context, RuntimeManager.class, null) {

			@Override
			public RuntimeManager addingService(ServiceReference<RuntimeManager> reference) {
				RuntimeManager runtimeManager = super.addingService(reference);
				log.debug("Found runtime manager " + runtimeManager);
				new Thread() {
					public void run() {
//						try {
//							Thread.sleep(5000);
//						} catch (InterruptedException e) {
//							return;
//						}

						runtimeManager.startRuntime("cms/test1", (config) -> {
							config.put("osgi.console", "host1:2023");
							config.put(CmsDeployProperty.SSHD_PORT.getProperty(), "2222");
							config.put(CmsDeployProperty.HTTP_PORT.getProperty(), "7070");
							config.put(CmsDeployProperty.HOST.getProperty(), "host1");
//							for (String key : config.keySet()) {
//								System.out.println(key + "=" + config.get(key));
////								log.debug(() -> key + "=" + config.get(key));
//							}
						});
						runtimeManager.startRuntime("native/test2", (config) -> {
							config.put("osgi.console", "host2:2023");
							config.put(CmsDeployProperty.SSHD_PORT.getProperty(), "2222");
							// config.put(CmsDeployProperty.HTTP_PORT.getProperty(), "7070");
							config.put(CmsDeployProperty.HOST.getProperty(), "host2");
							config.put("argeo.osgi.start.6", "org.argeo.swt.minidesktop");
//							config.put("argeo.directory", "ipa:///");
//							Path instanceData = userHome
//									.resolve("dev/git/unstable/argeo-slc/sdk/exec/cms-deployment/data");
//							config.put(InitConstants.PROP_OSGI_INSTANCE_AREA, instanceData.toUri().toString());
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
