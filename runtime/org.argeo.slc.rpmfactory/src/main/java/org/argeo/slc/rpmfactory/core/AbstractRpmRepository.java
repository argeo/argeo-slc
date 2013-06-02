package org.argeo.slc.rpmfactory.core;

import org.argeo.slc.rpmfactory.RpmRepository;

/** Common method to RPM repositories. */
public abstract class AbstractRpmRepository implements RpmRepository {
	private String id;
	private String url;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getUrl() {
		return url;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
