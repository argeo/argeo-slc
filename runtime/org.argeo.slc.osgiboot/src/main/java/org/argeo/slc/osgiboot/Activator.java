package org.argeo.slc.osgiboot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class Activator implements BundleActivator {
	public final static String PROP_SLC_OSGI_START = "slc.osgi.start";
	public final static String PROP_SLC_OSGI_BUNDLES = "slc.osgi.bundles";
	public final static String PROP_SLC_OSGI_DEV_BASES = "slc.osgi.devBases";
	public final static String PROP_SLC_OSGI_DEV_PATTERNS = "slc.osgi.devPatterns";
	public final static String PROP_SLC_OSGI_LOCATIONS = "slc.osgi.locations";
	public final static String PROP_SLC_OSGI_BASE_URL = "slc.osgi.baseUrl";
	public final static String PROP_SLC_MAVEN_DEPENDENCY_FILE = "slc.maven.dependencyFile";
	public final static String PROP_SLC_OSGIBOOT_DEBUG = "slc.osgiboot.debug";

	private static Boolean debug = Boolean.parseBoolean(System.getProperty(
			PROP_SLC_OSGIBOOT_DEBUG, "false"));

	public void start(BundleContext bundleContext) throws Exception {
		try {
			info("SLC OSGi bootstrap starting...");
//			installUrls(bundleContext, getDevLocationsUrls());

			installUrls(bundleContext, getLocationsUrls());

			installUrls(bundleContext, getBundlesUrls());

			installUrls(bundleContext, getMavenUrls());

			startBundles(bundleContext);

			info("SLC OSGi bootstrap completed");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void stop(BundleContext context) throws Exception {
	}

	protected static void installUrls(BundleContext bundleContext,
			List<String> urls) {
		Map<String, Bundle> installedBundles = getInstalledBundles(bundleContext);
		for (String url : urls) {
			try {
				if (installedBundles.containsKey(url)) {
					Bundle bundle = installedBundles.get(url);
					// bundle.update();
					info("Bundle " + bundle.getSymbolicName()
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

	protected List<String> getLocationsUrls() {
		List<String> urlsProvided = new ArrayList<String>();

		String baseUrl = getProperty(PROP_SLC_OSGI_BASE_URL, "reference:file:");
		String bundlesList = getProperty(PROP_SLC_OSGI_LOCATIONS);
		if (bundlesList == null)
			return urlsProvided;
		bundlesList = SystemPropertyUtils.resolvePlaceholders(bundlesList);

		StringTokenizer st = new StringTokenizer(bundlesList,
				File.pathSeparator);
		while (st.hasMoreTokens()) {
			urlsProvided.add(baseUrl + st.nextToken().trim());
		}
		return urlsProvided;
	}

	protected List<String> getDevLocationsUrls() {
		List<String> urls = new ArrayList<String>();

		String devBase = getProperty(PROP_SLC_OSGI_DEV_BASES);
		String devPatterns = getProperty(PROP_SLC_OSGI_DEV_PATTERNS);
		if (devBase == null)
			return urls;
		devBase = SystemPropertyUtils.resolvePlaceholders(devBase);
		devBase = devBase.replace(File.separatorChar, '/');
		devPatterns = SystemPropertyUtils.resolvePlaceholders(devPatterns);

		List<String> bases = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(devBase, ",");
		while (st.hasMoreTokens()) {
			String token = st.nextToken().trim();
			char lastChar = token.charAt(token.length() - 1);
			if (lastChar != '/')
				token = token + '/';
			bases.add(token);
		}

		List<String> patterns = new ArrayList<String>();
		st = new StringTokenizer(devPatterns, ";");
		while (st.hasMoreTokens()) {
			patterns.add(st.nextToken().trim());
		}

		List<String> matched = new ArrayList<String>();
		PathMatcher matcher = new AntPathMatcher();
		for (String base : bases)
			for (String pattern : patterns)
				match(matcher, matched, base, null, pattern);

		for (String fullPath : matched)
			urls.add("reference:file:" + fullPath);

		return urls;
	}

	protected List<String> getBundlesUrls() {
		List<String> urls = new ArrayList<String>();

		List<BundlesSet> bundlesSets = new ArrayList<BundlesSet>();
		String bundles = getProperty(PROP_SLC_OSGI_BUNDLES);
		if (bundles == null)
			return urls;
		info(PROP_SLC_OSGI_BUNDLES + "=" + bundles);

		StringTokenizer st = new StringTokenizer(bundles, ",");
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
				urls.add("reference:file:" + fullPath);
		}

		return urls;
	}

	private class BundlesSet {
		private String baseUrl = "reference:file";
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
					warn("Not a directory: " + newFullPath);
				}
			}
		}
	}

	protected List<String> getMavenUrls() throws Exception {
		String baseUrl = "reference:file:" + System.getProperty("user.home")
				+ "/.m2/repository/";
		String config = getProperty(PROP_SLC_MAVEN_DEPENDENCY_FILE);
		if (config == null)
			return new ArrayList<String>();

		List<MavenFile> mavenFiles = new ArrayList<MavenFile>();
		BufferedReader in = new BufferedReader(new FileReader(config));
		String line = null;
		while ((line = in.readLine()) != null) {
			try {
				line = line.trim();
				if (line.equals("")
						|| line
								.startsWith("The following files have been resolved:"))
					continue;// skip

				mavenFiles.add(convert(line));
			} catch (Exception e) {
				warn("Could not load line " + line);
			}
		}

		return asUrls(baseUrl, mavenFiles);
	}

	protected void startBundles(BundleContext bundleContext) throws Exception {
		String bundlesToStart = getProperty(PROP_SLC_OSGI_START);
		if (bundlesToStart == null)
			return;

		StringTokenizer st = new StringTokenizer(bundlesToStart, ",");
		Map<String, Bundle> bundles = getBundles(bundleContext);
		while (st.hasMoreTokens()) {
			String name = st.nextToken().trim();
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

	protected static Map<String, Bundle> getInstalledBundles(
			BundleContext bundleContext) {
		Map<String, Bundle> installedBundles = new HashMap<String, Bundle>();

		for (Bundle bundle : bundleContext.getBundles()) {
			String key = bundle.getSymbolicName();
			if (key == null) {
				key = bundle.getLocation();
			}
			installedBundles.put(key, bundle);
		}
		return installedBundles;
	}

	protected static Map<String, Bundle> getBundles(BundleContext bundleContext) {
		Map<String, Bundle> installedBundles = new HashMap<String, Bundle>();
		for (Bundle bundle : bundleContext.getBundles())
			installedBundles.put(bundle.getSymbolicName(), bundle);
		return installedBundles;
	}

	protected static List<String> asUrls(String baseUrl,
			List<MavenFile> mavenFiles) {
		List<String> urls = new ArrayList<String>();
		for (MavenFile mf : mavenFiles)
			urls.add(convertToUrl(baseUrl, mf));
		return urls;
	}

	protected static String convertToUrl(String baseUrl, MavenFile mf) {
		return baseUrl + mf.getGroupId().replace('.', '/') + '/'
				+ mf.getArtifactId() + '/' + mf.getVersion() + '/'
				+ mf.getArtifactId() + '-' + mf.getVersion() + '.'
				+ mf.getType();
	}

	protected static MavenFile convert(String str) {
		StringTokenizer st = new StringTokenizer(str, ":");
		MavenFile component = new MavenFile();
		component.setGroupId(st.nextToken());
		component.setArtifactId(st.nextToken());
		component.setType(st.nextToken());
		component.setVersion(st.nextToken());
		component.setScope(st.nextToken());
		return component;
	}

	protected static String getProperty(String name, String defaultValue) {
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

	protected static String getProperty(String name) {
		return getProperty(name, null);
	}

	private static void info(Object obj) {
		System.out.println("# INFO " + obj);
	}

	private static void debug(Object obj) {
		if (debug)
			System.out.println("# DBUG " + obj);
	}

	private static void warn(Object obj) {
		System.out.println("# WARN " + obj);
		// if (System.getProperty("os.name").contains("Windows"))
		// System.out.println("# WARN " + obj);
		// else
		// System.err.println("# WARN " + obj);
	}

	static class MavenFile {
		private String groupId;
		private String artifactId;
		private String version;
		private String type;
		private String classifier;
		private String scope;

		public String getScope() {
			return scope;
		}

		public void setScope(String scope) {
			this.scope = scope;
		}

		private String distributionId;

		public String getDistributionId() {
			return distributionId;
		}

		public void setDistributionId(String distributionId) {
			this.distributionId = distributionId;
		}

		public String getGroupId() {
			return groupId;
		}

		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}

		public String getArtifactId() {
			return artifactId;
		}

		public void setArtifactId(String artifactId) {
			this.artifactId = artifactId;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getClassifier() {
			return classifier;
		}

		public void setClassifier(String classifier) {
			this.classifier = classifier;
		}

	}

}
