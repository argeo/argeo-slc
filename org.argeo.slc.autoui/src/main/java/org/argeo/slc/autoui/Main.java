package org.argeo.slc.autoui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.cache.BundleCache;
import org.apache.felix.main.AutoActivator;
import org.netbeans.jemmy.ClassReference;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Main {
	private final Felix felix;
	private final BundleContext context;

	public Main(Felix felix) {
		this.felix = felix;
		context = felix.getBundleContext();
	}

	public Felix getFelix() {
		return felix;
	}

	public BundleContext getContext() {
		return context;
	}

	public static void main(String[] args) {
		try {
			// Start OSGi system
			Properties config = prepareConfig();
			Main main = startSystem(config);

			// Start UI (in main class loader)
			startUi(config);

			// Automate
			automateUi(main.getContext());
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	protected static Properties prepareConfig() throws Exception {
		final File cachedir = createTemporaryCacheDir();

		// Load config
		Properties config = new Properties();
		InputStream in = null;
		;
		try {
			in = Main.class
					.getResourceAsStream("/org/argeo/slc/autoui/felix.properties");
			config.load(in);
		} finally {
			if (in != null)
				in.close();
		}

		// Perform variable substitution for system properties.
		for (Enumeration e = config.propertyNames(); e.hasMoreElements();) {
			String name = (String) e.nextElement();
			config.setProperty(name, org.apache.felix.main.Main.substVars(
					config.getProperty(name), name, null, config));
		}

		config.put(BundleCache.CACHE_PROFILE_DIR_PROP, cachedir
				.getAbsolutePath());

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

	public static Main startSystem(Properties config) throws Exception {
		// Create list to hold custom framework activators.
		List list = new ArrayList();
		// Add activator to process auto-start/install properties.
		list.add(new AutoActivator(config));
		// Add our own activator.
		list.add(new AutoUiActivator());

		// Now create an instance of the framework.
		Felix felix = new Felix(config, list);
		felix.start();

		return new Main(felix);
	}

	public static void startUi(Properties config) throws Exception {
		String className = config.getProperty("argeo.scl.autoui.uiclass");
		String[] uiArgs = readArgumentsFromLine(config.getProperty(
				"argeo.slc.autoui.uiargs", ""));
		ClassReference classReference = new ClassReference(className);
		classReference.startApplication(uiArgs);
	}

	protected static void automateUi(BundleContext context) throws Exception {
		// Retrieve service and execute it
		ServiceReference ref = context
				.getServiceReference("org.argeo.slc.autoui.AutoUiApplication");
		Object service = context.getService(ref);
		AutoUiActivator.stdOut("service.class=" + service.getClass());
		AutoUiApplication app = (AutoUiApplication) service;
		app.execute(null);
	}

	/* UTILITIES */

	/**
	 * Transform a line into an array of arguments, taking "" as single
	 * arguments. (nested \" are not supported)
	 */
	private static String[] readArgumentsFromLine(String lineOrig) {

		String line = lineOrig.trim();// make sure there are no trailing
		// spaces
		System.out.println("line=" + line);
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
			System.out.println("res[i]=" + res[i]);
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
