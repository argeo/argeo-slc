package org.argeo.slc.support.deploy;

import org.argeo.slc.core.deploy.InstalledExecutables;
import org.argeo.slc.core.deploy.TargetData;

public class HttpdServerTargetData implements TargetData {
	private String serverRoot;
	private Integer port;
	private InstalledExecutables executables;

	public String getServerRoot() {
		return serverRoot;
	}

	public void setServerRoot(String serverRoot) {
		this.serverRoot = serverRoot;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public InstalledExecutables getExecutables() {
		return executables;
	}

	public void setExecutables(InstalledExecutables executables) {
		this.executables = executables;
	}

}
