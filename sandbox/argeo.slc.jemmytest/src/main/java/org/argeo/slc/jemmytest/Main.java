package org.argeo.slc.jemmytest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.framework.util.StringMap;
import org.apache.felix.main.AutoActivator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// Create a temporary bundle cache directory and
			// make sure to clean it up on exit.
			final File cachedir = File.createTempFile(
					"felix.example.servicebased", null);
			cachedir.delete();
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					deleteFileOrDir(cachedir);
				}
			});

			String mavenBase = "file:/home/mbaudier/.m2/repository/";

			Map configMap = new StringMap(false);
			configMap
					.put(
							Constants.FRAMEWORK_SYSTEMPACKAGES,
							"org.osgi.framework; version=1.4.0,"
									+ "org.osgi.service.packageadmin; version=1.2.0,"
									+ "org.osgi.service.startlevel; version=1.1.0,"
									+ "org.osgi.service.url; version=1.0.0,"
									+ "org.osgi.util.tracker; version=1.3.3,"
									/*+ "org.apache.felix.example.servicebased.host.service; version=1.0.0,"*/
									+ "javax.swing");
			configMap
					.put(
							AutoActivator.AUTO_START_PROP + ".1",
							mavenBase
									+ "org/apache/felix/org.apache.felix.shell/1.0.2/org.apache.felix.shell-1.0.2.jar "
									+ mavenBase
									+ "org/apache/felix/org.apache.felix.shell.tui/1.0.2/org.apache.felix.shell.tui-1.0.2.jar "
									+ mavenBase
									+ "org/argeo/dep/jemmy/org.argeo.dep.jemmy.nb61/0.2.0/org.argeo.dep.jemmy.nb61-0.2.0.jar "
									+ mavenBase
									+ "org/argeo/slc/org.argeo.slc.autoui/0.10.3-SNAPSHOT/org.argeo.slc.autoui-0.10.3-SNAPSHOT.jar");
			configMap.put(FelixConstants.LOG_LEVEL_PROP, "1");
			configMap.put(BundleCache.CACHE_PROFILE_DIR_PROP, cachedir
					.getAbsolutePath());

			// Create list to hold custom framework activators.
			List list = new ArrayList();
			// Add activator to process auto-start/install properties.
			list.add(new AutoActivator(configMap));
			// Add our own activator.
			list.add(new JemmyTestActivator());

			BundleContext context = null;
			try {
				// Now create an instance of the framework.
				Felix felix = new Felix(configMap, list);
				felix.start();

				context = felix.getBundleContext();

				// Bundle jemmyTestBundle = context
				// .installBundle(mavenBase
				// +
				// "org/argeo/slc/sandbox/org.argeo.slc.sandbox.jemmytest/0.1.1-SNAPSHOT/org.argeo.slc.sandbox.jemmytest-0.1.1-SNAPSHOT.jar");
				// jemmyTestBundle.start();

			} catch (Exception ex) {
				System.err.println("Could not create framework: " + ex);
				ex.printStackTrace();
				System.exit(-1);
			}

//			ServiceReference ref = context
//			.getServiceReference("org.argeo.slc.autoui.AutoUiApplication");
			ServiceReference ref = context
			.getServiceReference("java.lang.Runnable");
			Object service = context.getService(ref);
			JemmyTestActivator.stdOut("service=" + service.getClass());
			Runnable app = (Runnable) service;
			app.run();
			// app.execute(null);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		// Felix felix;
		// // JemmyTestActivator activator;
		//
		// // Create a case-insensitive configuration property map.
		// Map configMap = new StringMap(false);
		// // Configure the Felix instance to be embedded.
		// configMap.put(FelixConstants.EMBEDDED_EXECUTION_PROP, "true");
		// // Add core OSGi packages to be exported from the class path
		// // via the system bundle.
		// configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES,
		// "org.osgi.framework; version=1.3.0,"
		// + "org.osgi.service.packageadmin; version=1.2.0,"
		// + "org.osgi.service.startlevel; version=1.0.0,"
		// + "org.osgi.service.url; version=1.0.0");
		// // Explicitly specify the directory to use for caching bundles.
		// configMap.put(BundleCache.CACHE_PROFILE_DIR_PROP, "target/cache");
		//
		// try {
		// // Create host activator;
		// // activator = new JemmyTestActivator();
		// List list = new ArrayList();
		// // list.add(activator);
		// list.add(new Activator());
		// list.add(new org.apache.felix.shell.impl.Activator());
		// list.add(new org.apache.felix.bundlerepository.Activator());
		//
		// // Now create an instance of the framework with
		// // our configuration properties and activator.
		// felix = new Felix(configMap, list);
		//
		// // Now start Felix instance.
		// felix.start();
		//
		// Bundle jemmyBundle = felix
		// .getBundleContext()
		// .installBundle(
		// "file:/home/mbaudier/.m2/repository/org/argeo/dep/jemmy/org.argeo.dep.jemmy.nb61/0.2.0/org.argeo.dep.jemmy.nb61-0.2.0.jar");
		// jemmyBundle.start();
		// //
		// // Bundle autoUiBundle = felix
		// // .getBundleContext()
		// // .installBundle(
		// //
		// "reference:file:/home/mbaudier/dev/src/slc/org.argeo.slc.autoui/");
		// // autoUiBundle.start();
		//
		// Bundle[] bundles = felix.getBundleContext().getBundles();
		// for (int i = 0; i < bundles.length; i++) {
		// Bundle bundle = bundles[i];
		// System.out.println("" + bundle.getBundleId() + "\t"
		// + bundle.getSymbolicName() + "\t" + bundle.getState()
		// + "\t" + bundle.getLocation());
		// }
		//
		// // felix.stop();
		// } catch (Exception ex) {
		// System.err.println("Could not create framework: " + ex);
		// ex.printStackTrace();
		// }
		//
	}

	/**
	 * Utility method used to delete the profile directory when run as a
	 * stand-alone application.
	 * 
	 * @param file
	 *            The file to recursively delete.
	 */
	private static void deleteFileOrDir(File file) {
		if (file.isDirectory()) {
			File[] childs = file.listFiles();
			for (int i = 0; i < childs.length; i++) {
				deleteFileOrDir(childs[i]);
			}
		}
		file.delete();
	}

}
