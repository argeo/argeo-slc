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
package org.argeo.slc.osgi.build;

import java.net.URL;
import java.util.SortedMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.NameVersion;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.core.build.VersionedResourceDistribution;
import org.argeo.slc.osgi.OsgiBundle;
import org.osgi.framework.Bundle;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.osgi.util.OsgiBundleUtils;

public class OsgiRuntimeModularDistribution extends
		AbstractOsgiModularDistribution implements ResourceLoaderAware {
	private final static Log log = LogFactory
			.getLog(OsgiRuntimeModularDistribution.class);

	private ResourceLoader resourceLoader;

	protected void fillDistributions(
			SortedMap<NameVersion, Distribution> distributions)
			throws Exception {

		String frameworkUrl = System.getProperty("osgi.framework");
		String frameworkBaseUrl = null;
		if (frameworkUrl != null)
			frameworkBaseUrl = frameworkUrl.substring(0, frameworkUrl
					.lastIndexOf('/'));
		bundles: for (Bundle bundle : getBundleContext().getBundles()) {
			OsgiBundle osgiBundle = new OsgiBundle(bundle);

			String originalLocation = bundle.getLocation();

			if (OsgiBundleUtils.isSystemBundle(bundle)) {
				continue bundles;
			}

			String location = originalLocation;
			if (originalLocation.startsWith("reference:file:"))
				location = originalLocation.substring("reference:".length());

			if (frameworkBaseUrl != null
					&& originalLocation.startsWith("initial@reference:file:")) {
				location = frameworkBaseUrl
						+ '/'
						+ originalLocation.substring("initial@reference:file:"
								.length());
			}

			try {
				URL url = new URL(location);
				Resource res = resourceLoader.getResource(url.toString());
				distributions.put(osgiBundle,
						new VersionedResourceDistribution(osgiBundle, res));

				if (log.isTraceEnabled())
					log.debug("Added url " + url + " from original location "
							+ originalLocation);
			} catch (Exception e) {
				log.warn("Cannot interpret location " + location
						+ " of bundle " + bundle + ": " + e);
			}
		}
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
}
