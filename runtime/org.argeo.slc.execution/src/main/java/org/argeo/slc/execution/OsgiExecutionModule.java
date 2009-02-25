package org.argeo.slc.execution;

import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;

public class OsgiExecutionModule extends AbstractSpringExecutionModule implements
		BundleContextAware {
	private BundleContext bundleContext;

	public String getName() {
		return bundleContext.getBundle().getSymbolicName();
	}

	public String getVersion() {
		return bundleContext.getBundle().getHeaders().get("Bundle-Version")
				.toString();
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

}
