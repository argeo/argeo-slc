package org.argeo.slc.osgiboot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class Activator implements BundleActivator {
	
	public void start(BundleContext bundleContext) throws Exception {
		try {
			info("SLC OSGi bootstrap starting...");
			OsgiBoot osgiBoot = new OsgiBoot(bundleContext);
			
			osgiBoot.installUrls( osgiBoot.getBundlesUrls());

			osgiBoot.installUrls( osgiBoot.getLocationsUrls());

//			installUrls(bundleContext, getMavenUrls());

			osgiBoot.startBundles();

			info("SLC OSGi bootstrap completed");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void stop(BundleContext context) throws Exception {
	}

/*
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
*/
/*
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
*//*
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
*/
	private static void info(Object obj) {
		System.out.println("# INFO " + obj);
	}
/*
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
*//*
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
*/
}
