/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.lib.detached;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.tasks.JvmProcess;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.osgi.context.BundleContextAware;

public class DetachedLauncher extends JvmProcess implements BundleContextAware,
		InitializingBean, ResourceLoaderAware {
	private final static Log log = LogFactory.getLog(DetachedLauncher.class);

	private BundleContext bundleContext = null;
	private ResourceLoader resourceLoader = null;

	private Resource osgiFramework = null;
	private String osgibootBundleName = "org.argeo.osgi.boot";	
	private String equinoxBundleName = "org.eclipse.osgi";
	private String xmlapisBundleName = "com.springsource.org.apache.xmlcommons";
	private String xercesBundleName = "com.springsource.org.apache.xerces";

	private List<String> excludeBundleNames = new ArrayList<String>();
	private List<String> includeBundleUrls = new ArrayList<String>();

	/**
	 * Required by Spring for JDK 1.4. see
	 * http://forum.springsource.org/showthread.php?t=74555
	 */
	private Boolean prependXmlJars = false;

	public DetachedLauncher() {
		// Override defaults
		setSynchronous(false);
		setMainClass("org.argeo.osgi.boot.Launcher");
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

		StringBuffer osgiBundles = new StringBuffer("");
		StringBuffer osgiLocations = new StringBuffer("");
		bundles: for (Bundle bundle : bundleContext.getBundles()) {
			String name = bundle.getSymbolicName();

			if (excludeBundleNames.contains(name)) {
				if (log.isDebugEnabled())
					log.debug("Exclude bundle " + name);
				continue bundles;// skip excluded
			}

			String originalLocation = bundle.getLocation();
			if (log.isTraceEnabled())
				log.trace("Original location of bundle " + name + ": "
						+ originalLocation);
			String location = removeInitialReference(originalLocation);

			// Special bundles
			if (osgibootBundleName.equals(name))
				getClasspath().add(asResource(location));
			else if (equinoxBundleName.equals(name))
				continue bundles;// skip framework
			else if (xmlapisBundleName.equals(name) && prependXmlJars)
				getPBootClasspath().add(asResource(location));
			else if (xercesBundleName.equals(name) && prependXmlJars)
				getPBootClasspath().add(asResource(location));

			if (location.startsWith("file:")) {
				File file = new File(location.substring("file:".length()));
				if (osgiLocations.length() != 0)
					osgiLocations.append(File.pathSeparatorChar);
				location = file.getPath().replace('/', File.separatorChar);
				osgiLocations.append(location);
				if (log.isTraceEnabled())
					log.trace("Added bundle " + name
							+ " to argeo.osgi.locations: " + location);
			} else {
				if (osgiBundles.length() != 0)
					osgiBundles.append(',');
				location = location.replace('/', File.separatorChar);
				osgiBundles.append(location);
				if (log.isTraceEnabled())
					log.trace("Added bundle " + name + " to osgi.bundles: "
							+ location);
			}
		}

		for (String url : includeBundleUrls) {
			if (osgiBundles.length() != 0)
				osgiBundles.append(',');
			osgiBundles.append(url);
			if (log.isDebugEnabled())
				log.debug("Include url" + url);
		}

		getSystemProperties().setProperty("osgi.bundles",
				osgiBundles.toString());
		getSystemProperties().setProperty("argeo.osgi.locations",
				osgiLocations.toString());
		
		super.afterPropertiesSet();
	}

	protected String removeInitialReference(String location) {
		if (location.startsWith("initial@reference:file:"))
			location = System.getProperty("osgi.install.area")
					+ location.substring("initial@reference:file:".length());
		if (location.charAt(location.length() - 1) == '/')
			location.substring(0, location.length() - 1);
		return location;
	}

	protected Resource asResource(String location) {
		// Resource res = resourceLoader.getResource(location);

		final Resource res;
		if (location.startsWith("file:")) {
			File file = new File(location.substring("file:".length()));
			if (!file.exists())
				throw new SlcException("File " + file + " does not exist");

			try {
				res = new FileSystemResource(file.getCanonicalFile());
			} catch (IOException e) {
				throw new SlcException("Cannot create resource based on "
						+ file, e);
			}
		} else
			res = resourceLoader.getResource(location);

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

	public void setExcludeBundleNames(List<String> excludeBundleNames) {
		this.excludeBundleNames = excludeBundleNames;
	}

	public void setIncludeBundleUrls(List<String> includeBundleUrls) {
		this.includeBundleUrls = includeBundleUrls;
	}

}
