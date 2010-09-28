package org.argeo.slc.client.ui.views;

import org.argeo.slc.dao.process.SlcExecutionDao;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

public class ProcessListView extends ViewPart {
	// private final static Log log = LogFactory.getLog(ProcessListView.class);

	public static final String ID = "org.argeo.slc.client.ui.processListView";

	private TableViewer viewer;

	// IoC
	private SlcExecutionDao slcExecutionDao;
	private ITableLabelProvider tableLabelProvider;
	private IStructuredContentProvider structuredContentProvider;

	public void createPartControl(Composite parent) {
		Table table = createTable(parent);
		viewer = new TableViewer(table);
		viewer.setLabelProvider(tableLabelProvider);
		viewer.setContentProvider(structuredContentProvider);
		viewer.setInput(getViewSite());
	}

	protected Table createTable(Composite parent) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		Table table = new Table(parent, style);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 3;
		table.setLayoutData(gridData);

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

	public void retrieveResults() {
		viewer.setInput(slcExecutionDao.listSlcExecutions());
	}

	// IoC
	public void setSlcExecutionDao(SlcExecutionDao slcExecutionDao) {
		this.slcExecutionDao = slcExecutionDao;
	}

	public void setTableLabelProvider(ITableLabelProvider tableLabelProvider) {
		this.tableLabelProvider = tableLabelProvider;
	}

	public void setStructuredContentProvider(
			IStructuredContentProvider structuredContentProvider) {
		this.structuredContentProvider = structuredContentProvider;
	}

}