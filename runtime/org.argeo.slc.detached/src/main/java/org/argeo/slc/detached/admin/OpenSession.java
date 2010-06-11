package org.argeo.slc.detached.admin;

import java.util.List;
import java.util.ArrayList;
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

			Bundle[] allBundles = bundleContext.getBundles();
			Bundle[] bundlesToRefresh = new Bundle[refreshedBundleNames.size()];						

			log.debug("Bundles to refresh for DetachedSession:");
			
			for(int i = 0; i < bundlesToRefresh.length; ++i) {
				bundlesToRefresh[i] = getBundleForName((String)refreshedBundleNames.get(i), allBundles);
				if(log.isDebugEnabled())
					log.debug(" " + refreshedBundleNames.get(i));
			}

			(new MinimalBundlesManager(bundleContext)).upgradeSynchronous(bundlesToRefresh);
		}

		return session;
	}
	
	private Bundle getBundleForName(String symbolicName, Bundle[] bundles) {
		for (int i = 0; i < bundles.length; i++) {
			if (symbolicName.equals(bundles[i].getSymbolicName())) {
				return bundles[i];
			}
		}
		throw new DetachedException("No Bundle found for symbolic name " + symbolicName);
	}
}
