package org.argeo.slc.osgi.deploy;

import org.argeo.slc.core.deploy.RelativeResourceSet;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.osgi.io.OsgiBundleResourcePatternResolver;

public class OsgiResourceSet extends RelativeResourceSet implements
		BundleContextAware {
	private BundleContext bundleContext;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (getResourcePatternResolver() == null)
			setResourcePatternResolver(new OsgiBundleResourcePatternResolver(
					bundleContext.getBundle()));
		super.afterPropertiesSet();
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

}
