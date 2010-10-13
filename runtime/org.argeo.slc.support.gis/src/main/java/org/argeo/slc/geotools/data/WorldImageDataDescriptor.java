package org.argeo.slc.geotools.data;

import java.io.IOException;

import org.argeo.slc.geotools.AbstractDataDescriptor;
import org.springframework.core.io.Resource;

public class WorldImageDataDescriptor extends AbstractDataDescriptor {
	private final Resource resource;

	public WorldImageDataDescriptor(Resource resource) throws IOException {
		this.resource = resource;
	}

	public Resource getResource() {
		return resource;
	}

	@Override
	public String toString() {
		return resource.toString();
	}

}
