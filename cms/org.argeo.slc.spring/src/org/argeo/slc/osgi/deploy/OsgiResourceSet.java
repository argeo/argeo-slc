package org.argeo.slc.osgi.deploy;

import org.argeo.slc.core.deploy.DefaultResourceSet;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.eclipse.gemini.blueprint.io.OsgiBundleResourceLoader;
import org.eclipse.gemini.blueprint.io.OsgiBundleResourcePatternResolver;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.core.io.ResourceLoader;

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
