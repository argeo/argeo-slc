/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.argeo.slc.geotools.swing;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.media.jai.JAI;
import javax.sql.DataSource;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.deploy.DefaultResourceSet;
import org.argeo.slc.core.deploy.ResourceSet;
import org.argeo.slc.geotools.BeanFeatureTypeBuilder;
import org.argeo.slc.gis.model.FieldPosition;
import org.argeo.slc.jts.PositionProvider;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.WorldFileReader;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.gce.image.WorldImageFormat;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.map.MapLayer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapPaneAdapter;
import org.geotools.swing.event.MapPaneEvent;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * GeoTools Quickstart demo application. Prompts the user for a shapefile and
 * displays its contents on the screen in a map frame
 * 
 * @source $URL:
 *         http://svn.osgeo.org/geotools/trunk/demo/example/src/main/java/org
 *         /geotools/demo/Quickstart.java $
 */
public class GisFieldViewer implements InitializingBean, DisposableBean {
	private final static Log log = LogFactory.getLog(GisFieldViewer.class);

	protected final static BeanFeatureTypeBuilder<FieldPosition> POSITION = new BeanFeatureTypeBuilder<FieldPosition>(
			FieldPosition.class);

	static StyleFactory styleFactory = CommonFactoryFinder
			.getStyleFactory(null);
	static FilterFactory filterFactory = CommonFactoryFinder
			.getFilterFactory(null);

	private DateFormat fieldPositionDateFormat = new SimpleDateFormat(
			"yyyyMMdd-HHmmss");

	private PositionProvider positionProvider;

	private ClassLoader jaiImageIoClassLoader;

	private ResourceSet vectors = new DefaultResourceSet();
	private ResourceSet rasters = new DefaultResourceSet();

	private JMapPane mapPane;
	private JMapFrame mapFrame;
	private Frame awtFrame = null;

	/** in s */
	private Integer positionRefreshPeriod = 1;

	private FieldPosition currentPosition = null;
	private Boolean positionProviderDisconnected = false;
	private VersatileZoomTool versatileZoomTool;

	private DataSource dataSource;

	private DataStore postGisDataStore;

	public GisFieldViewer() {
		super();
	}

	public GisFieldViewer(Frame awtFrame) {
		this.awtFrame = awtFrame;
	}

	public static void main(String[] args) throws Exception {
		new GisFieldViewer().afterPropertiesSet();
	}

	public void afterPropertiesSet() {
		new Thread(new PositionUpdater()).start();

		// CoordinateReferenceSystem sphericalMercator;
		// try {
		// sphericalMercator = CRS.decode("EPSG:3785");
		// // sphericalMercator = CRS.decode("EPSG:900913");
		// } catch (Exception e1) {
		// throw new SlcException("Cannot create CRS", e1);
		// }

		// Create map context
		// MapContext mapContext = new DefaultMapContext(sphericalMercator);
		MapContext mapContext = new DefaultMapContext();
		mapContext.setTitle("GIS Field Viewer");

		if (awtFrame == null) {
			// Now display the map
			// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			mapFrame = new JMapFrame(mapContext);
			mapFrame.enableStatusBar(true);
			mapFrame.enableToolBar(false);
			mapFrame.enableLayerTable(true);
			mapFrame.initComponents();
			mapFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

			mapFrame.setSize(800, 600);

			mapPane = mapFrame.getMapPane();
		} else {
			mapPane = new JMapPane(new StreamingRenderer(), mapContext);
			awtFrame.add(mapPane);
		}

		// ReferencedEnvelope referencedEnvelope = new
		// ReferencedEnvelope(sphericalMercator);
		// mapFrame.getMapContext().setAreaOfInterest(referencedEnvelope);
		versatileZoomTool = new VersatileZoomTool();
		mapPane.setCursorTool(versatileZoomTool);

		mapPane.addMapPaneListener(new CustomMapPaneListener());

		if (mapFrame != null)
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					// Fullscreen
					// GraphicsEnvironment ge = GraphicsEnvironment
					// .getLocalGraphicsEnvironment();
					// GraphicsDevice[] devices = ge.getScreenDevices();
					// if (devices.length < 1)
					// throw new RuntimeException("No device available");
					// GraphicsDevice gd = devices[0];
					// mapFrame.setUndecorated(true);
					// // http://ubuntuforums.org/showthread.php?t=820924
					// mapFrame.setResizable(true);
					// gd.setFullScreenWindow(mapFrame);
					mapFrame.setVisible(true);
				}
			});

		// Center on position in order to facten first rendering
		centerOnPosition();

		// Rasters
		prepareJaiForRasters();
		StyleBuilder styleBuilder = new StyleBuilder();
		RasterSymbolizer rs = styleBuilder.createRasterSymbolizer();
		rs.setGeometryPropertyName("geom");
		Style rasterStyle = styleBuilder.createStyle(rs);
		WorldImageFormat worldImageFormat = new WorldImageFormat();

		for (Resource raster : rasters.listResources().values()) {
			try {
				mapContext.addLayer(worldImageFormat.getReader(raster.getURL())
						.read(null), rasterStyle);
			} catch (Exception e) {
				log.error("Could not load raster " + raster + ": " + e);
				if (log.isTraceEnabled())
					log.trace("Stack", e);
			}
		}

		// Vectors
		for (Resource vector : vectors.listResources().values()) {
			try {
				mapContext.addLayer(
						FileDataStoreFinder.getDataStore(vector.getURL())
								.getFeatureSource(), null);
			} catch (Exception e) {
				log.error("Could not load vector " + vector + ": " + e);
				if (log.isTraceEnabled())
					log.trace("Stack", e);
			}
		}

		try {
			Map params = new HashMap();
			// params.put("dbtype", "postgis");
			// params.put("host", "air");
			// params.put("port", new Integer(5432));
			// params.put("database", "test_berlin");
			// params.put("user", "argeo");
			// params.put("passwd", "argeo");
			//
			// DataStore pgDatastore = DataStoreFinder.getDataStore(params);

			PostgisNGDataStoreFactory factory = new PostgisNGDataStoreFactory();
			// JDBCDataStore pgDatastore = new JDBCDataStore();
			// pgDatastore.setDataSource(dataSource);
			// pgDatastore.setSQLDialect(new PostGISDialect(pgDatastore));

			params.put(PostgisNGDataStoreFactory.DATASOURCE.key, dataSource);

			//JDBCDataStore pgDatastore = factory.createDataStore(params);

			FeatureSource<SimpleFeatureType, SimpleFeature> source = postGisDataStore
					.getFeatureSource("ways");
			// log.debug("source CRS: "+source.getBounds().getCoordinateReferenceSystem());
			// log.debug("context CRS: "+mapContext.getCoordinateReferenceSystem());

			mapContext.addLayer(source, createLineStyle());
		} catch (Exception e) {
			log.error("Could not load db " + "" + ": " + e);
			e.printStackTrace();
			if (log.isTraceEnabled())
				log.trace("Stack", e);
		}

	}

	private Style createLineStyle() {
		Stroke stroke = styleFactory.createStroke(
				filterFactory.literal(Color.BLUE), filterFactory.literal(1));

		/*
		 * Setting the geometryPropertyName arg to null signals that we want to
		 * draw the default geomettry of features
		 */
		LineSymbolizer sym = styleFactory.createLineSymbolizer(stroke, null);

		Rule rule = styleFactory.createRule();
		rule.symbolizers().add(sym);
		FeatureTypeStyle fts = styleFactory
				.createFeatureTypeStyle(new Rule[] { rule });
		Style style = styleFactory.createStyle();
		style.featureTypeStyles().add(fts);

		return style;
	}

	public void destroy() throws Exception {
		if (mapFrame != null)
			mapFrame.dispose();
	}

	private void prepareJaiForRasters() {
		if (jaiImageIoClassLoader != null) {
			Thread.currentThread().setContextClassLoader(jaiImageIoClassLoader);
			try {
				JAI.getDefaultInstance()
						.getOperationRegistry()
						.registerServices(
								WorldFileReader.class.getClassLoader());
			} catch (IOException e) {
				e.printStackTrace();
			}
			// OperationDescriptor odesc = (OperationDescriptor) JAI
			// .getDefaultInstance().getOperationRegistry()
			// .getDescriptor("rendered", "ImageRead");
		}
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

	protected JMapPane getMapPane() {
		return mapPane;
	}

	public void setPositionProvider(PositionProvider positionProvider) {
		this.positionProvider = positionProvider;
	}

	public void setJaiImageIoClassLoader(ClassLoader classLoader) {
		this.jaiImageIoClassLoader = classLoader;
	}

	public void setVectors(ResourceSet vectors) {
		this.vectors = vectors;
	}

	public void setRasters(ResourceSet rasters) {
		this.rasters = rasters;
	}

	private class PositionUpdater implements Runnable {

		public void run() {
			MapLayer mapLayer = null;
			while (true) {
				FieldPosition currentPosition = positionProvider
						.currentPosition();

				receiveNewPosition(currentPosition);
				// versatileZoomTool.setFieldPosition(new
				// Point2D(currentPosition
				// .getLocation().getX(), currentPosition.getLocation()
				// .getY()));

				if (currentPosition != null) {

					// if (mapPane.getDisplayArea().contains(
					// currentPosition.getLocation().getCoordinate())) {
					// SimpleFeature feature = POSITION
					// .buildFeature(currentPosition);
					// FeatureCollection<SimpleFeatureType, SimpleFeature>
					// collection = new DefaultFeatureCollection(
					// "Field Position "
					// + fieldPositionDateFormat
					// .format(currentPosition
					// .getTimestamp()),
					// POSITION.getFeatureType());
					// collection.add(feature);
					// if (mapLayer != null)
					// mapPane.getMapContext().removeLayer(mapLayer);
					// Style style = SLD.createSimpleStyle(POSITION
					// .getFeatureType(), Color.RED);
					// mapLayer = new DefaultMapLayer(collection, style, "");
					// mapPane.getMapContext().addLayer(mapLayer);
					// }
				}

				try {
					Thread.sleep(positionRefreshPeriod * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
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

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setPostGisDataStore(DataStore postGisDataStore) {
		this.postGisDataStore = postGisDataStore;
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
}

// File rasterDir = new
// File("/home/mbaudier/gis/data/100501-Poehali");
// mapContext.addLayer(worldImageFormat.getReader(
// new File(rasterDir, "500k--l36-1--(1984).gif")).read(null),
// rasterStyle);
// mapContext.addLayer(worldImageFormat.getReader(
// new File(rasterDir, "500k--l35-4--(1978).gif")).read(null),
// rasterStyle);
// mapContext.addLayer(worldImageFormat.getReader(
// new File(rasterDir, "500k--l35-2--(1980).gif")).read(null),
// rasterStyle);
// mapContext.addLayer(worldImageFormat.getReader(
// new File(rasterDir, "100k--l36-050--(1982).gif"))
// .read(null), rasterStyle);

