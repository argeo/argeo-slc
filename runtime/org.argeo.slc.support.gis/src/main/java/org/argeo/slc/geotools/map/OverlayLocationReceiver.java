package org.argeo.slc.geotools.map;

import com.vividsolutions.jts.geom.Point;

public interface OverlayLocationReceiver {
	public void receiveOverlayLocation(Point point, Boolean stale);
}
