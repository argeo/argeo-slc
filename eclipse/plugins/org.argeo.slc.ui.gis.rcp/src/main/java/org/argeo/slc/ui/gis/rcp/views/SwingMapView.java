package org.argeo.slc.ui.gis.rcp.views;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import org.argeo.slc.geotools.map.OverlayLocationReceiver;
import org.argeo.slc.geotools.swing.VersatileZoomTool;
import org.argeo.slc.ui.gis.views.AbstractMapView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapPaneAdapter;
import org.geotools.swing.event.MapPaneEvent;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public class SwingMapView extends AbstractMapView implements
		OverlayLocationReceiver {
	public static final String ID = "org.argeo.slc.client.gis.views.GeoToolsMapView";

	private Composite embedded;
	private JMapPane mapPane;

	private VersatileZoomTool versatileZoomTool;

public void createPartControl(Composite parent) {
		embedded = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		Frame frame = SWT_AWT.new_Frame(embedded);

		mapPane = new JMapPane(new StreamingRenderer(), new DefaultMapContext());
		versatileZoomTool = new VersatileZoomTool();
		mapPane.setCursorTool(versatileZoomTool);

		mapPane.addMapPaneListener(new CustomMapPaneListener());

		frame.add(mapPane);
	}

	public void receiveOverlayLocation(final Point point, final Boolean stale) {
		final Point2D point2d = new DirectPosition2D(point.getCoordinate().x,
				point.getCoordinate().y);
		versatileZoomTool.setFieldPosition(point2d);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				AffineTransform tr = mapPane.getWorldToScreenTransform();
				// DirectPosition2D geoCoords = new DirectPosition2D(point
				// .getCoordinate().x, point.getCoordinate().y);
				DirectPosition2D geoCoords = new DirectPosition2D(point2d);
				if (tr == null)
					return;
				tr.transform(geoCoords, geoCoords);
				geoCoords.setCoordinateReferenceSystem(mapPane.getMapContext()
						.getCoordinateReferenceSystem());

				final int halfRefSize = 3;
				Rectangle rect = new Rectangle((int) Math.round(geoCoords
						.getX() - halfRefSize), (int) Math.round(geoCoords
						.getY() - halfRefSize), halfRefSize * 2 + 1,
						halfRefSize * 2 + 1);
				Graphics2D g2D = (Graphics2D) mapPane.getGraphics();
				if (g2D == null)
					return;
				g2D.setColor(Color.WHITE);
				if (stale)
					g2D.setXORMode(Color.ORANGE);
				else
					g2D.setXORMode(Color.RED);
				g2D.drawRect(rect.x, rect.y, rect.width, rect.height);
				g2D.drawRect(rect.x - 1, rect.y - 1, rect.width + 2,
						rect.height + 2);
			}
		});
	}

	protected void centerMap(Coordinate coordinate) {
		Envelope2D env = new Envelope2D();
		final double increment = 1d;
		env.setFrameFromDiagonal(coordinate.x - increment, coordinate.y
				- increment, coordinate.x + increment, coordinate.y + increment);
		mapPane.setDisplayArea(env);
	}

	public void setFocus() {
		if (embedded != null)
			embedded.setFocus();
	}
	

	@Override
	public MapContext getMapContext() {
		return mapPane.getMapContext();
	}

	protected void redrawOverlayLocation() {
		// FIXME: implement it
	}

	private class CustomMapPaneListener extends MapPaneAdapter {

		@Override
		public void onRenderingStopped(MapPaneEvent ev) {
			redrawOverlayLocation();
		}

		@Override
		public void onDisplayAreaChanged(MapPaneEvent ev) {
			redrawOverlayLocation();
		}

		@Override
		public void onRenderingProgress(MapPaneEvent ev) {
			redrawOverlayLocation();
		}

		@Override
		public void onRenderingStarted(MapPaneEvent ev) {
			redrawOverlayLocation();
		}

		@Override
		public void onResized(MapPaneEvent ev) {
			redrawOverlayLocation();
		}

	}

}