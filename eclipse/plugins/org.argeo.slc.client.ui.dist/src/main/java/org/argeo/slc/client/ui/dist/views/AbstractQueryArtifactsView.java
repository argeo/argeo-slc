package org.argeo.slc.client.ui.dist.views;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.ArgeoException;
import org.argeo.eclipse.ui.GenericTableComparator;
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

	/* DEPENDENCY INJECTION */
	private Session session;

	// This page widgets
	private TableViewer viewer;
	private List<TableViewerColumn> tableViewerColumns = new ArrayList<TableViewerColumn>();
	private ArtifactsTableConfigurer tableConfigurer;
	private GenericTableComparator comparator;

	protected void createResultPart(Composite parent) {
		viewer = new TableViewer(parent);
		Table table = viewer.getTable();
		table.getParent().setLayout(new GridLayout(1, false));
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);

		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setContentProvider(new ViewContentProvider());
		// viewer.addDoubleClickListener(new ViewDoubleClickListener());
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
			int i = 0;
			for (final String columnName : qr.getColumnNames()) {
				TableViewerColumn tvc = new TableViewerColumn(viewer, SWT.NONE);
				tableConfigurer.configureColumn(columnName, tvc, i);
				tvc.setLabelProvider(tableConfigurer
						.getLabelProvider(columnName));
				tableViewerColumns.add(tvc);
				i++;
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
				throw new ArgeoException("Cannot read query result", e);
			}

		} catch (RepositoryException e) {
			ErrorDialog.openError(null, "Error", "Cannot execute JCR query: "
					+ statement, new Status(IStatus.ERROR,
					"org.argeo.eclipse.ui.jcr", e.getMessage()));
		}
	}

	// Can be overridden by subclasses.
	protected String generateSelectStatement() {
		StringBuffer sb = new StringBuffer("select * ");
		return sb.toString();
	}

	protected String generateFromStatement() {
		StringBuffer sb = new StringBuffer(" from [");
		sb.append(SLC_ARTIFACT);
		sb.append("] ");
		return sb.toString();
	}

	// Providers
	protected class ViewContentProvider implements IStructuredContentProvider {

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
}