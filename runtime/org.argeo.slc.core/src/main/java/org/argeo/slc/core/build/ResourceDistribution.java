package org.argeo.slc.core.build;

import org.argeo.slc.build.Distribution;
import org.springframework.core.io.Resource;

public class ResourceDistribution implements Distribution {
	private Resource location;

	public ResourceDistribution() {
	}

	public ResourceDistribution(Resource location) {
		this.location = location;
	}

	public String getDistributionId() {
		return location.toString();
	}

	public Resource getLocation() {
		return location;
	}

	public void setLocation(Resource location) {
		this.location = location;
	}

}
