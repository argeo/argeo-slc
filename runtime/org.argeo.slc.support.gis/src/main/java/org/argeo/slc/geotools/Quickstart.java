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
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.gpsbabel.GpsBabelPositionProvider;
import org.argeo.slc.jts.PositionProvider;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.DefaultMapLayer;
import org.geotools.map.MapContext;
import org.geotools.map.MapLayer;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
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
public class Quickstart {
	private final static Log log = LogFactory.getLog(Quickstart.class);

	/**
	 * GeoTools Quickstart demo application. Prompts the user for a shapefile
	 * and displays its contents on the screen in a map frame
	 */
	public static void main(String[] args) throws Exception {
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
		frame.enableToolBar(true);
		frame.enableLayerTable(true);
		frame.initComponents();

		frame.setSize(800, 600);

		final double clickToZoom = 0.1; // 1 wheel click is 10% zoom
		final JMapPane mapPane = frame.getMapPane();
		mapPane.addMouseWheelListener(new MouseWheelListener() {

			public void mouseWheelMoved(MouseWheelEvent ev) {
				int clicks = ev.getWheelRotation();
				// -ve means wheel moved up, +ve means down
				int sign = (clicks < 0 ? -1 : 1);

				ReferencedEnvelope env = mapPane.getDisplayArea();
				double width = env.getWidth();
				double delta = width * clickToZoom * sign;

				env.expandBy(delta);
				mapPane.setDisplayArea(env);
				mapPane.repaint();
			}
		});
		// mapPane.addMouseListener(new MapMouseAdapter() {
		// // wheel event handler
		// public void handleMouseWheelEvent(MouseWheelEvent ev) {
		// int clicks = ev.getWheelRotation();
		// // -ve means wheel moved up, +ve means down
		// int sign = (clicks < 0 ? -1 : 1);
		//
		// ReferencedEnvelope env = mapPane.getDisplayArea();
		// double width = env.getWidth();
		// double delta = width * clickToZoom * sign;
		//
		// env.expandBy(delta);
		// mapPane.setDisplayArea(env);
		// mapPane.repaint();
		// }
		// });

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

		PositionProvider positionProvider = new GpsBabelPositionProvider();

		// FeatureSource<SimpleFeatureType, SimpleFeature> fs =
		// FileDataStoreFinder
		// .getDataStore(new File(dir, "countries-EuroMed-NEarth.shp"))
		// .getFeatureSource();

		// mapContext.addLayer(FileDataStoreFinder.getDataStore(
		// new File(dir, "cities-EuroMed-NEarth.shp")).getFeatureSource(),
		// null);
		mapContext.addLayer(FileDataStoreFinder.getDataStore(
				new File(dir, "countries-EuroMed-NEarth.shp"))
				.getFeatureSource(), null);
		 mapContext.addLayer(FileDataStoreFinder.getDataStore(
		 new File(dir, "highways-EastBalkan-OSM.shp"))
		 .getFeatureSource(), null);

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

			Thread.sleep(1000);
		}
	}

}