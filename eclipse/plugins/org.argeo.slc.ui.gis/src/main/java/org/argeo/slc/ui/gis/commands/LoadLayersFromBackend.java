package org.argeo.slc.ui.gis.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.geotools.Backend;
import org.argeo.slc.geotools.DataDescriptor;
import org.argeo.slc.geotools.data.FeatureSourceDataDescriptor;
import org.argeo.slc.geotools.data.PostgisDataDescriptor;
import org.argeo.slc.geotools.data.WorldImageDataDescriptor;
import org.argeo.slc.ui.gis.views.AbstractMapView;
import org.argeo.slc.ui.gis.views.LayersView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.geotools.map.MapContext;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;

/** Open a JCR query editor. */
public class LoadLayersFromBackend extends AbstractHandler {
	private final static Log log = LogFactory
			.getLog(LoadLayersFromBackend.class);
	private Backend backend;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		AbstractMapView mapView = (AbstractMapView) HandlerUtil
				.getActiveWorkbenchWindow(event).getActivePage()
				.findView(AbstractMapView.ID);

		MapContext mapContext = mapView.getMapContext();
		mapContext.clearLayerList();
		try {
			// load layers
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
						mapContext
								.addLayer(backend.loadFeatureSource(dd), null);
					} catch (Exception e) {
						log.warn(e);
					}
				} else if (dd instanceof PostgisDataDescriptor) {
					// DataStore dataStore = backend.loadDataStore(dd);
					for (DataDescriptor dd2 : backend.getAvailableData(dd))
						mapContext.addLayer(backend.loadFeatureSource(dd2),
								null);
				}
			}

			LayersView view = (LayersView) HandlerUtil
					.getActiveWorkbenchWindow(event).getActivePage()
					.findView(LayersView.ID);
			view.setMapContext(mapContext);
			// view.refresh();
		} catch (Exception e) {
			throw new ExecutionException("Cannot load layers from backend", e);
		}
		return null;
	}

	public void setBackend(Backend backend) {
		this.backend = backend;
	}

}
