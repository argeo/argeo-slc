package org.argeo.slc.support.deploy;

import java.io.File;
import java.net.URL;

import org.argeo.slc.core.deploy.DeployedSystem;

public interface WebApplication extends DeployedSystem{
	public URL getBaseUrl();
	public File getRootLocation();
}
