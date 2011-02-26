package org.argeo.slc.ui.gis.rap.openlayers;

import java.util.HashMap;

import org.argeo.slc.ui.gis.views.AbstractMapView;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.geotools.map.MapContext;
import org.polymap.openlayers.rap.widget.OpenLayersWidget;
import org.polymap.openlayers.rap.widget.base.OpenLayersEventListener;
import org.polymap.openlayers.rap.widget.base.OpenLayersObject;
import org.polymap.openlayers.rap.widget.base_types.Bounds;
import org.polymap.openlayers.rap.widget.base_types.Icon;
import org.polymap.openlayers.rap.widget.base_types.LonLat;
import org.polymap.openlayers.rap.widget.base_types.OpenLayersMap;
import org.polymap.openlayers.rap.widget.base_types.Pixel;
import org.polymap.openlayers.rap.widget.base_types.Size;
import org.polymap.openlayers.rap.widget.base_types.Style;
import org.polymap.openlayers.rap.widget.controls.ButtonControl;
import org.polymap.openlayers.rap.widget.controls.Control;
import org.polymap.openlayers.rap.widget.controls.EditingToolbarControl;
import org.polymap.openlayers.rap.widget.controls.KeyboardDefaultsControl;
import org.polymap.openlayers.rap.widget.controls.LayerSwitcherControl;
import org.polymap.openlayers.rap.widget.controls.MouseDefaultsControl;
import org.polymap.openlayers.rap.widget.controls.OverviewMapControl;
import org.polymap.openlayers.rap.widget.controls.PanZoomBarControl;
import org.polymap.openlayers.rap.widget.controls.PanelControl;
import org.polymap.openlayers.rap.widget.controls.ScaleControl;
import org.polymap.openlayers.rap.widget.controls.ScaleLineControl;
import org.polymap.openlayers.rap.widget.controls.SelectFeatureControl;
import org.polymap.openlayers.rap.widget.controls.SnappingControl;
import org.polymap.openlayers.rap.widget.controls.ToggleButtonControl;
import org.polymap.openlayers.rap.widget.features.VectorFeature;
import org.polymap.openlayers.rap.widget.geometry.LineStringGeometry;
import org.polymap.openlayers.rap.widget.geometry.LinearRingGeometry;
import org.polymap.openlayers.rap.widget.geometry.PointGeometry;
import org.polymap.openlayers.rap.widget.geometry.PolygonGeometry;
import org.polymap.openlayers.rap.widget.layers.BoxLayer;
import org.polymap.openlayers.rap.widget.layers.ImageLayer;
import org.polymap.openlayers.rap.widget.layers.MarkersLayer;
import org.polymap.openlayers.rap.widget.layers.OSMLayer;
import org.polymap.openlayers.rap.widget.layers.VectorLayer;
import org.polymap.openlayers.rap.widget.layers.WMSLayer;
import org.polymap.openlayers.rap.widget.marker.BoxMarker;
import org.polymap.openlayers.rap.widget.marker.IconMarker;

public class OpenLayersView extends AbstractMapView implements
		OpenLayersEventListener {
	private MapContext mapContext;

	private OpenLayersWidget openLayersWidget;
	private OpenLayersMap map;

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		// openLayersWidget = new OpenLayersWidget(parent, SWT.NONE);
		Composite top = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		top.setLayout(layout);

		openLayersWidget = new OpenLayersWidget(top, SWT.MULTI | SWT.WRAP,
				"/js_lib/OpenLayers/OpenLayers.js");
		openLayersWidget.setLayoutData(new GridData(GridData.FILL_BOTH));

		map = openLayersWidget.getMap();

		map.addControl(new LayerSwitcherControl());
		map.addControl(new MouseDefaultsControl());
		map.addControl(new KeyboardDefaultsControl());
		map.addControl(new PanZoomBarControl());
		map.addControl(new ScaleControl());

		// OSMLayer osm = new OSMLayer("OSM",
		// "http://tile.openstreetmap.org/${z}/${x}/${y}.png", 19);
		// map.addLayer(osm);

		 WMSLayer wms_layer = new WMSLayer("argeo",
		 "https://dev.argeo.org/geoserver/wms?",
		 "mbaudier-trips-2010");
		 map.addLayer(wms_layer);
		// createUI(map);
	}

	@Override
	public void setFocus() {
		openLayersWidget.setFocus();
	}

	public MapContext getMapContext() {
		return mapContext;
	}

	// HACKED from Simple Example
	private VectorLayer edit_layer;
	private EditingToolbarControl edit_toolbar;
	private VectorLayer selectable_boxes_layer;

	OverviewMapControl overview = null;
	WMSLayer wms_layer2;

	public void createUI(OpenLayersMap map) {
		HashMap<String, String> payload_map = new HashMap<String, String>();
		payload_map.put("layername", "event.layer.name");

		map.events.register(this, "changebaselayer", payload_map);

		payload_map.put("property", "event.property");
		payload_map.put("visibility", "event.layer.visibility");

		map.events.register(this, "changelayer", payload_map);

		// create and add a WMS layer
		WMSLayer wms_layer = new WMSLayer("polymap WMS",
				"http://www.polymap.de/geoserver/wms?",
				"states,tasmania_state_boundaries,tasmania_roads,tasmania_water_bodies");

		map.addLayer(wms_layer);

		// create and add a WMS layer with opacity
		wms_layer2 = new WMSLayer("OpenLayers WMS",
				"http://labs.metacarta.com/wms/vmap0", "basic");
		// wms_layer2.setIsBaseLayer(false);
		wms_layer2.setOpacity(0.2);
		map.addLayer(wms_layer2);

		// add a ImageLayer with external URL
		Bounds bounds = new Bounds(-180, -88.759, 180, 88.759);
		Size size = new Size(580, 288);
		ImageLayer image_layer = new ImageLayer("image layer ext",
				"http://earthtrends.wri.org/images/maps/4_m_citylights_lg.gif",
				bounds, size);
		map.addLayer(image_layer);

		// add a ImageLayer with internal URL
		Image image = Graphics.getImage("res/polymap_logo.png", getClass()
				.getClassLoader());
		ImageLayer image_layer_int = new ImageLayer("image layer int", image,
				bounds);
		map.addLayer(image_layer_int);

		// set Zoom and Center
		map.zoomTo(3);
		map.setCenter(-100.0, 40.0);

		// add some controls
		LayerSwitcherControl layer_switcher = new LayerSwitcherControl();

		map.addControl(layer_switcher);
		layer_switcher.maximizeControl();

		// map.addControl(new MouseDefaultsControl());
		map.addControl(new KeyboardDefaultsControl());
		// map.addControl(new PanZoomBarControl());
		map.addControl(new ScaleControl());

		ScaleLineControl scale_line = new ScaleLineControl();
		scale_line.setBottomOutUnits("");
		scale_line.setBottomInUnits("");
		map.addControl(scale_line);

		// overview = new OverviewMapControl( wms_layer2);
		// map.addControl(overview);

		/*
		 * 
		 * overview.destroy(); //map.removeControl(overview );
		 * 
		 * overview = new OverviewMapControl(image_layer_int);
		 * map.addControl(overview);
		 * 
		 * 
		 * 
		 * overview.addLayer(wms_layer2 );
		 */

		// overview.maximizeControl();

		// add vector layer to have a layer the user can edit
		edit_layer = new VectorLayer("edit layer");

		// map.addControl(new EditingToolbarControl(edit_layer) );

		PanelControl pc = new PanelControl();

		ToggleButtonControl tbc = new ToggleButtonControl("class_for_tbc");
		ButtonControl bc = new ButtonControl("class_for_bc", "alert('foo');");

		pc.addControls(new Control[] { bc, tbc });

		tbc.setStyle("background-color:red;", "background-color:green;");
		bc.setStyle("background-color:yellow; ");

		pc.setStyle(
				" width:  100px;   height: 24px;   right: 23px;  top: 5px;     position: relative; border-width:1px;  border-style:solid;",
				" display:block; width: 24px;   height: 24px; float:left;");
		map.addControl(pc);

		tbc.events.register(this, "activate", null);
		edit_layer.events.register(this, "beforefeatureadded", null);

		map.addLayer(edit_layer);
		edit_layer.setVisibility(false);

		// add vector layer with some boxes to demonstrate the modify feature
		// feature
		selectable_boxes_layer = new VectorLayer("selectable boxes");

		selectable_boxes_layer.events.register(this, "featureselected", null);

		selectable_boxes_layer.events.register(this, "featuremodified", null);

		map.addLayer(selectable_boxes_layer);

		OSMLayer osm = new OSMLayer("OSM",
				"http://tile.openstreetmap.org/${z}/${x}/${y}.png", 19);
		map.addLayer(osm);

		// map.events.register(this, "click", new HashMap<String,String>() {{
		// put("x",selectable_boxes_layer.getJSObjRef()+".getFeatureFromEvent(event)");
		// }});

		VectorFeature vector_feature = new VectorFeature(new Bounds(-100, 40,
				-80, 60).toGeometry());
		selectable_boxes_layer.addFeatures(vector_feature);
		vector_feature = new VectorFeature(
				new Bounds(-90, 70, -60, 80).toGeometry());
		selectable_boxes_layer.addFeatures(vector_feature);

		selectable_boxes_layer.setVisibility(false);

		// add vector layer to show how to style a feature
		VectorLayer styled_features_layer = new VectorLayer("styled Features");
		map.addLayer(styled_features_layer);

		Style point_style_red = new Style();
		point_style_red.setAttribute("fillColor", "#FF0000");

		vector_feature = new VectorFeature(new PointGeometry(-85, 50),
				point_style_red);
		styled_features_layer.addFeatures(vector_feature);

		Style poly_style = new Style();
		poly_style.setAttribute("fillColor", "blue");
		poly_style.setAttribute("strokeColor", "black");
		poly_style.setAttribute("strokeDashstyle", "dashdot");

		vector_feature = new VectorFeature(
				new Bounds(-120, 23, -100, 42).toGeometry(), poly_style);
		styled_features_layer.addFeatures(vector_feature);

		Style point_style_green = new Style();
		point_style_green.setAttribute("fillColor", "#00FF00");
		point_style_green.setAttribute("pointRadius", 20);

		vector_feature = new VectorFeature(new PointGeometry(-95, 65),
				point_style_green);
		styled_features_layer.addFeatures(vector_feature);

		// show some geometry
		VectorLayer geometry_features_layer = new VectorLayer(
				"Geometry Features");
		map.addLayer(geometry_features_layer);
		geometry_features_layer.setVisibility(false);

		PointGeometry[] point_list = { new PointGeometry(-99, 60),
				new PointGeometry(-107, 64), new PointGeometry(-130, 70) };
		VectorFeature linestring_feature = new VectorFeature(
				new LineStringGeometry(point_list));
		geometry_features_layer.addFeatures(linestring_feature);

		PointGeometry[] poly_point_list = { new PointGeometry(-99, 30),
				new PointGeometry(-107, 44), new PointGeometry(-130, 50) };
		VectorFeature poly_feature = new VectorFeature(new PolygonGeometry(
				new LinearRingGeometry(poly_point_list)));
		geometry_features_layer.addFeatures(poly_feature);

		/*
		 * // setting up the Modify Feature Control ModifyFeatureControl mfc =
		 * new ModifyFeatureControl( selectable_boxes_layer);
		 * 
		 * mfc.addMode(MooverdifyFeatureControl.DRAG);
		 * mfc.addMode(ModifyFeatureControl.RESHAPE);
		 * mfc.addMode(ModifyFeatureControl.ROTATE);
		 * 
		 * mfc.activate(); map.addControl(mfc);
		 */
		// setting up the Modify Feature Control

		/*
		 * mfc.addMode(ModifyFeatureControl.DRAG);
		 * mfc.addMode(ModifyFeatureControl.RESHAPE);
		 * mfc.addMode(ModifyFeatureControl.ROTATE);
		 */
		// mfc.setHover(true);

		SelectFeatureControl mfc = new SelectFeatureControl(
				selectable_boxes_layer, SelectFeatureControl.FLAG_HOVER);

		map.addControl(mfc);
		mfc.setHighlightOnly(true);
		mfc.setRenderIntent("temporary");
		// mfc.activate();

		// mfc.setHover(false);
		/*
		 * SelectFeatureControl mfc2 = new SelectFeatureControl(
		 * selectable_boxes_layer,true); //SelectFeatureControl.FLAG_BOX);
		 * 
		 * mfc.addMode(ModifyFeatureControl.DRAG);
		 * mfc.addMode(ModifyFeatureControl.RESHAPE);
		 * mfc.addMode(ModifyFeatureControl.ROTATE);
		 * 
		 * // mfc.setHover(true);
		 * 
		 * 
		 * map.addControl(mfc2); mfc2.setBox(true); mfc2.setMultiple(false);
		 * //mfc2.activate();
		 */
		// mfc2.setHover(false);

		// showing box_layer
		BoxMarker bm = new BoxMarker(new Bounds(-120, 23, -100, 42));
		BoxLayer bl = new BoxLayer("box layer");
		bl.addMarker(bm);
		map.addLayer(bl);
		bl.setVisibility(false);

		// show IconMarkers
		MarkersLayer ml = new MarkersLayer("icon markers");
		map.addLayer(ml);
		ml.setVisibility(false);

		// icon marker with default icon-image
		IconMarker im = new IconMarker(new LonLat(-100, 50));
		ml.addMarker(im);

		// icon marker with custom image
		Icon ico = new Icon("http://www.mensus.net/img/icons/google/aqua.png",
				new Size(10, 17), new Pixel(0, 0));
		IconMarker im2 = new IconMarker(new LonLat(-110, 60), ico);
		ml.addMarker(im2);

	}

	public void process_event(OpenLayersObject obj, String event_name,
			HashMap<String, String> payload) {
		System.out.println("event from" + obj);
		if (event_name.equals("changebaselayer")) {
			System.out
					.println("client changed baselayer to '"
							+ payload.get("layername") + "' "
							+ payload.get("property"));

			// if (overview != null)
			// map.removeControl(overview);

			// overview = new OverviewMapControl(wms_layer2);
			// overview.addLayer( selectable_boxes_layer);

			// map.addControl(overview);
			// overview.addLayer( selectable_boxes_layer);
		} else if (event_name.equals("changelayer")) {
			System.out.println("client changed layer '"
					+ payload.get("layername") + "' " + payload.get("property")
					+ "' " + payload.get("visibility"));
			if (payload.get("property").equals("visibility")) {
				Boolean visible = payload.get("visibility").equals("true");
				if (payload.get("layername").equals(edit_layer.getName())) {
					OpenLayersMap map = openLayersWidget.getMap();
					if (visible) {
						// adding edit control for the vector layer created
						// above
						edit_toolbar = new EditingToolbarControl(edit_layer);
						map.addControl(edit_toolbar);
						VectorLayer[] snapping_layers = { edit_layer,
								selectable_boxes_layer };
						SnappingControl snap_ctrl = new SnappingControl(
								edit_layer, snapping_layers, false);
						snap_ctrl.activate();
						map.addControl(snap_ctrl);

					} else {
						edit_toolbar.deactivate();
						map.removeControl(edit_toolbar);
					}
				}
			}
		} else
			System.out.println("unknown event " + event_name);
	}

}
