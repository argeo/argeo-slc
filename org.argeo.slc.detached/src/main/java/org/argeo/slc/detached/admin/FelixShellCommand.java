package org.argeo.slc.detached.admin;

import org.argeo.slc.detached.DetachedAdminCommand;
import org.argeo.slc.detached.DetachedAnswer;
import org.argeo.slc.detached.DetachedException;
import org.argeo.slc.detached.DetachedRequest;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class FelixShellCommand implements DetachedAdminCommand {
	public final static String PROP_FELIX_CMDLINE = "slc.detached.felix.cmdline";

	public DetachedAnswer execute(DetachedRequest request,
			BundleContext bundleContext) {
		ServiceReference ref = bundleContext
				.getServiceReference("org.apache.felix.shell.ShellService");
		if (ref == null)
			throw new DetachedException("Felix shell service not found.");

		String cmdLine = request.getProperties()
				.getProperty(PROP_FELIX_CMDLINE);
		if (cmdLine == null)
			throw new DetachedException("Property " + PROP_FELIX_CMDLINE
					+ " must be defined.");
		
		// TODO: check were to put Felix dependency
		// see http://felix.apache.org/site/apache-felix-shell-service.html
		throw new DetachedException("Not yet implemented.");
	}
}
