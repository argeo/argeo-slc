package org.argeo.slc.equinox.cli;

import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.osgi.framework.BundleContext;

@SuppressWarnings("restriction")
public class Main {

	public static void main(String[] args) {
		try {
			String confDir = "";
			String[] equinoxArgs = { "-console", "-conf", confDir };
			BundleContext systemBundleContext = EclipseStarter.startup(
					equinoxArgs, null);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
