package org.argeo.slc.support.deploy;

import java.net.URL;

import org.argeo.slc.core.deploy.DeployedSystem;

public interface WebServer extends DeployedSystem {
	public URL getBaseUrl();
}
