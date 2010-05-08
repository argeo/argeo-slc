package org.argeo.slc.geotools.swing;

import java.awt.Color;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.media.jai.JAI;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.deploy.DefaultResourceSet;
import org.argeo.slc.core.deploy.ResourceSet;
import org.argeo.slc.geotools.BeanFeatureTypeBuilder;
import org.argeo.slc.gis.model.Position;
import org.argeo.slc.jts.PositionProvider;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.WorldFileReader;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.gce.image.WorldImageFormat;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.DefaultMapLayer;
import org.geotools.map.MapContext;
import org.geotools.map.MapLayer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.JMapPane;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

/**
 * GeoTools Quickstart demo application. Prompts the user for a shapefile and
 * displays its contents on the screen in a map frame
 * 
 * @source $URL:
 *         http://svn.osgeo.org/geotools/trunk/demo/example/src/main/java/org
 *         /geotools/demo/Quickstart.java $
 */
public class GisFieldViewer implements InitializingBean {
	private final static Log log = LogFactory.getLog(GisFieldViewer.class);

	protected final static BeanFeatureTypeBuilder<Position> POSITION = new BeanFeatureTypeBuilder<Position>(
			Position.class);

	private DateFormat fieldPositionDateFormat = new SimpleDateFormat(
			"yyyyMMdd-HHmmss");

	private PositionProvider positionProvider;

	private ClassLoader jaiImageIoClassLoader;

	private ResourceSet vectors = new DefaultResourceSet();
	private ResourceSet rasters = new DefaultResourceSet();

	private JMapPane mapPane;

	/** in s */
	private Integer positionRefreshPeriod = 10;

	public static void main(String[] args) throws Exception {
		new GisFieldViewer().afterPropertiesSet();
	}

	public void afterPropertiesSet() {

		// Create map context
		MapContext mapContext = new DefaultMapContext();
		mapContext.setTitle("GIS Field Viewer");

		// Now display the map
		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		final JMapFrame frame = new JMapFrame(mapContext);
		frame.enableStatusBar(true);
		frame.enableToolBar(false);
		frame.enableLayerTable(true);
		frame.initComponents();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		frame.setSize(800, 600);

		mapPane = frame.getMapPane();
		mapPane.setCursorTool(new VersatileZoomTool());

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});

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
				mapContext.addLayer(FileDataStoreFinder.getDataStore(
						vector.getURL()).getFeatureSource(), null);
			} catch (Exception e) {
				log.error("Could not load vector " + vector + ": " + e);
				if (log.isTraceEnabled())
					log.trace("Stack", e);
			}
		}

		new Thread(new PositionUpdater()).start();
	}

	private void prepareJaiForRasters() {
		if (jaiImageIoClassLoader != null) {
			Thread.currentThread().setContextClassLoader(jaiImageIoClassLoader);
			try {
				JAI.getDefaultInstance().getOperationRegistry()
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
				Position currentPosition = positionProvider.currentPosition();

				if (mapPane.getDisplayArea().contains(
						currentPosition.getLocation().getCoordinate())) {
					SimpleFeature feature = POSITION
							.buildFeature(currentPosition);
					FeatureCollection<SimpleFeatureType, SimpleFeature> collection = new DefaultFeatureCollection(
							"Field Position "
									+ fieldPositionDateFormat
											.format(currentPosition
													.getTimestamp()), POSITION
									.getFeatureType());
					collection.add(feature);
					if (mapLayer != null)
						mapPane.getMapContext().removeLayer(mapLayer);
					Style style = SLD.createSimpleStyle(POSITION
							.getFeatureType(), Color.RED);
					mapLayer = new DefaultMapLayer(collection, style, "");
					mapPane.getMapContext().addLayer(mapLayer);
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

