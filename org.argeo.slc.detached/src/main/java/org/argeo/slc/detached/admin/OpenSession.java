package org.argeo.slc.detached.admin;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.detached.DetachedAdminCommand;
import org.argeo.slc.detached.DetachedException;
import org.argeo.slc.detached.DetachedRequest;
import org.argeo.slc.detached.DetachedSession;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class OpenSession implements DetachedAdminCommand {
	private final static Log log = LogFactory.getLog(OpenSession.class);

	public DetachedSession execute(DetachedRequest request,
			BundleContext bundleContext) {
		DetachedSession session = new DetachedSession();
		session.setUuid(Long.toString(System.currentTimeMillis()));

		Properties props = request.getProperties();
		if (props.containsKey(DetachedSession.PROP_DO_IT_AGAIN_POLICY))
			session.setDoItAgainPolicy(props
					.getProperty(DetachedSession.PROP_DO_IT_AGAIN_POLICY));

		String refreshedBundles = props
				.getProperty("slc.detached.refreshedBundles");
		if (refreshedBundles != null) {
			Bundle[] bundles = bundleContext.getBundles();
			Bundle bundle = null;
			for (int i = 0; i < bundles.length; i++) {
				if (bundles[i].getSymbolicName().equals(refreshedBundles)) {
					bundle = bundles[i];
				}
			}

			if (bundle != null) {
				try {
					bundle.stop();
					bundle.update();
					bundle.start();
					log.info("Refreshed bundle " + bundle.getSymbolicName());
				} catch (BundleException e) {
					throw new DetachedException("Could not refresh bundle "
							+ bundle.getSymbolicName(), e);
				}
			} else {
				log.warn("Did not find bundle to refresh " + refreshedBundles);
			}

		}

		return session;
	}
}
