package org.argeo.cms.worldwind;

import java.util.Objects;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Box;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.view.orbit.OrbitView;

public class WorldWindUtils {
	public static void zoomTo(WorldWindow wwd, Sector sector, boolean goTo) {
		Objects.requireNonNull(wwd);
		Objects.requireNonNull(sector);

		// Create a bounding box for the specified sector in order to estimate its size
		// in model coordinates.
		Box extent = Sector.computeBoundingBox(wwd.getModel().getGlobe(),
				wwd.getSceneController().getVerticalExaggeration(), sector);

		// Estimate the distance between the center position and the eye position that
		// is necessary to cause the sector to
		// fill a viewport with the specified field of view. Note that we change the
		// distance between the center and eye
		// position here, and leave the field of view constant.
		Angle fov = wwd.getView().getFieldOfView();
		double zoom = extent.getRadius() / fov.cosHalfAngle() / fov.tanHalfAngle();

		if (goTo) {
			wwd.getView().goTo(new Position(sector.getCentroid(), 0d), zoom);
		} else {
			OrbitView orbitView = (OrbitView) wwd.getView();
			orbitView.setCenterPosition(new Position(sector.getCentroid(), 0d));
			orbitView.setZoom(zoom);
		}
	}

}
