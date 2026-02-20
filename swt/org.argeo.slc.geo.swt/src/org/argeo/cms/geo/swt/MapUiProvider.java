package org.argeo.cms.geo.swt;

import org.argeo.api.acr.Content;
import org.argeo.api.js.ol.GeoJSON;
import org.argeo.api.js.ol.Layer;
import org.argeo.api.js.ol.OSM;
import org.argeo.api.js.ol.TileLayer;
import org.argeo.api.js.ol.VectorLayer;
import org.argeo.api.js.ol.VectorSource;
import org.argeo.app.swt.js.SwtBrowserJsPart;
import org.argeo.cms.geo.ux.js.AbstractGeoJsObject;
import org.argeo.cms.geo.ux.js.OpenLayersMapPart;
import org.argeo.cms.geo.ux.js.SentinelCloudless;
import org.argeo.cms.swt.acr.SwtUiProvider;
import org.argeo.cms.ux.js.JsClient;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/** Create map parts. */
public class MapUiProvider implements SwtUiProvider {

	@Override
	public Control createUiPart(Composite parent, Content context) {
		JsClient jsClient;
//		boolean experimental = true;
//		if (experimental) {
//			if (SwtVariant.RCP.isCurrentVariant()) {
//				Composite embed = new Composite(parent, SwtIntConstant.EMBEDDED.getAsInt());
//				java.awt.Frame frame = SWT_AWT.new_Frame(embed);
//				WorldWindowGLCanvas wwd = new WorldWindowGLCanvas();
//				wwd.setPreferredSize(new java.awt.Dimension(1000, 800));
//				frame.add(wwd, java.awt.BorderLayout.CENTER);
//				wwd.setModel(new BasicModel());
//			} else if (SwtVariant.RAP.isCurrentVariant()) {
//				jsClient = new SwtBrowserJsPart(parent, 0, AbstractGeoJsObject.ARGEO_APP_WORDLWIND_JS_URL);
//			}
//			return null;
//		}
		jsClient = new SwtBrowserJsPart(parent, 0, AbstractGeoJsObject.ARGEO_APP_GEO_JS_URL);
		OpenLayersMapPart mapPart = new OpenLayersMapPart(jsClient, "defaultOverviewMap");
		mapPart.getMap().getView().setCenter(new int[] { 0, 0 });
		mapPart.getMap().getView().setZoom(6);

		Layer satelliteLayer = new TileLayer(new SentinelCloudless());
		satelliteLayer.setMaxResolution(200);
		mapPart.getMap().addLayer(satelliteLayer);

		TileLayer baseLayer = new TileLayer();
		baseLayer.setSource(new OSM());
		baseLayer.setOpacity(0.5);
		mapPart.getMap().addLayer(baseLayer);

		Layer dataLayer = new VectorLayer(new VectorSource(
				"https://openlayers.org/en/v4.6.5/examples/data/geojson/countries.geojson", new GeoJSON()));
		mapPart.getMap().addLayer(dataLayer);

//		SwtJsMapPart map = new SwtJsMapPart("defaultOverviewMap", parent, 0);
//		map.setCenter(13.404954, 52.520008); // Berlin
////		map.setCenter(-74.00597, 40.71427); // NYC
////		map.addPoint(-74.00597, 40.71427, null);
//		map.setZoom(6);
//		// map.addUrlLayer("https://openlayers.org/en/v4.6.5/examples/data/geojson/countries.geojson",
//		// Format.GEOJSON);
		return parent;
	}

}
