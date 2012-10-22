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
package org.argeo.slc.osgi.deploy;

import org.argeo.slc.core.deploy.DefaultResourceSet;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.osgi.io.OsgiBundleResourceLoader;
import org.springframework.osgi.io.OsgiBundleResourcePatternResolver;
import org.springframework.osgi.util.OsgiBundleUtils;

/**
 * Retrieves ressources from an OSGi bundle either the active one or another one
 * referenced by its symbolic name.
 */
public class OsgiResourceSet extends DefaultResourceSet implements
		BundleContextAware {
	private BundleContext bundleContext;
	private Bundle bundle = null;
	private String bundleSymbolicName = null;

	private OsgiBundleResourceLoader osgiBundleResourceLoader = null;

	@Override
	public void afterPropertiesSet() throws Exception {
		osgiBundleResourceLoader = new OsgiBundleResourceLoader(getBundle());
		if (getResourcePatternResolver() == null)
			setResourcePatternResolver(new OsgiBundleResourcePatternResolver(
					osgiBundleResourceLoader));
		super.afterPropertiesSet();
	}

	public Bundle getBundle() {
		if (bundle != null)
			return bundle;
		else if (bundleSymbolicName != null)// do not cache
			return OsgiBundleUtils.findBundleBySymbolicName(bundleContext,
					bundleSymbolicName);
		else
			// containing bundle
			return bundleContext.getBundle();
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	@Override
	public ResourceLoader getResourceLoaderToUse() {
		return osgiBundleResourceLoader;
	}

	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
	}

	public void setBundleSymbolicName(String bundleSymbolicName) {
		this.bundleSymbolicName = bundleSymbolicName;
	}

}
