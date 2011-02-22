package org.argeo.slc.client.gis.views;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.geotools.Backend;
import org.argeo.slc.geotools.DataDescriptor;
import org.argeo.slc.geotools.data.FeatureSourceDataDescriptor;
import org.argeo.slc.geotools.data.PostgisDataDescriptor;
import org.argeo.slc.geotools.data.WorldImageDataDescriptor;
import org.argeo.slc.geotools.swing.VersatileZoomTool;
import org.argeo.slc.gis.model.FieldPosition;
import org.argeo.slc.jts.PositionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.map.MapContext;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapPaneAdapter;
import org.geotools.swing.event.MapPaneEvent;

import com.vividsolutions.jts.geom.Coordinate;

public class GeoToolsMapView extends ViewPart {
	public static final String ID = "org.argeo.slc.client.gis.views.GeoToolsMapView";
	private final static Log log = LogFactory.getLog(GeoToolsMapView.class);

	private Composite embedded;

	private PositionProvider positionProvider;

	private Backend backend;

	private MapContext mapContext;
	private JMapPane mapPane;

	/** in s */
	private Integer positionRefreshPeriod = 1;
	private FieldPosition currentPosition = null;
	private Boolean positionProviderDisconnected = false;
	private VersatileZoomTool versatileZoomTool;

	public void createPartControl(Composite parent) {
		embedded = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		Frame frame = SWT_AWT.new_Frame(embedded);

		mapPane = new JMapPane(new StreamingRenderer(), mapContext);
		versatileZoomTool = new VersatileZoomTool();
		mapPane.setCursorTool(versatileZoomTool);

		mapPane.addMapPaneListener(new CustomMapPaneListener());

		frame.add(mapPane);

		centerOnPosition();

		for (DataDescriptor dd : backend.getAvailableData(null)) {
			if (dd instanceof WorldImageDataDescriptor) {
				StyleBuilder styleBuilder = new StyleBuilder();
				RasterSymbolizer rs = styleBuilder.createRasterSymbolizer();
				rs.setGeometryPropertyName("geom");
				Style rasterStyle = styleBuilder.createStyle(rs);
				try {
					mapContext.addLayer(backend.loadGridCoverage(dd),
							rasterStyle);
				} catch (Exception e) {
					log.warn(e);
				}

			} else if (dd instanceof FeatureSourceDataDescriptor) {
				try {
					mapContext.addLayer(backend.loadFeatureSource(dd), null);
				} catch (Exception e) {
					log.warn(e);
				}
			} else if (dd instanceof PostgisDataDescriptor) {
				// DataStore dataStore = backend.loadDataStore(dd);
				for (DataDescriptor dd2 : backend.getAvailableData(dd))
					mapContext.addLayer(backend.loadFeatureSource(dd2), null);
			}
		}
		// GisFieldViewer gisFieldViewer = new GisFieldViewer(frame);
		// gisFieldViewer.setPostGisDataStore(postGisDataStore);
		// gisFieldViewer.setPositionProvider(positionProvider);
		// gisFieldViewer.setJaiImageIoClassLoader(jaiImageIoClassLoader);
		// gisFieldViewer.afterPropertiesSet();

		new Thread(new PositionUpdater()).start();

	}

	protected void receiveNewPosition(FieldPosition position) {
		if (position != null && versatileZoomTool != null) {
			positionProviderDisconnected = false;
			currentPosition = position;
			Point2D point2d = new DirectPosition2D(currentPosition
					.getLocation().getCoordinate().x, currentPosition
					.getLocation().getCoordinate().y);
			versatileZoomTool.setFieldPosition(point2d);
			drawPositionLocation();
		} else {
			positionProviderDisconnected = true;
		}
	}

	protected void drawPositionLocation() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (currentPosition == null)
					return;

				AffineTransform tr = getMapPane().getWorldToScreenTransform();
				DirectPosition2D geoCoords = new DirectPosition2D(
						currentPosition.getLocation().getCoordinate().x,
						currentPosition.getLocation().getCoordinate().y);
				if (tr == null)
					return;
				tr.transform(geoCoords, geoCoords);
				geoCoords.setCoordinateReferenceSystem(getMapPane()
						.getMapContext().getCoordinateReferenceSystem());

				final int halfRefSize = 3;
				Rectangle rect = new Rectangle((int) Math.round(geoCoords
						.getX() - halfRefSize), (int) Math.round(geoCoords
						.getY() - halfRefSize), halfRefSize * 2 + 1,
						halfRefSize * 2 + 1);
				Graphics2D g2D = (Graphics2D) getMapPane().getGraphics();
				if (g2D == null)
					return;
				g2D.setColor(Color.WHITE);
				if (positionProviderDisconnected)
					g2D.setXORMode(Color.ORANGE);
				else
					g2D.setXORMode(Color.RED);
				g2D.drawRect(rect.x, rect.y, rect.width, rect.height);
				g2D.drawRect(rect.x - 1, rect.y - 1, rect.width + 2,
						rect.height + 2);
			}
		});
	}

	protected void centerOnPosition() {
		if (currentPosition == null)
			return;
		Envelope2D env = new Envelope2D();
		final double increment = 1d;
		Coordinate positionCoo = currentPosition.getLocation().getCoordinate();
		env.setFrameFromDiagonal(positionCoo.x - increment, positionCoo.y
				- increment, positionCoo.x + increment, positionCoo.y
				+ increment);
		getMapPane().setDisplayArea(env);

	}

	public void setFocus() {
		if (embedded != null)
			embedded.setFocus();
	}

	protected JMapPane getMapPane() {
		return mapPane;
	}

	public void setPositionProvider(PositionProvider positionProvider) {
		this.positionProvider = positionProvider;
	}

	public void setBackend(Backend backend) {
		this.backend = backend;
	}

	public void setMapContext(MapContext mapContext) {
		this.mapContext = mapContext;
	}

	private class CustomMapPaneListener extends MapPaneAdapter {

		@Override
		public void onRenderingStopped(MapPaneEvent ev) {
			drawPositionLocation();
		}

		@Override
		public void onDisplayAreaChanged(MapPaneEvent ev) {
			drawPositionLocation();
		}

		@Override
		public void onRenderingProgress(MapPaneEvent ev) {
			drawPositionLocation();
		}

		@Override
		public void onRenderingStarted(MapPaneEvent ev) {
			drawPositionLocation();
		}

		@Override
		public void onResized(MapPaneEvent ev) {
			drawPositionLocation();
		}

	}

	private class PositionUpdater implements Runnable {

		public void run() {
			while (true) {
				FieldPosition currentPosition = positionProvider
						.currentPosition();

				receiveNewPosition(currentPosition);

				if (currentPosition != null) {
				}

				try {
					Thread.sleep(positionRefreshPeriod * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}