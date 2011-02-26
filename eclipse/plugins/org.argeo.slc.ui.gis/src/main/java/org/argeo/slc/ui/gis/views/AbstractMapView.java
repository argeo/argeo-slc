package org.argeo.slc.ui.gis.views;

import org.eclipse.ui.part.ViewPart;
import org.geotools.map.MapContext;

import com.vividsolutions.jts.geom.Coordinate;

public abstract class AbstractMapView extends ViewPart {
	public final static String ID = "org.argeo.slc.ui.gis.mapView";
//	private final static Log log = LogFactory.getLog(AbstractMapView.class);

//	private MapContext mapContext;

//	protected abstract void createMapWidget(Composite parent,
//			MapContext mapContext);
	
	public abstract MapContext getMapContext();

//	protected abstract void drawOverlayLocation(Point point, Boolean stale);

//	protected abstract void centerMap(Coordinate coordinate);

//	public void createPartControl(Composite parent) {
//		createMapWidget(parent, mapContext);
//
//		// center on position
////		if (currentPosition != null)
////			centerMap(currentPosition.getLocation().getCoordinate());
//
//	}
//	public void setMapContext(MapContext mapContext) {
//		this.mapContext = mapContext;
//	}
}