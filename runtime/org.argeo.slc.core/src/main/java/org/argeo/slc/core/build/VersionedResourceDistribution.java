package org.argeo.slc.core.build;

import org.argeo.slc.build.NameVersion;
import org.springframework.core.io.Resource;

public class VersionedResourceDistribution extends ResourceDistribution
		implements NameVersion {
	private String name;
	private String version;

	public VersionedResourceDistribution() {
		super();
	}

	public VersionedResourceDistribution(NameVersion nameVersion,
			Resource resource) {
		this(nameVersion.getName(), nameVersion.getVersion(), resource);
	}

	public VersionedResourceDistribution(String name, String version,
			Resource resource) {
		super(resource);
		this.name = name;
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
