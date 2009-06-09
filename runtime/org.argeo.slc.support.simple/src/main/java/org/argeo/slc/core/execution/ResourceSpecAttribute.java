package org.argeo.slc.core.execution;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.springframework.core.io.Resource;

/** @deprecated */
public class ResourceSpecAttribute extends AbstractSpecAttribute {
	public final static String TYPE_PATH = "path";
	public final static String TYPE_URL = "url";
	public final static String TYPE_STREAM = "stream";

	private Resource resource;
	private String type = TYPE_PATH;

	private final static Log log = LogFactory
			.getLog(ResourceSpecAttribute.class);

	public ResourceSpecAttribute() {
		log
				.warn(getClass()
						+ " is deprecated and will soon be removed. Please use slcDefault.executionResources instead.");
	}

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

	public String getType() {
		return type;
	}

}
