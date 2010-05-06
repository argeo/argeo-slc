package org.argeo.slc.jts;

import com.vividsolutions.jts.geom.Point;

public interface PositionProvider {
	public Point currentPosition();
}
