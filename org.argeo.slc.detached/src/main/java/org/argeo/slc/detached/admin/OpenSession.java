package org.argeo.slc.detached.admin;

import java.util.Properties;

import org.argeo.slc.detached.DetachedAdminCommand;
import org.argeo.slc.detached.DetachedRequest;
import org.argeo.slc.detached.DetachedSession;
import org.osgi.framework.BundleContext;

public class OpenSession implements DetachedAdminCommand {

	public DetachedSession execute(DetachedRequest request,
			BundleContext bundleContext) {
		DetachedSession session = new DetachedSession();
		session.setUuid(Long.toString(System.currentTimeMillis()));

		Properties props = request.getProperties();
		if (props.containsKey(DetachedSession.PROP_DO_IT_AGAIN_POLICY))
			session.setDoItAgainPolicy(props
					.getProperty(DetachedSession.PROP_DO_IT_AGAIN_POLICY));
		
		return session;
	}
}
