package org.argeo.slc.osgi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.springframework.osgi.context.BundleContextAware;

public class BundlesManager implements BundleContextAware {
	private final static Log log = LogFactory.getLog(BundlesManager.class);

	private BundleContext bundleContext;

	private List<String> urlsToInstall;

	public void init() {
		// Install
		if (urlsToInstall != null) {
			Map<String, Bundle> installedBundles = new HashMap<String, Bundle>();
			for (Bundle bundle : bundleContext.getBundles())
				installedBundles.put(bundle.getLocation(), bundle);

			for (String url : urlsToInstall)
				try {

					if (installedBundles.containsKey(url)) {
						Bundle bundle = installedBundles.get(url);
						// bundle.update();
						log.debug("Bundle " + bundle.getSymbolicName()
								+ " already installed from " + url);
					} else {
						Bundle bundle = bundleContext.installBundle(url);
						log.debug("Installed bundle "
								+ bundle.getSymbolicName() + " from " + url);
					}
				} catch (BundleException e) {
					log.warn("Could not install bundle from " + url + ": "
							+ e.getMessage());
				}
		}

	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void setUrlsToInstall(List<String> urlsToInstall) {
		this.urlsToInstall = urlsToInstall;
	}

}
