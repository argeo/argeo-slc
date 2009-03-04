package org.argeo.slc.osgiboot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
	public final static String PROP_SLC_OSGI_DEV_BASES = "slc.osgi.devBases";
	public final static String PROP_SLC_OSGI_DEV_PATTERNS = "slc.osgi.devPatterns";
	public final static String PROP_SLC_OSGI_LOCATIONS = "slc.osgi.locations";
	public final static String PROP_SLC_MAVEN_DEPENDENCY_FILE = "slc.maven.dependencyFile";

	private static Boolean debug = true;

	public void start(BundleContext bundleContext) throws Exception {
		installUrls(bundleContext, getDevLocationsUrls());

		installUrls(bundleContext, getLocationsUrls());

		List<String> urls = getMavenUrls();
		installUrls(bundleContext, urls);

		startBundles(bundleContext);
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
					info("Installed bundle " + bundle.getSymbolicName()
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

		String bundlesList = getProperty(PROP_SLC_OSGI_LOCATIONS);
		if (bundlesList == null)
			return urlsProvided;
		bundlesList = SystemPropertyUtils.resolvePlaceholders(bundlesList);

		StringTokenizer st = new StringTokenizer(bundlesList,
				File.pathSeparator);
		while (st.hasMoreTokens()) {
			urlsProvided.add("reference:file:" + st.nextToken().trim());
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

	protected void match(PathMatcher matcher, List<String> matched,
			String base, String currentPath, String pattern) {
		if (currentPath == null) {
			// Init
			File[] files = new File(base.replace('/', File.separatorChar))
					.listFiles();
			for (File file : files)
				if (file.isDirectory())
					match(matcher, matched, base, file.getName(), pattern);
		} else {
			String fullPath = base + currentPath;
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
				File[] files = new File((base + currentPath).replace('/',
						File.separatorChar)).listFiles();
				for (File file : files)
					if (file.isDirectory()) {
						String newCurrentPath = currentPath + '/'
								+ file.getName();
						if (matcher.matchStart(pattern, newCurrentPath)) {
							// recurse only if start matches
							match(matcher, matched, base, newCurrentPath,
									pattern);
						} else {
							debug(newCurrentPath
									+ " does not start match with " + pattern);

						}
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
				System.err.println("Could not load line " + line);
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
					warn("Bundle name cannot be started: " + e.getMessage());
				}
			else
				warn("Bundle " + name + " not installed.");

		}
	}

	protected static Map<String, Bundle> getInstalledBundles(
			BundleContext bundleContext) {
		Map<String, Bundle> installedBundles = new HashMap<String, Bundle>();
		for (Bundle bundle : bundleContext.getBundles())
			installedBundles.put(bundle.getLocation(), bundle);
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

	protected String getProperty(String name) {
		String value = System.getProperty(name);
		if (value == null || value.equals(""))
			return null;
		else
			return value;
	}

	private static void info(Object obj) {
		System.out.println("#INFO " + obj);
	}

	private static void debug(Object obj) {
		System.out.println("#DBUG " + obj);
	}

	private static void warn(Object obj) {
		System.err.println("#WARN " + obj);
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
