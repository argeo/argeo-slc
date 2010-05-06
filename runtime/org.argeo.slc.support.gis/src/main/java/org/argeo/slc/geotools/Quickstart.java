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

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFactorySpi;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.JMapPane;
import org.geotools.swing.event.MapMouseAdapter;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

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

		FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = FileDataStoreFinder
				.getDataStore(new File(dir, "countries-EuroMed-NEarth.shp"))
				.getFeatureSource();

		// Create a map context and add our shapefile to it
		MapContext map = new DefaultMapContext();
		map.setTitle("Quickstart");

		// Now display the map
		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		final JMapFrame frame = new JMapFrame(map);
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

		map.addLayer(FileDataStoreFinder.getDataStore(
				new File(dir, "cities-EuroMed-NEarth.shp")).getFeatureSource(),
				null);
		map.addLayer(FileDataStoreFinder.getDataStore(
				new File(dir, "countries-EuroMed-NEarth.shp"))
				.getFeatureSource(), null);
		map.addLayer(FileDataStoreFinder.getDataStore(
				new File(dir, "highways-EastBalkan-OSM.shp"))
				.getFeatureSource(), null);

	}
}