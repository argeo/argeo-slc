package org.argeo.slc.core.build;

import java.io.IOException;
import java.io.InputStream;

import org.argeo.slc.SlcException;
import org.argeo.slc.StreamReadable;
import org.argeo.slc.build.Distribution;
import org.springframework.core.io.Resource;

/** A software distribution archive accessible via a {@link Resource}. */
public class ResourceDistribution implements Distribution, StreamReadable {
	private Resource resource;

	public ResourceDistribution() {
	}

	public ResourceDistribution(Resource location) {
		this.resource = location;
	}

	public String getDistributionId() {
		return resource.toString();
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public InputStream getInputStream() {
		try {
			return resource.getInputStream();
		} catch (IOException e) {
			throw new SlcException("Cannot get input stream", e);
		}
	}

	@Override
	public String toString() {
		return resource.toString();
	}

}
