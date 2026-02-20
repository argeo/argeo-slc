package org.argeo.cms.geo.ux.js;

import org.argeo.api.js.ol.Source;

public class SentinelCloudless extends Source {

	public SentinelCloudless(Object... args) {
		super(args);
	}

	@Override
	public String getJsPackage() {
		return "argeo.app.geo";
	}

}
