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

	private Boolean debug = Boolean.parseBoolean(System.getProperty(
			PROP_SLC_OSGIBOOT_DEBUG, "false"));

	private boolean excludeSvn = true;

	private final BundleContext bundleContext;

	public OsgiBoot(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void installUrls(List<String> urls) {
		Map<String, Bundle> installedBundles = getInstalledBundles();
		for (String url : urls) {
			try {
				if (installedBundles.containsKey(url)) {
					Bundle bundle = installedBundles.get(url);
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

		Map<String, Bundle> bundles = getBundles();
		for (int i = 0; i < bundlesToStart.size(); i++) {
			String name = bundlesToStart.get(i).toString();
			Bundle bundle = bundles.get(name);
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
	public Map<String, Bundle> getInstalledBundles() {
		Map<String, Bundle> installedBundles = new HashMap<String, Bundle>();

		for (Bundle bundle : bundleContext.getBundles()) {
			installedBundles.put(bundle.getLocation(), bundle);
		}
		return installedBundles;
	}

	/** Key is symbolic name */
	public Map<String, Bundle> getBundles() {
		Map<String, Bundle> installedBundles = new HashMap<String, Bundle>();
		for (Bundle bundle : bundleContext.getBundles())
			installedBundles.put(bundle.getSymbolicName(), bundle);
		return installedBundles;
	}

	public List<String> getLocationsUrls() {
		String baseUrl = getProperty(PROP_SLC_OSGI_BASE_URL, DEFAULT_BASE_URL);
		String bundleLocations = getProperty(PROP_SLC_OSGI_LOCATIONS);
		return getLocationsUrls(baseUrl, bundleLocations);
	}

	public List<String> getLocationsUrls(String baseUrl, String bundleLocations) {
		List<String> urls = new ArrayList<String>();

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

	public List<String> getBundlesUrls() {
		String baseUrl = getProperty(PROP_SLC_OSGI_BASE_URL, DEFAULT_BASE_URL);
		String bundlePatterns = getProperty(PROP_SLC_OSGI_BUNDLES);
		return getBundlesUrls(baseUrl, bundlePatterns);
	}

	public List<String> getBundlesUrls(String baseUrl, String bundlePatterns) {
		List<String> urls = new ArrayList<String>();

		List<BundlesSet> bundlesSets = new ArrayList<BundlesSet>();
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

		List<String> included = new ArrayList<String>();
		PathMatcher matcher = new AntPathMatcher();
		for (BundlesSet bundlesSet : bundlesSets)
			for (String pattern : bundlesSet.getIncludes())
				match(matcher, included, bundlesSet.getDir(), null, pattern);

		List<String> excluded = new ArrayList<String>();
		for (BundlesSet bundlesSet : bundlesSets)
			for (String pattern : bundlesSet.getExcludes())
				match(matcher, excluded, bundlesSet.getDir(), null, pattern);

		for (String fullPath : included) {
			if (!excluded.contains(fullPath))
				urls.add(baseUrl + fullPath);
		}

		return urls;
	}

	protected void match(PathMatcher matcher, List<String> matched,
			String base, String currentPath, String pattern) {
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

			for (File file : files)
				match(matcher, matched, base, file.getName(), pattern);
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
				String newFullPath = (base + '/' + currentPath).replace('/',
						File.separatorChar);
				File[] files = new File(newFullPath).listFiles();
				if (files != null) {
					for (File file : files)
						if (file.isDirectory()) {
							String newCurrentPath = currentPath + '/'
									+ file.getName();
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
						}
				} else {
					//warn("Not a directory: " + newFullPath);
				}
			}
		}
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

	public Boolean getDebug() {
		return debug;
	}

	public void setDebug(Boolean debug) {
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
		private List<String> includes = new ArrayList<String>();
		private List<String> excludes = new ArrayList<String>();

		public BundlesSet(String def) {
			StringTokenizer st = new StringTokenizer(def, ";");

			if (!st.hasMoreTokens())
				throw new RuntimeException("Base dir not defined.");
			try {
				String dirPath = st.nextToken();
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

		public List<String> getIncludes() {
			return includes;
		}

		public List<String> getExcludes() {
			return excludes;
		}

		public String getBaseUrl() {
			return baseUrl;
		}

	}

}
