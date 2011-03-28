package org.argeo.slc.gpx.hibernate;

import org.hibernatespatial.HBSpatialExtension;
import org.hibernatespatial.SpatialDialect;
import org.hibernatespatial.cfg.HSConfiguration;

public class HibernateSpatialContext {
	private SpatialDialect defaultDialect;

	public void init() {
		HSConfiguration config = new HSConfiguration();
		// config.setDefaultDialect("org.hibernatespatial.postgis.PostgisDialect");
		HBSpatialExtension.setConfiguration(config);
		HBSpatialExtension.setDefaultSpatialDialect(defaultDialect);
	}

	public void setDefaultDialect(SpatialDialect defaultDialect) {
		this.defaultDialect = defaultDialect;
	}

}
