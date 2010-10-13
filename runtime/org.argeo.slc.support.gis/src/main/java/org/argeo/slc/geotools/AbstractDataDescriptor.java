package org.argeo.slc.geotools;

import java.util.UUID;

public abstract class AbstractDataDescriptor implements DataDescriptor{
	private String id;
	private Boolean isLoaded;

	public AbstractDataDescriptor() {
		this.id = UUID.randomUUID().toString();
		this.isLoaded = false;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean getIsLoaded() {
		return isLoaded;
	}

	public void setIsLoaded(Boolean isLoaded) {
		this.isLoaded = isLoaded;
	}

}
