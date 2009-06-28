package org.argeo.slc.osgi.build;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.UnsupportedException;
import org.argeo.slc.build.BasicNameVersion;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.build.ModularDistribution;
import org.argeo.slc.build.NameVersion;
import org.argeo.slc.core.build.VersionedResourceDistribution;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.osgi.context.BundleContextAware;

public class BundleModularDistribution implements ModularDistribution,
		BundleContextAware, InitializingBean, ResourceLoaderAware {
	private final static Log log = LogFactory
			.getLog(BundleModularDistribution.class);

	private BundleContext bundleContext;
	private ResourceLoader resourceLoader;

	private String libDirectory = "/lib";
	private EclipseUpdateSite eclipseUpdateSite;

	/** Initialized by the object itself. */
	private SortedMap<NameVersion, VersionedResourceDistribution> distributions = new TreeMap<NameVersion, VersionedResourceDistribution>();

	public Distribution getModuleDistribution(String moduleName,
			String moduleVersion) {
		return distributions
				.get(new BasicNameVersion(moduleName, moduleVersion));
		// URL url = findModule(moduleName, moduleVersion);
		// return new ResourceDistribution(new UrlResource(url));
	}

	@SuppressWarnings(value = { "unchecked" })
	protected URL findModule(String moduleName, String version) {
		Enumeration<URL> urls = (Enumeration<URL>) bundleContext.getBundle()
				.findEntries(libDirectory, moduleName + "*", false);

		if (!urls.hasMoreElements())
			throw new SlcException("Cannot find module " + moduleName);

		URL url = urls.nextElement();

		// TODO: check version as well
		if (urls.hasMoreElements())
			throw new SlcException("More than one module with name "
					+ moduleName);
		return url;
	}

	public String getDistributionId() {
		return bundleContext.getBundle().getSymbolicName()
				+ "-"
				+ bundleContext.getBundle().getHeaders().get(
						Constants.BUNDLE_VERSION);
	}

	public Set<NameVersion> listModulesNameVersions() {
		return distributions.keySet();
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	@SuppressWarnings(value = { "unchecked" })
	public void afterPropertiesSet() throws Exception {
		Enumeration<URL> urls = (Enumeration<URL>) bundleContext.getBundle()
				.findEntries(libDirectory, "*.jar", false);
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			JarInputStream in = null;
			try {
				in = new JarInputStream(url.openStream());
				Manifest mf = in.getManifest();
				String name = mf.getMainAttributes().getValue(
						Constants.BUNDLE_SYMBOLICNAME);
				// Skip additional specs such as
				// ; singleton:=true
				if (name.indexOf(';') > -1) {
					name = new StringTokenizer(name, " ;").nextToken();
				}

				String version = mf.getMainAttributes().getValue(
						Constants.BUNDLE_VERSION);
				BasicNameVersion nameVersion = new BasicNameVersion(name,
						version);
				distributions.put(nameVersion,
						new VersionedResourceDistribution(name, version,
								resourceLoader.getResource(url.toString())));
			} finally {
				IOUtils.closeQuietly(in);
			}
		}
		if (log.isDebugEnabled())
			log.debug("Distribution " + getName() + ":" + getVersion()
					+ " loaded (" + distributions.size() + " modules)");

	}

	protected String findVersion(String name) {
		Set<String> versions = new HashSet<String>();
		for (NameVersion key : distributions.keySet()) {
			if (key.getName().equals(name))
				versions.add(key.getVersion());
		}

		if (versions.size() == 0)
			throw new SlcException("Cannot find version for name " + name);
		else if (versions.size() > 1)
			throw new SlcException("Found more than one version for name "
					+ name + ": " + versions);
		else
			return versions.iterator().next();

	}

	public void setLibDirectory(String libDirectory) {
		this.libDirectory = libDirectory;
	}

	public Object getDescriptor(String descriptorType) {
		if (descriptorType.equals("eclipse"))
			return writeEclipseUpdateSite();
		else
			throw new UnsupportedException("descriptorType", descriptorType);
	}

	protected Set<NameVersion> writePlainUrlList() {
		return distributions.keySet();
	}

	protected String writeEclipseUpdateSite() {
		if (eclipseUpdateSite == null)
			throw new SlcException("No eclipse update site declared.");

		StringBuffer buf = new StringBuffer("");
		buf.append("<site>");

		List<EclipseUpdateSiteCategory> usedCategories = new ArrayList<EclipseUpdateSiteCategory>();
		for (EclipseUpdateSiteFeature feature : eclipseUpdateSite.getFeatures()) {

			String featureId = feature.getName();
			String featureVersion = findVersion(featureId);
			buf.append("<feature");
			buf.append(" url=\"features/").append(featureId).append('_')
					.append(featureVersion).append(".jar\"");
			buf.append(" id=\"").append(featureId).append("\"");
			buf.append(" version=\"").append(featureVersion).append("\"");
			buf.append(">\n");

			for (EclipseUpdateSiteCategory category : feature.getCategories()) {
				usedCategories.add(category);
				buf.append("  <category name=\"").append(category.getName())
						.append("\"/>\n");
			}
			buf.append("</feature>\n\n");
		}

		for (EclipseUpdateSiteCategory category : usedCategories) {
			buf.append("<category-def");
			buf.append(" name=\"").append(category.getName()).append("\"");
			buf.append(" label=\"").append(category.getLabel()).append("\"");
			buf.append(">\n");
			buf.append("  <description>").append(category.getDescription())
					.append("</description>\n");
			buf.append("</category-def>\n\n");
		}

		buf.append("</site>");
		return buf.toString();
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public String getName() {
		return bundleContext.getBundle().getSymbolicName();
	}

	public String getVersion() {
		return bundleContext.getBundle().getHeaders().get(
				Constants.BUNDLE_VERSION).toString();
	}

	@Override
	public String toString() {
		return new BasicNameVersion(this).toString();
	}

	public void setEclipseUpdateSite(EclipseUpdateSite eclipseUpdateSite) {
		this.eclipseUpdateSite = eclipseUpdateSite;
	}

}
