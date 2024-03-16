package org.argeo.api.slc.deploy;

public interface InstalledExecutables extends DeployedSystem {
	public String getExecutablePath(String key);
}
