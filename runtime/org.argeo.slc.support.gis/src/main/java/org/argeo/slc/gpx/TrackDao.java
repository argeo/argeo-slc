package org.argeo.slc.gpx;

import java.io.InputStream;

public interface TrackDao {
	public Object importTrackPoints(String source, String sensor, InputStream in);
}
