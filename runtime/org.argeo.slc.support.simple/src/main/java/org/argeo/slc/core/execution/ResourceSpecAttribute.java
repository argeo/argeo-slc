package org.argeo.slc.core.execution;

import java.io.IOException;

import org.argeo.slc.SlcException;
import org.springframework.core.io.Resource;

// TODO: offer the ability to read the resource
public class ResourceSpecAttribute extends AbstractSpecAttribute {
	public final static String TYPE_PATH = "path";
	public final static String TYPE_URL = "url";
	public final static String TYPE_STREAM = "stream";

	private Resource resource;
	private String type = TYPE_PATH;

	public Object getValue() {
		return convertResource(resource);
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public Object convertResource(Resource resource) {
		try {
			if (TYPE_PATH.equals(type))
				return resource.getFile().getCanonicalPath();
			else if (TYPE_URL.equals(type))
				return resource.getURL().toString();
			else if (TYPE_STREAM.equals(type))
				return resource.getInputStream();
			else
				throw new SlcException("Unkown type " + type);
		} catch (IOException e) {
			throw new SlcException("Cannot convert resource " + resource, e);
		}
	}

	public void setType(String type) {
		this.type = type;
	}

}
