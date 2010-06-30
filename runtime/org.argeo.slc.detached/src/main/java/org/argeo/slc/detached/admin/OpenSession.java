/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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
