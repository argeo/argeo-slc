package org.argeo.slc.lib.detached;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.tasks.JvmProcess;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.osgi.context.BundleContextAware;

public class DetachedLauncher extends JvmProcess implements BundleContextAware,
		InitializingBean, ResourceLoaderAware {
	private final static Log log = LogFactory.getLog(DetachedLauncher.class);

	private BundleContext bundleContext = null;
	private ResourceLoader resourceLoader = null;

	private Resource osgiFramework = null;
	private String osgibootBundleName = "org.argeo.slc.osgiboot";
	private String equinoxBundleName = "org.eclipse.osgi";
	private String xmlapisBundleName = "com.springsource.org.apache.xmlcommons";
	private String xercesBundleName = "com.springsource.org.apache.xerces";

	/**
	 * Required by Spring for JDK 1.4. see
	 * http://forum.springsource.org/showthread.php?t=74555
	 */
	private Boolean prependXmlJars = false;

	public DetachedLauncher() {
		// Override defaults
		setSynchronous(false);
		setMainClass("org.argeo.slc.osgiboot.Launcher");
	}

	public void afterPropertiesSet() throws Exception {
		if (bundleContext == null)
			throw new SlcException("An OSGi bundle context is required.");

		// Equinox jar
		if (osgiFramework == null)
			getClasspath()
					.add(asResource(System.getProperty("osgi.framework")));
		else
			getClasspath().add(osgiFramework);

		StringBuffer osgiLocations = new StringBuffer("");
		bundles: for (Bundle bundle : bundleContext.getBundles()) {
			String name = bundle.getSymbolicName();

			// Special bundles
			if (osgibootBundleName.equals(name))
				getClasspath().add(findOsgiboot(bundle));
			else if (equinoxBundleName.equals(name))
				continue bundles;// skip framework
			else if (xmlapisBundleName.equals(name) && prependXmlJars)
				getPBootClasspath().add(asResource(bundle.getLocation()));
			else if (xercesBundleName.equals(name) && prependXmlJars)
				getPBootClasspath().add(asResource(bundle.getLocation()));

			if (osgiLocations.length() != 0)
				osgiLocations.append(',');
			osgiLocations.append(bundle.getLocation());
		}

		getSystemProperties().setProperty("osgi.bundles",
				osgiLocations.toString());
	}

	protected Resource findOsgiboot(Bundle bundle) {
		String location = bundle.getLocation();
		if (location.startsWith("initial@reference:file:"))
			location = System.getProperty("osgi.install.area") + "/../"
					+ location.substring("initial@reference:file:".length());
		if (location.charAt(location.length() - 1) == '/')
			location.substring(0, location.length() - 1);
		return asResource(location);
	}

	protected Resource asResource(String location) {
		Resource res = resourceLoader.getResource(location);
		if (log.isDebugEnabled())
			log.debug("Converted " + location + " to " + res);
		return res;
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public void setOsgibootBundleName(String osgibootBundleName) {
		this.osgibootBundleName = osgibootBundleName;
	}

	public void setXmlapisBundleName(String xmlapisBundleName) {
		this.xmlapisBundleName = xmlapisBundleName;
	}

	public void setXercesBundleName(String xercesBundleName) {
		this.xercesBundleName = xercesBundleName;
	}

	public void setOsgiFramework(Resource osgiFramework) {
		this.osgiFramework = osgiFramework;
	}

	public void setEquinoxBundleName(String equinoxBundleName) {
		this.equinoxBundleName = equinoxBundleName;
	}

	public void setPrependXmlJars(Boolean prependXmlJars) {
		this.prependXmlJars = prependXmlJars;
	}

}