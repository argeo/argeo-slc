package org.argeo.slc.client.ui.views;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.process.SlcExecution;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

public class ProcessListView extends ViewPart {
	private final static Log log = LogFactory.getLog(ProcessListView.class);

	public static final String ID = "org.argeo.slc.client.ui.processListView";

	private TableViewer viewer;

	private SlcExecutionDao slcExecutionDao;

	public void createPartControl(Composite parent) {
		Table table = createTable(parent);
		viewer = new TableViewer(table);
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setContentProvider(new ViewContentProvider());

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

	protected static class ViewContentProvider implements
			IStructuredContentProvider {

		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		}

		public void dispose() {
		}

		@SuppressWarnings("unchecked")
		public Object[] getElements(Object obj) {
			if (obj instanceof List) {
				return ((List<SlcExecution>) obj).toArray();
			} else {
				return new Object[0];
			}
		}
	}

	protected class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			SlcExecution se = (SlcExecution) obj;
			switch (index) {

			case 0:
				return getText(se.getStartDate());
			case 1:
				return se.getHost();
			case 2:
				return se.getUuid();
			case 3:
				return se.currentStep().getType();
			}
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			return null;
		}

	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void retrieveResults() {
		try {
			List<SlcExecution> lst = slcExecutionDao.listSlcExecutions();

			if (log.isTraceEnabled())
				log.trace("Slc Execution count: " + lst.size());
			viewer.setInput(lst);
			// viewer.refresh();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setSlcExecutionDao(SlcExecutionDao slcExecutionDao) {
		this.slcExecutionDao = slcExecutionDao;
	}

}