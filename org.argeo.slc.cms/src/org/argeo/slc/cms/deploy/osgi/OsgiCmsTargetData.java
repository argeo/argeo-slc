package org.argeo.slc.cms.deploy.osgi;

import java.nio.file.Path;

import org.argeo.slc.cms.deploy.SimpleCmsTargetData;

public class OsgiCmsTargetData extends SimpleCmsTargetData {
	private Integer telnetPort;

	public OsgiCmsTargetData(Path instanceData, Integer httpPort, Integer telnetPort) {
		super(instanceData, httpPort);
		this.telnetPort = telnetPort;
	}

	public Integer getTelnetPort() {
		return telnetPort;
	}

	public void setTelnetPort(Integer telnetPort) {
		this.telnetPort = telnetPort;
	}

}
