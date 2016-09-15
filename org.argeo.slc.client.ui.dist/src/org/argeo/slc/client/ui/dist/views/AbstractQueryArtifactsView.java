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
package org.argeo.slc.client.ui.dist.views;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.eclipse.ui.GenericTableComparator;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.utils.ArtifactsTableConfigurer;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;

/** Factorizes useful methods to build a query view in a sashForm */
public abstract class AbstractQueryArtifactsView extends ViewPart implements
		SlcTypes {
	private static final Log log = LogFactory
			.getLog(AbstractQueryArtifactsView.class);

	// shortcuts
	final protected static String SAVB = "[" + SLC_ARTIFACT_VERSION_BASE + "]";
	final protected static String SBA = "[" + SLC_BUNDLE_ARTIFACT + "]";
	final protected static String SIP = "[" + SLC_IMPORTED_PACKAGE + "]";
	final protected static String SEP = "[" + SLC_EXPORTED_PACKAGE + "]";

	/* DEPENDENCY INJECTION */
	private Session session;
	private List<String> columnProperties;

	// This page widgets
	private TableViewer viewer;
	private List<TableViewerColumn> tableViewerColumns = new ArrayList<TableViewerColumn>();
	private ArtifactsTableConfigurer tableConfigurer;
	private GenericTableComparator comparator;

	// to be set by client to display all columns
	private boolean displayAllColumns = false;

	protected void createResultPart(Composite parent) {
		viewer = new TableViewer(parent);
		Table table = viewer.getTable();
		table.getParent().setLayout(new GridLayout(1, false));
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);

		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setContentProvider(new ViewContentProvider());
		// viewer.addDoubleClickListener(new GenericDoubleClickListener());

		tableConfigurer = new ArtifactsTableConfigurer(viewer, 1,
				GenericTableComparator.DESCENDING);

		comparator = tableConfigurer.getComparator();
		viewer.setComparator(comparator);
	}

	protected void executeQuery(String statement) {
		try {
			Calendar stStamp = new GregorianCalendar();
			if (log.isDebugEnabled()) {
				log.debug("Executed query: " + statement);
			}
			QueryResult qr = session.getWorkspace().getQueryManager()
					.createQuery(statement, Query.JCR_SQL2).execute();

			if (log.isDebugEnabled()) {
				Calendar enStamp = new GregorianCalendar();
				long duration = enStamp.getTimeInMillis()
						- stStamp.getTimeInMillis();
				log.debug("Query executed in : " + duration / 1000 + "s.");
			}

			// remove previous columns
			for (TableViewerColumn tvc : tableViewerColumns)
				tvc.getColumn().dispose();

			// If a pre(-defined list of columns has been injected, we use it,
			// otherwise we display all results of the resultSet
			if (!displayAllColumns && columnProperties != null) {
				int i = 0;

				Iterator<String> it = columnProperties.iterator();
				while (it.hasNext()) {
					String columnName = it.next();

					TableViewerColumn tvc = new TableViewerColumn(viewer,
							SWT.NONE);
					tableConfigurer.configureColumn(columnName, tvc, i);
					tvc.setLabelProvider(tableConfigurer
							.getLabelProvider(columnName));
					tableViewerColumns.add(tvc);
					i++;
				}
			} else {
				int i = 0;
				for (final String columnName : qr.getColumnNames()) {
					TableViewerColumn tvc = new TableViewerColumn(viewer,
							SWT.NONE);
					// Small hack to remove prefix from the column name
					// String tmpStr = columnName.substring(columnName
					// .lastIndexOf(".") + 1);
					tableConfigurer.configureColumn(columnName, tvc, i);
					tvc.setLabelProvider(tableConfigurer
							.getLabelProvider(columnName));
					tableViewerColumns.add(tvc);
					i++;
				}
			}
			// We must create a local list because query result can be read only
			// once.
			try {
				List<Row> rows = new ArrayList<Row>();
				RowIterator rit = qr.getRows();
				while (rit.hasNext()) {
					rows.add(rit.nextRow());
				}
				viewer.setInput(rows);
			} catch (RepositoryException e) {
				throw new SlcException("Cannot read query result", e);
			}

		} catch (RepositoryException e) {
			ErrorDialog.openError(null, "Error", "Cannot execute JCR query: "
					+ statement, new Status(IStatus.ERROR,
					"org.argeo.eclipse.ui.jcr", e.getMessage()));
		}
	}

	/**
	 * Client must use this method to display all columns of the result set
	 * instead of a limited predifined and injected set
	 **/
	public void displayAllColumns(boolean flag) {
		displayAllColumns = flag;
	}

	// Can be overridden by subclasses.
	protected String generateSelectStatement() {
		StringBuffer sb = new StringBuffer("select " + SAVB + ".* ");
		return sb.toString();
	}

	protected String generateFromStatement() {
		StringBuffer sb = new StringBuffer(" from ");
		sb.append(SAVB);
		sb.append(" ");
		return sb.toString();
	}

	// Providers
	protected class ViewContentProvider implements IStructuredContentProvider {
		private static final long serialVersionUID = 5286293288979552056L;

		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		}

		public void dispose() {
		}

		@SuppressWarnings("unchecked")
		public Object[] getElements(Object obj) {
			return ((List<String[]>) obj).toArray();
		}
	}

	protected class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		private static final long serialVersionUID = -2407263563879116348L;

		public String getColumnText(Object obj, int index) {
			if (!(obj instanceof String[]))
				return "Object is not properly formatted ";

			String[] value = (String[]) obj;

			return value[index];
		}

		public Image getColumnImage(Object obj, int index) {
			return null;
		}
	}

	/* DEPENDENCY INJECTION */
	public void setSession(Session session) {
		this.session = session;
	}

	public void setColumnProperties(List<String> columnProperties) {
		this.columnProperties = columnProperties;
	}
}