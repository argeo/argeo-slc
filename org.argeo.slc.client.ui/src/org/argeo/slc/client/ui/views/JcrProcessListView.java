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
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;
import javax.jcr.query.Query;

import org.argeo.eclipse.ui.jcr.AsyncUiEventListener;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;
import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.client.ui.SlcImages;
import org.argeo.slc.client.ui.editors.ProcessEditor;
import org.argeo.slc.client.ui.editors.ProcessEditorInput;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.jcr.SlcJcrUtils;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/** Displays processes. */
public class JcrProcessListView extends ViewPart {
	public static final String ID = ClientUiPlugin.ID + ".jcrProcessListView";

	private TableViewer viewer;

	private Repository repository;
	private Session session;

	private EventListener processesObserver;

	private DateFormat dateFormat = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss");
	private Integer queryLimit = 2000;

	public void createPartControl(Composite parent) {
		try {
			session = repository.login();
		} catch (RepositoryException re) {
			throw new SlcException("Unable to log in Repository " + repository,
					re);
		}
		Table table = createTable(parent);
		viewer = new TableViewer(table);
		viewer.setLabelProvider(new LabelProvider());
		viewer.setContentProvider(new ContentProvider());
		viewer.setInput(getViewSite());
		viewer.addDoubleClickListener(new ViewDoubleClickListener());

		processesObserver = new AsyncUiEventListener(viewer.getTable()
				.getDisplay()) {
			protected void onEventInUiThread(List<Event> events) {
				// TODO optimize by updating only the changed process
				viewer.refresh();
			}
		};
		try {
			ObservationManager observationManager = session.getWorkspace()
					.getObservationManager();
			observationManager.addEventListener(processesObserver,
					Event.NODE_ADDED | Event.NODE_REMOVED
							| Event.PROPERTY_CHANGED,
					SlcJcrUtils.getSlcProcessesBasePath(session), true, null,
					null, false);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot register listeners", e);
		}

	}

	protected Table createTable(Composite parent) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION;
		// does not function with RAP, commented for the time being
		// | SWT.HIDE_SELECTION;

		Table table = new Table(parent, style);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("Date");
		column.setWidth(200);

		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Host");
		column.setWidth(100);

		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText("Id");
		column.setWidth(300);

		column = new TableColumn(table, SWT.LEFT, 3);
		column.setText("Status");
		column.setWidth(100);

		return table;
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	class ContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			try {
				// TODO filter, optimize with virtual table, ...
				String sql = "SELECT * from [slc:process] ORDER BY [jcr:lastModified] DESC";
				Query query = session.getWorkspace().getQueryManager()
						.createQuery(sql, Query.JCR_SQL2);
				// TODO paging
				query.setLimit(queryLimit);
				List<Node> nodes = new ArrayList<Node>();
				for (NodeIterator nit = query.execute().getNodes(); nit
						.hasNext();) {
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

	class LabelProvider extends ColumnLabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(Object obj, int columnIndex) {
			if (columnIndex != 0)
				return null;
			try {
				Node node = (Node) obj;
				String status = node.getProperty(SlcNames.SLC_STATUS)
						.getString();
				if (status.equals(ExecutionProcess.NEW)
						|| status.equals(ExecutionProcess.INITIALIZED)
						|| status.equals(ExecutionProcess.SCHEDULED))
					return SlcImages.PROCESS_SCHEDULED;
				else if (status.equals(ExecutionProcess.ERROR)
						|| status.equals(ExecutionProcess.UNKOWN))
					return SlcImages.PROCESS_ERROR;
				else if (status.equals(ExecutionProcess.COMPLETED))
					return SlcImages.PROCESS_COMPLETED;
				else if (status.equals(ExecutionProcess.RUNNING))
					return SlcImages.PROCESS_RUNNING;
				else if (status.equals(ExecutionProcess.KILLED))
					return SlcImages.PROCESS_ERROR;
				else
					throw new SlcException("Unkown status " + status);
			} catch (RepositoryException e) {
				throw new SlcException("Cannot get column text", e);
			}
		}

		public String getColumnText(Object obj, int index) {
			try {
				Node node = (Node) obj;
				switch (index) {

				case 0:
					return dateFormat.format(node
							.getProperty(Property.JCR_LAST_MODIFIED).getDate()
							.getTime());
				case 1:
					return "local";
				case 2:
					return node.getProperty(SlcNames.SLC_UUID).getString();
				case 3:
					return node.getProperty(SlcNames.SLC_STATUS).getString();
				}
				return getText(obj);
			} catch (RepositoryException e) {
				throw new SlcException("Cannot get column text", e);
			}
		}

	}

	class ViewDoubleClickListener implements IDoubleClickListener {
		public void doubleClick(DoubleClickEvent evt) {
			Object obj = ((IStructuredSelection) evt.getSelection())
					.getFirstElement();
			try {
				if (obj instanceof Node) {
					Node node = (Node) obj;
					if (node.isNodeType(SlcTypes.SLC_PROCESS)) {
						IWorkbenchPage activePage = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow().getActivePage();
						activePage.openEditor(
								new ProcessEditorInput(node.getPath()),
								ProcessEditor.ID);
					}
				}
			} catch (Exception e) {
				throw new SlcException("Cannot open " + obj, e);
			}
		}

	}

	public void dispose() {
		JcrUtils.unregisterQuietly(session.getWorkspace(), processesObserver);
		JcrUtils.logoutQuietly(session);
		super.dispose();
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

}