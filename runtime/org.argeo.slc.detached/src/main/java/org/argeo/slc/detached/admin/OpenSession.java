package org.argeo.slc.detached.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

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
			List refreshedBundleNames = new ArrayList();
			StringTokenizer st = new StringTokenizer(refreshedBundles, ",");
			while (st.hasMoreTokens()) {
				refreshedBundleNames.add(st.nextElement());
			}

			Bundle[] bundles = bundleContext.getBundles();

			List bundlesToRefresh = new ArrayList();
			for (int i = 0; i < bundles.length; i++) {
				if (refreshedBundleNames.contains(bundles[i].getSymbolicName())) {
					bundlesToRefresh.add(bundles[i]);
				}
			}

			for (int i = 0; i < bundlesToRefresh.size(); i++) {
				Bundle bundle = (Bundle) bundlesToRefresh.get(i);
				try {
					bundle.stop();
					bundle.update();
					bundle.start();
					log.info("Refreshed bundle " + bundle.getSymbolicName());
				} catch (BundleException e) {
					throw new DetachedException("Could not refresh bundle "
							+ bundle.getSymbolicName(), e);
				}
			}
		}

		return session;
	}
}
