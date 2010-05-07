/*
 *    GeoTools - The Open Source Java GIS Tookit
 *    http://geotools.org
 *
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This file is hereby placed into the Public Domain. This means anyone is
 *    free to do whatever they wish with this file. Use it well and enjoy!
 */
package org.argeo.slc.geotools;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.media.jai.JAI;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.geotools.swing.VersatileZoomTool;
import org.argeo.slc.jts.PositionProvider;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.WorldFileReader;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.gce.image.WorldImageFormat;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.DefaultMapLayer;
import org.geotools.map.MapContext;
import org.geotools.map.MapLayer;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.JMapPane;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Point;

/**
 * GeoTools Quickstart demo application. Prompts the user for a shapefile and
 * displays its contents on the screen in a map frame
 * 
 * @source $URL:
 *         http://svn.osgeo.org/geotools/trunk/demo/example/src/main/java/org
 *         /geotools/demo/Quickstart.java $
 */
public class SimpleGisFieldViewer implements Runnable {
	private final static Log log = LogFactory
			.getLog(SimpleGisFieldViewer.class);

	private PositionProvider positionProvider;

	private ClassLoader jaiImageIoClassLoader;

	public static void main(String[] args) throws Exception {
		new SimpleGisFieldViewer().run();
	}

	public void run() {
		Iterator<FileDataStoreFactorySpi> ps = FileDataStoreFinder
				.getAvailableDataStores();
		log.debug("Available datastores:");
		while (ps.hasNext()) {
			log.debug(ps.next());
		}

		// display a data store file chooser dialog for shapefiles
		// File file = JFileDataStoreChooser.showOpenFile("shp", null);
		// if (file == null) {
		// return;
		// }
		File dir = new File(
				"/home/mbaudier/gis/projects/100122-EasternBalkans2010/data");

		// FeatureSource<SimpleFeatureType, SimpleFeature> featureSource =
		// FileDataStoreFinder
		// .getDataStore(new File(dir, "countries-EuroMed-NEarth.shp"))
		// .getFeatureSource();

		// Create a map context and add our shapefile to it
		MapContext mapContext = new DefaultMapContext();
		mapContext.setTitle("Quickstart");

		// Now display the map
		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		final JMapFrame frame = new JMapFrame(mapContext);
		frame.enableStatusBar(true);
		frame.enableToolBar(false);
		frame.enableLayerTable(false);
		frame.initComponents();

		frame.setSize(800, 600);

		final JMapPane mapPane = frame.getMapPane();
		mapPane.setCursorTool(new VersatileZoomTool());

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});

		// Create position type
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setName("Position");
		builder.setNamespaceURI("http://localhost/");
		builder.setCRS(DefaultGeographicCRS.WGS84);

		// add attributes in order
		builder.add("Location", Point.class);
		builder.add("ID", Integer.class);
		builder.add("Name", String.class);

		// build the type
		final SimpleFeatureType POSITION = builder.buildFeatureType();

		// PositionProvider positionProvider = new GpsBabelPositionProvider();

		try {
			// FeatureSource<SimpleFeatureType, SimpleFeature> fs =
			// FileDataStoreFinder
			// .getDataStore(new File(dir, "countries-EuroMed-NEarth.shp"))
			// .getFeatureSource();

			// mapContext.addLayer(FileDataStoreFinder.getDataStore(
			// new File(dir, "cities-EuroMed-NEarth.shp")).getFeatureSource(),
			// null);

			// Raster
			if (jaiImageIoClassLoader != null) {
				Thread.currentThread().setContextClassLoader(
						jaiImageIoClassLoader);
				JAI.getDefaultInstance().getOperationRegistry()
						.registerServices(
								WorldFileReader.class.getClassLoader());
//				OperationDescriptor odesc = (OperationDescriptor) JAI
//						.getDefaultInstance().getOperationRegistry()
//						.getDescriptor("rendered", "ImageRead");
			}

			// Raster style
			StyleBuilder styleBuilder = new StyleBuilder();
			RasterSymbolizer rasterSymbolizer = styleBuilder
					.createRasterSymbolizer();
			rasterSymbolizer.setGeometryPropertyName("geom");
			Style rasterStyle = styleBuilder.createStyle(rasterSymbolizer);
			WorldImageFormat worldImageFormat = new WorldImageFormat();

			File rasterDir = new File("/home/mbaudier/gis/data/100501-Poehali");
			mapContext.addLayer(worldImageFormat.getReader(
					new File(rasterDir, "500k--l36-1--(1984).gif")).read(null),
					rasterStyle);
			mapContext.addLayer(worldImageFormat.getReader(
					new File(rasterDir, "500k--l35-4--(1978).gif")).read(null),
					rasterStyle);
			mapContext.addLayer(worldImageFormat.getReader(
					new File(rasterDir, "500k--l35-2--(1980).gif")).read(null),
					rasterStyle);
			mapContext.addLayer(worldImageFormat.getReader(
					new File(rasterDir, "100k--l36-050--(1982).gif")).read(null),
					rasterStyle);

			mapContext.addLayer(FileDataStoreFinder.getDataStore(
					new File(dir, "countries-EuroMed-NEarth.shp"))
					.getFeatureSource(), null);
			// mapContext.addLayer(FileDataStoreFinder.getDataStore(
			// new File(dir, "highways-EastBalkan-OSM.shp"))
			// .getFeatureSource(), null);
		} catch (IOException e1) {
			throw new SlcException("Cannot load sta stores", e1);
		}

		MapLayer mapLayer = null;
		while (true) {
			SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(
					POSITION);

			// add the attributes
			featureBuilder.add(positionProvider.currentPosition());
			featureBuilder.add(12);
			featureBuilder.add("My Name");

			// build the feature
			SimpleFeature feature = featureBuilder.buildFeature("Flag.12");
			FeatureCollection<SimpleFeatureType, SimpleFeature> collection = new DefaultFeatureCollection(
					"testCollection", POSITION);
			collection.add(feature);
			if (mapLayer != null)
				mapContext.removeLayer(mapLayer);
			Style style = SLD.createSimpleStyle(POSITION, Color.RED);
			mapLayer = new DefaultMapLayer(collection, style, "");
			mapContext.addLayer(mapLayer);
			// mapContext.addLayer(collection,null);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void setPositionProvider(PositionProvider positionProvider) {
		this.positionProvider = positionProvider;
	}

	public void setJaiImageIoClassLoader(ClassLoader classLoader) {
		this.jaiImageIoClassLoader = classLoader;
	}

}