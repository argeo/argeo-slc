package org.argeo.slc.osgiboot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.argeo.slc.osgiboot.internal.springutil.AntPathMatcher;
import org.argeo.slc.osgiboot.internal.springutil.PathMatcher;
import org.argeo.slc.osgiboot.internal.springutil.SystemPropertyUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class OsgiBoot {
	public final static String PROP_SLC_OSGI_START = "slc.osgi.start";
	public final static String PROP_SLC_OSGI_BUNDLES = "slc.osgi.bundles";
	public final static String PROP_SLC_OSGI_LOCATIONS = "slc.osgi.locations";
	public final static String PROP_SLC_OSGI_BASE_URL = "slc.osgi.baseUrl";
	public final static String PROP_SLC_OSGIBOOT_DEBUG = "slc.osgiboot.debug";

	public final static String DEFAULT_BASE_URL = "reference:file:";
	public final static String EXCLUDES_SVN_PATTERN = "**/.svn/**";

	private boolean debug = Boolean.valueOf(
			System.getProperty(PROP_SLC_OSGIBOOT_DEBUG, "false"))
			.booleanValue();

	private boolean excludeSvn = true;

	private final BundleContext bundleContext;

	public OsgiBoot(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void installUrls(List urls) {
		Map installedBundles = getInstalledBundles();
		for (int i = 0; i < urls.size(); i++) {
			String url = (String) urls.get(i);
			try {
				if (installedBundles.containsKey(url)) {
					Bundle bundle = (Bundle) installedBundles.get(url);
					// bundle.update();
					if (debug)
						debug("Bundle " + bundle.getSymbolicName()
								+ " already installed from " + url);
				} else {
					Bundle bundle = bundleContext.installBundle(url);
					if (debug)
						debug("Installed bundle " + bundle.getSymbolicName()
								+ " from " + url);
				}
			} catch (BundleException e) {
				warn("Could not install bundle from " + url + ": "
						+ e.getMessage());
			}
		}

	}

	public void startBundles() throws Exception {
		String bundlesToStart = getProperty(PROP_SLC_OSGI_START);
		startBundles(bundlesToStart);
	}

	public void startBundles(String bundlesToStartStr) throws Exception {
		if (bundlesToStartStr == null)
			return;

		StringTokenizer st = new StringTokenizer(bundlesToStartStr, ",");
		List bundlesToStart = new ArrayList();
		while (st.hasMoreTokens()) {
			String name = st.nextToken().trim();
			bundlesToStart.add(name);
		}
		startBundles(bundlesToStart);
	}

	public void startBundles(List bundlesToStart) throws Exception {
		if (bundlesToStart.size() == 0)
			return;

		Map bundles = getBundles();
		for (int i = 0; i < bundlesToStart.size(); i++) {
			String name = bundlesToStart.get(i).toString();
			Bundle bundle = (Bundle) bundles.get(name);
			if (bundle != null)
				try {
					bundle.start();
				} catch (Exception e) {
					warn("Bundle " + name + " cannot be started: "
							+ e.getMessage());
				}
			else
				warn("Bundle " + name + " not installed.");

		}
	}

	/** Key is location */
	public Map getInstalledBundles() {
		Map installedBundles = new HashMap();

		Bundle[] bundles = bundleContext.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			installedBundles.put(bundles[i].getLocation(), bundles[i]);
		}
		return installedBundles;
	}

	/** Key is symbolic name */
	public Map getBundles() {
		Map namedBundles = new HashMap();
		Bundle[] bundles = bundleContext.getBundles();
		for (int i = 0; i < bundles.length; i++) {
			namedBundles.put(bundles[i].getSymbolicName(), bundles[i]);
		}
		return namedBundles;
	}

	public List getLocationsUrls() {
		String baseUrl = getProperty(PROP_SLC_OSGI_BASE_URL, DEFAULT_BASE_URL);
		String bundleLocations = getProperty(PROP_SLC_OSGI_LOCATIONS);
		return getLocationsUrls(baseUrl, bundleLocations);
	}

	public List getLocationsUrls(String baseUrl, String bundleLocations) {
		List urls = new ArrayList();

		if (bundleLocations == null)
			return urls;
		bundleLocations = SystemPropertyUtils
				.resolvePlaceholders(bundleLocations);
		if (debug)
			debug(PROP_SLC_OSGI_LOCATIONS + "=" + bundleLocations);

		StringTokenizer st = new StringTokenizer(bundleLocations,
				File.pathSeparator);
		while (st.hasMoreTokens()) {
			urls.add(baseUrl + st.nextToken().trim());
		}
		return urls;
	}

	public List getBundlesUrls() {
		String baseUrl = getProperty(PROP_SLC_OSGI_BASE_URL, DEFAULT_BASE_URL);
		String bundlePatterns = getProperty(PROP_SLC_OSGI_BUNDLES);
		return getBundlesUrls(baseUrl, bundlePatterns);
	}

	public List getBundlesUrls(String baseUrl, String bundlePatterns) {
		List urls = new ArrayList();

		List bundlesSets = new ArrayList();
		if (bundlePatterns == null)
			return urls;
		bundlePatterns = SystemPropertyUtils
				.resolvePlaceholders(bundlePatterns);
		if (debug)
			debug(PROP_SLC_OSGI_BUNDLES + "=" + bundlePatterns
					+ " (excludeSvn=" + excludeSvn + ")");

		StringTokenizer st = new StringTokenizer(bundlePatterns, ",");
		while (st.hasMoreTokens()) {
			bundlesSets.add(new BundlesSet(st.nextToken()));
		}

		List included = new ArrayList();
		PathMatcher matcher = new AntPathMatcher();
		for (int i = 0; i < bundlesSets.size(); i++) {
			BundlesSet bundlesSet = (BundlesSet) bundlesSets.get(i);
			for (int j = 0; j < bundlesSet.getIncludes().size(); j++) {
				String pattern = (String) bundlesSet.getIncludes().get(j);
				match(matcher, included, bundlesSet.getDir(), null, pattern);
			}
		}

		List excluded = new ArrayList();
		for (int i = 0; i < bundlesSets.size(); i++) {
			BundlesSet bundlesSet = (BundlesSet) bundlesSets.get(i);
			for (int j = 0; j < bundlesSet.getExcludes().size(); j++) {
				String pattern = (String) bundlesSet.getExcludes().get(j);
				match(matcher, excluded, bundlesSet.getDir(), null, pattern);
			}
		}

		for (int i = 0; i < included.size(); i++) {
			String fullPath = (String) included.get(i);
			if (!excluded.contains(fullPath))
				urls.add(baseUrl + fullPath);
		}

		return urls;
	}

	protected void match(PathMatcher matcher, List matched, String base,
			String currentPath, String pattern) {
		if (currentPath == null) {
			// Init
			File baseDir = new File(base.replace('/', File.separatorChar));
			File[] files = baseDir.listFiles();

			if (files == null) {
				warn("Base dir " + baseDir + " has no children, exists="
						+ baseDir.exists() + ", isDirectory="
						+ baseDir.isDirectory());
				return;
			}

			for (int i = 0; i < files.length; i++)
				match(matcher, matched, base, files[i].getName(), pattern);
		} else {
			String fullPath = base + '/' + currentPath;
			if (matched.contains(fullPath))
				return;// don't try deeper if already matched

			boolean ok = matcher.match(pattern, currentPath);
			if (debug)
				debug(currentPath + " " + (ok ? "" : " not ")
						+ " matched with " + pattern);
			if (ok) {
				matched.add(fullPath);
				return;
			} else {
				String newFullPath = relativeToFullPath(base, currentPath);
				File newFile = new File(newFullPath);
				File[] files = newFile.listFiles();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						String newCurrentPath = currentPath + '/'
								+ files[i].getName();
						if (files[i].isDirectory()) {
							if (matcher.matchStart(pattern, newCurrentPath)) {
								// recurse only if start matches
								match(matcher, matched, base, newCurrentPath,
										pattern);
							} else {
								if (debug)
									debug(newCurrentPath
											+ " does not start match with "
											+ pattern);

							}
						} else {
							boolean nonDirectoryOk = matcher.match(pattern,
									newCurrentPath);
							if (debug)
								debug(currentPath + " " + (ok ? "" : " not ")
										+ " matched with " + pattern);
							if (nonDirectoryOk)
								matched.add(relativeToFullPath(base,
										newCurrentPath));
						}
					}
				}
			}
		}
	}

	/** Transforms a relative path in a full system path. */
	protected String relativeToFullPath(String basePath, String relativePath) {
		return (basePath + '/' + relativePath).replace('/', File.separatorChar);
	}

	protected void info(Object obj) {
		System.out.println("# INFO " + obj);
	}

	protected void debug(Object obj) {
		if (debug)
			System.out.println("# DBUG " + obj);
	}

	protected void warn(Object obj) {
		System.out.println("# WARN " + obj);
		// Because of a weird bug under Windows when starting it in a forked VM
		// if (System.getProperty("os.name").contains("Windows"))
		// System.out.println("# WARN " + obj);
		// else
		// System.err.println("# WARN " + obj);
	}

	protected String getProperty(String name, String defaultValue) {
		final String value;
		if (defaultValue != null)
			value = System.getProperty(name, defaultValue);
		else
			value = System.getProperty(name);

		if (value == null || value.equals(""))
			return null;
		else
			return value;
	}

	protected String getProperty(String name) {
		return getProperty(name, null);
	}

	public boolean getDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public BundleContext getBundleContext() {
		return bundleContext;
	}

	/** Whether to exclude Subversion directories (true by default) */
	public boolean isExcludeSvn() {
		return excludeSvn;
	}

	public void setExcludeSvn(boolean excludeSvn) {
		this.excludeSvn = excludeSvn;
	}

	protected class BundlesSet {
		private String baseUrl = "reference:file";// not used yet
		private final String dir;
		private List includes = new ArrayList();
		private List excludes = new ArrayList();

		public BundlesSet(String def) {
			StringTokenizer st = new StringTokenizer(def, ";");

			if (!st.hasMoreTokens())
				throw new RuntimeException("Base dir not defined.");
			try {
				String dirPath = st.nextToken();

				if (dirPath.startsWith("file:"))
					dirPath = dirPath.substring("file:".length());

				dir = new File(dirPath.replace('/', File.separatorChar))
						.getCanonicalPath();
				if (debug)
					debug("Base dir: " + dir);
			} catch (IOException e) {
				throw new RuntimeException("Cannot convert to absolute path", e);
			}

			while (st.hasMoreTokens()) {
				String tk = st.nextToken();
				StringTokenizer stEq = new StringTokenizer(tk, "=");
				String type = stEq.nextToken();
				String pattern = stEq.nextToken();
				if ("in".equals(type) || "include".equals(type)) {
					includes.add(pattern);
				} else if ("ex".equals(type) || "exclude".equals(type)) {
					excludes.add(pattern);
				} else if ("baseUrl".equals(type)) {
					baseUrl = pattern;
				} else {
					System.err.println("Unkown bundles pattern type " + type);
				}
			}

			if (excludeSvn && !excludes.contains(EXCLUDES_SVN_PATTERN)) {
				excludes.add(EXCLUDES_SVN_PATTERN);
			}
		}

		public String getDir() {
			return dir;
		}

		public List getIncludes() {
			return includes;
		}

		public List getExcludes() {
			return excludes;
		}

		public String getBaseUrl() {
			return baseUrl;
		}

	}

}
