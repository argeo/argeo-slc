package org.argeo.slc.client.ui.views;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.test.tree.ResultAttributes;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
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

public class ResultListView extends ViewPart {
	private final static Log log = LogFactory.getLog(ResultListView.class);

	public static final String ID = "org.argeo.slc.client.ui.resultListView";

	private TableViewer viewer;

	private TreeTestResultCollectionDao testResultCollectionDao;

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
		column.setText("UUID");
		column.setWidth(300);

		return table;
	}

	protected class ViewContentProvider implements IStructuredContentProvider {
		// private List<ResultAttributes> lst;

		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
			// if (arg2 instanceof List) {
			// lst = (List<ResultAttributes>) arg2;
			// log.trace("result count: " + lst.size());
			// }
		}

		public void dispose() {
		}

		public Object[] getElements(Object obj) {
			// if (lst == null)
			// return new Object[0];
			// else
			// return lst.toArray();
			return testResultCollectionDao.listResultAttributes(null).toArray();
		}
	}

	protected class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			ResultAttributes ra = (ResultAttributes) obj;
			switch (index) {
			case 0:
				return getText(ra.getCloseDate());
			case 1:
				return ra.getUuid();
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
			// List<ResultAttributes> lst = testResultCollectionDao
			// .listResultAttributes(null);
			// log.info("result count: " + lst.size());
			// viewer.setInput(lst);
			viewer.refresh();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setTestResultCollectionDao(
			TreeTestResultCollectionDao testResultCollectionDao) {
		this.testResultCollectionDao = testResultCollectionDao;
	}

}
