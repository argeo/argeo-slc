package org.argeo.slc.client.gis.views;

import java.awt.Frame;

import org.argeo.slc.geotools.swing.GisFieldViewer;
import org.argeo.slc.jts.PositionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.geotools.data.DataStore;

public class GeoToolsMapView extends ViewPart {
	public static final String ID = "org.argeo.slc.client.gis.views.GeoToolsMapView";

	private Composite embedded;

	private PositionProvider positionProvider;

	private DataStore postGisDataStore;

	private ClassLoader jaiImageIoClassLoader;

	public void createPartControl(Composite parent) {
		embedded = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		Frame frame = SWT_AWT.new_Frame(embedded);

		GisFieldViewer gisFieldViewer = new GisFieldViewer(frame);
		gisFieldViewer.setPostGisDataStore(postGisDataStore);
		gisFieldViewer.setPositionProvider(positionProvider);
		gisFieldViewer.setJaiImageIoClassLoader(jaiImageIoClassLoader);
		gisFieldViewer.afterPropertiesSet();
	}

	public void setFocus() {
		if (embedded != null)
			embedded.setFocus();
	}

	public void setPositionProvider(PositionProvider positionProvider) {
		this.positionProvider = positionProvider;
	}

	public void setPostGisDataStore(DataStore postGisDataStore) {
		this.postGisDataStore = postGisDataStore;
	}

	public void setJaiImageIoClassLoader(ClassLoader jaiImageIoClassLoader) {
		this.jaiImageIoClassLoader = jaiImageIoClassLoader;
	}

}