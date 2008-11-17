package org.argeo.slc.detached.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class Main {
	private final static String DEV_BUNDLE_PREFIX = "slc.osgi.devbundle.";

	public static void main(String[] args) {
		info("Argeo SLC Detached launcher starting...");
		try {
			// Load properties
			String propertyPath = "slc-detached.properties";
			Properties config = prepareConfig(propertyPath);

			// Start app (in main class loader)
			startApp(config);

			// Start OSGi framework
			startEquinox(config);
			info("Argeo SLC Detached launcher started.");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	protected static Properties prepareConfig(String propertyFilePath)
			throws Exception {
		// Format slc.home
		String slcHome = System.getProperty("slc.home");
		if (slcHome != null) {
			slcHome = new File(slcHome).getCanonicalPath();
			System.setProperty("slc.home", slcHome);
		}

		// Load config
		Properties config = new Properties();
		InputStream in = null;
		try {
			File file = new File(propertyFilePath);
			if (file.exists()) {
				in = new FileInputStream(propertyFilePath);
				config.load(in);
			}
		} finally {
			if (in != null)
				in.close();
		}

		// System properties have priority.
		config.putAll(System.getProperties());
		return config;
	}

	public static void startEquinox(Properties config) throws Exception {
		System.out.println("java.class.path="
				+ System.getProperty("java.class.path"));

		File baseDir = new File(System.getProperty("user.dir"))
				.getCanonicalFile();
		String equinoxConfigurationPath = baseDir.getPath() + File.separator
				+ "slc-detached" + File.separator + "equinoxConfiguration";
		String[] equinoxArgs = { "-console", "-noExit", "-clean",
				"-configuration", equinoxConfigurationPath };

		BundleContext context = EclipseStarter.startup(equinoxArgs, null);

		// Load from class path (dev environment, maven)
		StringTokenizer st = new StringTokenizer(System
				.getProperty("java.class.path"), File.pathSeparator);
		while (st.hasMoreTokens()) {
			try {
				String path = st.nextToken();
				String url = "reference:file:"
						+ new File(path).getCanonicalPath();
				context.installBundle(url);
				info("Installed from classpath " + url);
			} catch (Exception e) {
				bundleInstallWarn(e.getMessage());
			}
		}

		// Load from dev bundles
		Map devBundleUrls = getDevBundleUrls(config);
		Iterator devBundles = devBundleUrls.keySet().iterator();
		while (devBundles.hasNext()) {
			try {
				String bundleName = (String) devBundles.next();
				String url = (String) devBundleUrls.get(bundleName);
				context.installBundle(url);
				info("Installed as dev bundle " + url);
			} catch (Exception e) {
				bundleInstallWarn(e.getMessage());
			}
		}

		// Load from distribution
		String slcHome = config.getProperty("slc.home");
		if (slcHome != null) {
			File libDir = new File(slcHome + File.separator + "lib");
			File[] bundleFiles = libDir.listFiles();
			for (int i = 0; i < bundleFiles.length; i++) {
				try {
					String url = "reference:file:"
							+ bundleFiles[i].getCanonicalPath();
					context.installBundle(url);
					info("INFO: Installed from SLC home " + url);
				} catch (Exception e) {
					bundleInstallWarn(e.getMessage());
				}

			}
		}

		// Start bundles
		String bundleStart = config.getProperty("slc.osgi.start",
				"org.springframework.osgi.extender,org.argeo.slc.detached");
		StringTokenizer stBundleStart = new StringTokenizer(bundleStart, ",");
		while (stBundleStart.hasMoreTokens()) {
			String bundleSymbolicName = stBundleStart.nextToken();
			startBundle(context, bundleSymbolicName);
		}
	}

	private static Map getDevBundleUrls(Properties config) {
		Map bundles = new Hashtable();
		Iterator keys = config.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			if (key.startsWith(DEV_BUNDLE_PREFIX)) {
				String bundle = key.substring(DEV_BUNDLE_PREFIX.length());
				String path = config.getProperty(key);
				bundles.put(bundle, path);
			}
		}
		return bundles;
	}

	private static void startBundle(BundleContext bundleContext,
			String symbolicName) throws BundleException {
		Bundle bundle = findBundleBySymbolicName(bundleContext, symbolicName);
		if (bundle != null)
			bundle.start();
		else
			throw new RuntimeException("Bundle " + symbolicName + " not found");
		info("Started " + symbolicName);
	}

	/** WARNING: return the first one found! */
	private static Bundle findBundleBySymbolicName(BundleContext bundleContext,
			String symbolicName) {
		Bundle[] bundles = bundleContext.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			Bundle bundle = bundles[i];
			if (bundle.getSymbolicName().equals(symbolicName)) {
				return bundle;
			}
		}
		return null;
	}

	public static void startApp(Properties config) throws Exception {
		String className = config.getProperty("slc.detached.appclass");
		String[] uiArgs = readArgumentsFromLine(config.getProperty(
				"slc.detached.appargs", ""));

		if (className == null)
			throw new Exception(
					"A main class has to be defined with the system property slc.detached.appclass");

		// Launch main method using reflection
		Class clss = Class.forName(className);
		Class[] mainArgsClasses = new Class[] { uiArgs.getClass() };
		Object[] mainArgs = { uiArgs };
		Method mainMethod = clss.getMethod("main", mainArgsClasses);
		mainMethod.invoke(null, mainArgs);
	}

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

	private static void info(Object obj) {
		System.out.println("[INFO] " + obj);
	}

	private static void bundleInstallWarn(Object obj) {
		System.err.println("[WARN] " + obj);
	}
}
