package org.argeo.slc.jts;

import org.argeo.slc.gis.model.FieldPosition;

public interface PositionProvider {
	/** @return the position or null if it cannot be retrieved */
	public FieldPosition currentPosition();
}
