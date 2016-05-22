/*
 * Copyright (C) 2007-2012 Argeo GmbH
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
package org.argeo.slc.client.ui.views;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;
import javax.jcr.query.Query;

import org.argeo.eclipse.ui.jcr.AsyncUiEventListener;
import org.argeo.eclipse.ui.jcr.NodeElementComparer;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.client.ui.editors.ProcessEditor;
import org.argeo.slc.client.ui.editors.ProcessEditorInput;
import org.argeo.slc.jcr.SlcJcrResultUtils;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/** Displays results. */
public class JcrResultListView extends ViewPart implements SlcNames {
	public static final String ID = ClientUiPlugin.ID + ".jcrResultListView";

	private TableViewer viewer;

	private Repository repository;
	private Session session;

	private EventListener resultsObserver;

	private DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
	private Integer queryLimit = 2000;

	public void createPartControl(Composite parent) {
		try {
			session = repository.login();
		} catch (RepositoryException e1) {
			throw new SlcException("Cannot log in to repository");
		}

		Table table = createTable(parent);
		viewer = new TableViewer(table);
		viewer.setLabelProvider(createLabelProvider());
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setInput(getViewSite());
		viewer.addDoubleClickListener(new ViewDoubleClickListener());
		viewer.setComparer(new NodeElementComparer());

		getViewSite().setSelectionProvider(viewer);

		resultsObserver = new ResultObserver(viewer.getTable().getDisplay());
		try {
			ObservationManager observationManager = session.getWorkspace().getObservationManager();
			String[] nodeTypes = { SlcTypes.SLC_TEST_RESULT };
			// FIXME Will not be notified if empty result is deleted
			observationManager.addEventListener(resultsObserver, Event.PROPERTY_ADDED | Event.NODE_REMOVED,
					SlcJcrResultUtils.getSlcResultsBasePath(session), true, null, nodeTypes, false);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot register listeners", e);
		}

	}

	protected Table createTable(Composite parent) {
		int style = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI;
		// does not work with RAP, commented for the time being
		// | SWT.HIDE_SELECTION;

		Table table = new Table(parent, style);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("Date");
		column.setWidth(200);

		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Id");
		column.setWidth(300);

		return table;
	}

	// public void refresh() {
	// viewer.refresh();
	// }

	/*
	 * METHODS TO BE OVERRIDDEN
	 */
	protected IBaseLabelProvider createLabelProvider() {
		return new ViewLabelProvider();
	}

	protected void processDoubleClick(DoubleClickEvent evt) {
		Object obj = ((IStructuredSelection) evt.getSelection()).getFirstElement();
		try {
			if (obj instanceof Node) {
				Node node = (Node) obj;
				// FIXME: open a default result editor
				if (node.isNodeType(SlcTypes.SLC_PROCESS)) {
					IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					activePage.openEditor(new ProcessEditorInput(node.getPath()), ProcessEditor.ID);
				}
			}
		} catch (Exception e) {
			throw new SlcException("Cannot open " + obj, e);
		}
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	class ViewContentProvider implements IStructuredContentProvider {
		private static final long serialVersionUID = -4719077015405546260L;

		public Object[] getElements(Object inputElement) {
			try {
				// TODO filter, optimize with virtual table, ...
				String sql = "SELECT * from [" + SlcTypes.SLC_TEST_RESULT + "] ORDER BY [jcr:lastModified] DESC";
				Query query = session.getWorkspace().getQueryManager().createQuery(sql, Query.JCR_SQL2);
				// TODO paging
				query.setLimit(queryLimit);
				List<Node> nodes = new ArrayList<Node>();
				for (NodeIterator nit = query.execute().getNodes(); nit.hasNext();) {
					nodes.add(nit.nextNode());
				}
				return nodes.toArray();
			} catch (RepositoryException e) {
				throw new SlcException("Cannot retrieve processes", e);
			}
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	class ViewLabelProvider extends ColumnLabelProvider implements ITableLabelProvider {
		private static final long serialVersionUID = -6323202576386795247L;

		public Image getColumnImage(Object obj, int columnIndex) {
			if (columnIndex != 0)
				return null;
			try {
				Node node = (Node) obj;
				if (node.hasProperty(SLC_COMPLETED)) {
					// TODO
				}
				return null;
			} catch (RepositoryException e) {
				throw new SlcException("Cannot get column text", e);
			}
		}

		public String getColumnText(Object obj, int index) {
			try {
				Node node = (Node) obj;
				switch (index) {

				case 0:
					if (node.hasProperty(SLC_COMPLETED)) {
						return dateFormat.format(node.getProperty(SLC_COMPLETED).getDate().getTime());
					} else {
						return "OPEN";
					}
				case 1:
					return node.getProperty(SlcNames.SLC_UUID).getString();
				}
				return getText(obj);
			} catch (RepositoryException e) {
				throw new SlcException("Cannot get column text", e);
			}
		}

	}

	class ViewDoubleClickListener implements IDoubleClickListener {
		public void doubleClick(DoubleClickEvent evt) {
			processDoubleClick(evt);
		}

	}

	class ResultObserver extends AsyncUiEventListener {

		public ResultObserver(Display display) {
			super(display);
		}

		@Override
		protected Boolean willProcessInUiThread(List<Event> events) throws RepositoryException {
			for (Event event : events) {
				// getLog().debug("Received event " + event);
				int eventType = event.getType();
				if (eventType == Event.NODE_REMOVED)
					return true;
				String path = event.getPath();
				int index = path.lastIndexOf('/');
				String propertyName = path.substring(index + 1);
				if (propertyName.equals(SLC_COMPLETED) || propertyName.equals(SLC_UUID)) {
					return true;
				}
			}
			return false;
		}

		protected void onEventInUiThread(List<Event> events) throws RepositoryException {
			if (getLog().isTraceEnabled())
				getLog().trace("Refresh result list");
			viewer.refresh();
		}
	}

	public void dispose() {
		JcrUtils.unregisterQuietly(session.getWorkspace(), resultsObserver);
		JcrUtils.logoutQuietly(session);
		super.dispose();
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}
}