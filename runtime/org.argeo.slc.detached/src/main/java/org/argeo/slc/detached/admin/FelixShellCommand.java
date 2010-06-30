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
