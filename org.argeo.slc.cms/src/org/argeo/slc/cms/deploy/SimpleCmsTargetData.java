package org.argeo.slc.cms.deploy;

import java.nio.file.Path;

public class SimpleCmsTargetData implements CmsTargetData {
	private Path instanceData;
	private String host;
	private Integer httpPort;

	public SimpleCmsTargetData(Path instanceData, String host, Integer httpPort) {
		this.instanceData = instanceData;
		this.host = host;
		this.httpPort = httpPort;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String hostname) {
		this.host = hostname;
	}

	public Integer getHttpPort() {
		return httpPort;
	}

	public void setHttpPort(Integer httpPort) {
		this.httpPort = httpPort;
	}

	public Path getInstanceData() {
		return instanceData;
	}

	public void setInstanceData(Path instanceData) {
		this.instanceData = instanceData;
	}

}
