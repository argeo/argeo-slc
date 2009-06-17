package org.argeo.slc.equinox;

import org.argeo.slc.SlcException;
import org.argeo.slc.deploy.DynamicRuntime;
import org.argeo.slc.osgi.OsgiBundle;
import org.argeo.slc.osgi.OsgiRuntime;
import org.eclipse.core.runtime.adaptor.EclipseStarter;

public class EquinoxRuntime extends OsgiRuntime implements
		DynamicRuntime<OsgiBundle> {

	public void shutdown() {
		try {
			EclipseStarter.shutdown();
		} catch (Exception e) {
			throw new SlcException("Cannot shutdown Equinox runtime.", e);
		}
	}

}
