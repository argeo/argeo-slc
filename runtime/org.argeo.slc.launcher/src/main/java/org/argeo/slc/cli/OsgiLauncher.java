package org.argeo.slc.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class OsgiLauncher {
	public final static String PROP_SLC_OSGI_EQUINOX_ARGS = "slc.osgi.equinox.args";
	public final static String PROP_SLC_OSGI_START = "slc.osgi.start";

	public static void main(String[] args) {
		try {
			String baseUrl = args[0];
			String config = args[1];

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

			List<String> urls = asUrls(baseUrl, mavenFiles);

			// Start Equinox
			File baseDir = new File(System.getProperty("user.dir"))
					.getCanonicalFile();
			String equinoxConfigurationPath = baseDir.getPath()
					+ File.separator + "slc-detached" + File.separator
					+ "equinoxConfiguration";

			String equinoxArgsLineDefault = "-console -noExit -clean -debug -configuration "
					+ equinoxConfigurationPath;
			String equinoxArgsLine = System.getProperty(
					PROP_SLC_OSGI_EQUINOX_ARGS, equinoxArgsLineDefault);
			String[] equinoxArgs = equinoxArgsLine.split(" ");

			BundleContext bundleContext = EclipseStarter.startup(equinoxArgs,
					null);

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

			String bundlesToStart = System.getProperty(PROP_SLC_OSGI_START,
					"org.springframework.osgi.extender");
			StringTokenizer st = new StringTokenizer(bundlesToStart, ",");
			Map<String, Bundle> bundles = getBundles(bundleContext);
			while (st.hasMoreTokens()) {
				String name = st.nextToken().trim();
				Bundle bundle = bundles.get(name);
				bundle.start();

			}
		} catch (Exception e) {
			e.printStackTrace();
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

	private static void info(Object obj) {
		System.out.println("[INFO] " + obj);
	}

	private static void warn(Object obj) {
		System.err.println("[WARN] " + obj);
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
