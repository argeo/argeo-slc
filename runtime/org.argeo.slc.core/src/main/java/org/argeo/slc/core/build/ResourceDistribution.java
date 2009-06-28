package org.argeo.slc.core.build;

import java.io.IOException;
import java.io.InputStream;

import org.argeo.slc.SlcException;
import org.argeo.slc.StreamReadable;
import org.argeo.slc.build.Distribution;
import org.springframework.core.io.Resource;

public class ResourceDistribution implements Distribution, StreamReadable {
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

	public InputStream getInputStream() {
		try {
			return location.getInputStream();
		} catch (IOException e) {
			throw new SlcException("Cannot get input stream", e);
		}
	}

}
