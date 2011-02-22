package org.argeo.slc.ui.gis.views;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.eclipse.ui.TreeParent;
import org.argeo.slc.SlcException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.part.ViewPart;
import org.geotools.data.DataStore;
import org.geotools.map.MapContext;
import org.geotools.map.MapLayer;
import org.opengis.feature.type.Name;

public class LayersView extends ViewPart {
	public final static String ID = "org.argeo.slc.ui.gis.layersView";

	private final static Log log = LogFactory.getLog(LayersView.class);

	private TreeViewer viewer;

	private List<DataStore> dataStores;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new MapContextContentProvider());
		viewer.setLabelProvider(new MapContextLabelProvider());
		viewer.setInput(getViewSite());
	}

	@Override
	public void setFocus() {
		viewer.getTree().setFocus();
	}

	public void setMapContext(MapContext mapContext) {
		viewer.setInput(mapContext);
	}

	public void refresh() {
		viewer.refresh();
	}

	public void setDataStores(List<DataStore> dataStores) {
		this.dataStores = dataStores;
	}

	private class MapContextContentProvider implements ITreeContentProvider {

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			TreeParent dataStoresNode = new TreeParent("Data Stores");
			for (DataStore dataStore : dataStores)
				dataStoresNode.addChild(new DataStoreNode(dataStore));
			if (inputElement instanceof MapContext)
				return new Object[] {
						new MapContextNode((MapContext) inputElement),
						dataStoresNode };
			else if (inputElement instanceof IViewSite)
				return new Object[] { dataStoresNode };
			else
				return getChildren(inputElement);
		}

		public Object[] getChildren(Object element) {
			if (element instanceof MapContextNode) {
				MapContextNode mapContextNode = (MapContextNode) element;
				return mapContextNode.getMapContext().getLayers();
			} else if (element instanceof MapLayer) {
				MapLayer mapLayer = (MapLayer) element;

			} else if (element instanceof TreeParent) {
				return ((TreeParent) element).getChildren();
			}
			return new Object[0];
		}

		public Object getParent(Object element) {
			if (element instanceof TreeParent) {
				return ((TreeParent) element).getParent();
			}
			return null;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof MapContextNode) {
				return true;
			} else if (element instanceof TreeParent) {
				return ((TreeParent) element).hasChildren();
			} else if (element instanceof MapLayer) {
				return false;
			}
			return false;
		}

	}

	private class MapContextLabelProvider extends LabelProvider {

		@Override
		public String getText(Object element) {
			if (element instanceof MapLayer) {
				MapLayer mapLayer = (MapLayer) element;
				String title = mapLayer.getTitle();
				if (title == null || title.trim().equals(""))
					title = mapLayer.toString();
				return title;
			}
			return super.getText(element);
		}

	}

	private class MapContextNode extends TreeParent {
		private MapContext mapContext;

		public MapContextNode(MapContext mapContext) {
			super("Map Context");
			this.mapContext = mapContext;
		}

		public MapContext getMapContext() {
			return mapContext;
		}

	}

	private class DataStoreNode extends TreeParent {
		private DataStore dataStore;

		public DataStoreNode(DataStore dataStore) {
			super(dataStore.getInfo().getTitle() != null ? dataStore.getInfo()
					.getTitle() : dataStore.toString());
			this.dataStore = dataStore;
			try {
				for (Name name : dataStore.getNames()) {
					if (log.isDebugEnabled())
						log.debug("Name: " + name);
					addChild(new TreeParent(name.toString()));
				}
			} catch (IOException e) {
				throw new SlcException("Cannot scan data store", e);
			}
		}

		public DataStore getDataStore() {
			return dataStore;
		}

	}
}
