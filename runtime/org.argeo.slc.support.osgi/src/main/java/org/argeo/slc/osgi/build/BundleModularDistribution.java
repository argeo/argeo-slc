/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
package org.argeo.slc.osgi.build;

import java.net.URL;
import java.util.Enumeration;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.apache.commons.io.IOUtils;
import org.argeo.slc.BasicNameVersion;
import org.argeo.slc.NameVersion;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.core.build.VersionedResourceDistribution;
import org.osgi.framework.Constants;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;

public class BundleModularDistribution extends AbstractOsgiModularDistribution
		implements ResourceLoaderAware {
	private ResourceLoader resourceLoader;

	private String libDirectory = "/lib";

	@SuppressWarnings(value = { "unchecked" })
	protected void fillDistributions(
			SortedMap<NameVersion, Distribution> distributions)
			throws Exception {
		Enumeration<URL> urls = (Enumeration<URL>) getBundleContext()
				.getBundle().findEntries(libDirectory, "*.jar", false);
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
	}

	public void setLibDirectory(String libDirectory) {
		this.libDirectory = libDirectory;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	/*
	 * @SuppressWarnings(value = { "unchecked" }) protected URL
	 * findModule(String moduleName, String version) { Enumeration<URL> urls =
	 * (Enumeration<URL>) bundleContext.getBundle() .findEntries(libDirectory,
	 * moduleName + "*", false);
	 * 
	 * if (!urls.hasMoreElements()) throw new SlcException("Cannot find module "
	 * + moduleName);
	 * 
	 * URL url = urls.nextElement();
	 * 
	 * // TODO: check version as well if (urls.hasMoreElements()) throw new
	 * SlcException("More than one module with name " + moduleName); return url;
	 * }
	 */

}
