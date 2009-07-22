package org.argeo.slc.osgiboot;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.osgi.framework.BundleContext;

public class Launcher {

	public static void main(String[] args) {
		startMainClass();

		BundleContext bundleContext = null;
		try {
			bundleContext = EclipseStarter.startup(args, null);
		} catch (Exception e) {
			throw new RuntimeException("Cannot start Equinox.", e);
		}

		OsgiBoot osgiBoot = new OsgiBoot(bundleContext);
		osgiBoot.bootstrap();
	}
//
//	protected static void startEquinox(Properties config) throws Exception {
//		info("java.home=" + System.getProperty("java.home"));
//		info("java.class.path=" + System.getProperty("java.class.path"));
//
//		File baseDir = new File(System.getProperty("user.dir"))
//				.getCanonicalFile();
//		String equinoxConfigurationPath = baseDir.getPath() + File.separator
//				+ "slc-detached" + File.separator + "equinoxConfiguration";
//
//		String equinoxArgsLineDefault = "-console -noExit -clean -debug -configuration "
//				+ equinoxConfigurationPath;
//		String equinoxArgsLine = config.getProperty(PROP_SLC_OSGI_EQUINOX_ARGS,
//				equinoxArgsLineDefault);
//		// String[] equinoxArgs = { "-console", "-noExit", "-clean", "-debug",
//		// "-configuration", equinoxConfigurationPath };
//		String[] equinoxArgs = equinoxArgsLine.split(" ");
//
//		BundleContext context = EclipseStarter.startup(equinoxArgs, null);
//	}

	protected static void startMainClass() {
		Properties config = System.getProperties();
		String className = config.getProperty("slc.detached.appclass");
		String[] uiArgs = readArgumentsFromLine(config.getProperty(
				"slc.detached.appargs", ""));
		try {
			// Launch main method using reflection
			Class clss = Class.forName(className);
			Class[] mainArgsClasses = new Class[] { uiArgs.getClass() };
			Object[] mainArgs = { uiArgs };
			Method mainMethod = clss.getMethod("main", mainArgsClasses);
			mainMethod.invoke(null, mainArgs);
		} catch (Exception e) {
			throw new RuntimeException("Cannot start main class.", e);
		}

	}

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

}
