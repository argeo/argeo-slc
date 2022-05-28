package org.argeo.slc.cms.deploy;

import java.nio.file.Path;

public class SimpleCmsTargetData implements CmsTargetData {
	private Path instanceData;
	private Integer httpPort;

	public SimpleCmsTargetData(Path instanceData, Integer httpPort) {
		this.instanceData = instanceData;
		this.httpPort = httpPort;
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
