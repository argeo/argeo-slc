package org.argeo.slc.detached.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.main.AutoActivator;

public class Main {
	private final static Log log = LogFactory.getLog(Main.class);

	public static void main(String[] args) {
		log.info("Argeo SLC Detached launcher starting...");
		try {
			// Load properties
			String propertyPath = "slc-detached.properties";
			Properties config = prepareConfig(propertyPath);

			// Create cache dir
			if (!config.containsKey(BundleCache.CACHE_PROFILE_DIR_PROP)) {
				final File cachedir = createTemporaryCacheDir();
				config.put(BundleCache.CACHE_PROFILE_DIR_PROP, cachedir
						.getAbsolutePath());
			}

			// Start app (in main class loader)
			startApp(config);
			// Thread.sleep(10000);

			// Start OSGi system
			Felix felix = startSystem(config);

			log.info("Argeo SLC Detached system started (Felix " + felix + ")");

			// felix.stop();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	protected static Properties prepareConfig(String propertyFilePath)
			throws Exception {
		// Load config
		Properties config = new Properties();
		InputStream in = null;
		;
		try {
			in = Main.class
					.getResourceAsStream("/org/argeo/slc/detached/launcher/felix.properties");
			config.load(in);
		} finally {
			if (in != null)
				in.close();
		}

		try {
			in = new FileInputStream(propertyFilePath);
			config.load(in);
		} finally {
			if (in != null)
				in.close();
		}

		// System properties have priority.
		config.putAll(System.getProperties());

		// Perform variable substitution for system properties.
		for (Enumeration e = config.propertyNames(); e.hasMoreElements();) {
			String name = (String) e.nextElement();
			config.setProperty(name, org.apache.felix.main.Main.substVars(
					config.getProperty(name), name, null, config));
			if (log.isTraceEnabled())
				log.trace(name + "=" + config.getProperty(name));
		}

		return config;
	}

	protected static File createTemporaryCacheDir() throws IOException {
		// Create a temporary bundle cache directory and
		// make sure to clean it up on exit.
		final File cachedir = File.createTempFile("argeo.slc.autoui", null);
		cachedir.delete();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				deleteFileOrDir(cachedir);
			}
		});
		return cachedir;
	}

	public static Felix startSystem(Properties config) throws Exception {
		// Create list to hold custom framework activators.
		List list = new ArrayList();
		// Add activator to process auto-start/install properties.
		list.add(new AutoActivator(config));
		// Add our own activator.
		// list.add(new AutoUiActivator());

		// Now create an instance of the framework.
		Felix felix = new Felix(config, list);
		felix.start();

		return felix;
	}

	public static void startApp(Properties config) throws Exception {
		String className = config.getProperty("argeo.scl.detached.appclass");
		String[] uiArgs = readArgumentsFromLine(config.getProperty(
				"argeo.slc.detached.appargs", ""));

		// Launch main method using reflection
		Class clss = Class.forName(className);
		Class[] mainArgsClasses = new Class[] { uiArgs.getClass() };
		Object[] mainArgs = { uiArgs };
		Method mainMethod = clss.getMethod("main", mainArgsClasses);
		mainMethod.invoke(null, mainArgs);
	}

	// protected static void automateUi(BundleContext bundleContext)
	// throws Exception {
	// // Retrieve service and execute it
	// ServiceReference ref = bundleContext
	// .getServiceReference(DetachedExecutionServer.class.getName());
	// Object service = bundleContext.getService(ref);
	//
	// log.debug("service.class=" + service.getClass());
	// DetachedExecutionServer app = (DetachedExecutionServer) service;
	// DetachedStepRequest request = new DetachedStepRequest();
	// request.setStepRef("jemmyTest");
	// app.executeStep(request);
	// }

	/* UTILITIES */

	/**
	 * Transform a line into an array of arguments, taking "" as single
	 * arguments. (nested \" are not supported)
	 */
	private static String[] readArgumentsFromLine(String lineOrig) {

		String line = lineOrig.trim();// remove trailing spaces
		// System.out.println("line=" + line);
		List args = new Vector();
		StringBuffer curr = new StringBuffer("");
		boolean inQuote = false;
		char[] arr = line.toCharArray();
		for (int i = 0; i < arr.length; i++) {
			char c = arr[i];
			switch (c) {
			case '\"':
				inQuote = !inQuote;
				break;
			case ' ':
				if (!inQuote) {// otherwise, no break: goes to default
					if (curr.length() > 0) {
						args.add(curr.toString());
						curr = new StringBuffer("");
					}
					break;
				}
			default:
				curr.append(c);
				break;
			}
		}

		// Add last arg
		if (curr.length() > 0) {
			args.add(curr.toString());
			curr = null;
		}

		String[] res = new String[args.size()];
		for (int i = 0; i < args.size(); i++) {
			res[i] = args.get(i).toString();
			// System.out.println("res[i]=" + res[i]);
		}
		return res;
	}

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
