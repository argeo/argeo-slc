package org.argeo.slc.client.ui.views;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;

/**
 * Multi-instance view that enables to browse the details of a given
 * TreeTestResult
 * 
 * @author bsinou
 * 
 */

public class ResultDetailView extends ViewPart {
	private final static Log log = LogFactory.getLog(ResultDetailView.class);
	public static final String ID = "org.argeo.slc.client.ui.resultDetailView";

	protected String[] columnNames = new String[] { "Test", "Message",
			"Exception Msg" };

	private TreeViewer viewer;
	private Tree resultDetailTree;

	private String uuid;
	private TreeTestResult ttr;

	// IoC
	private IContentProvider contentProvider;
	private ITableLabelProvider labelProvider;
	private TreeTestResultDao treeTestResultDao;

	public void createPartControl(Composite parent) {
		resultDetailTree = new Tree(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		// GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		// gd.horizontalSpan = 3;
		// resultDetailTree.setLayoutData(gd);
		resultDetailTree.setLinesVisible(true);
		resultDetailTree.setHeaderVisible(true);

		for (int i = 0; i < columnNames.length; i++) {
			TreeColumn column = new TreeColumn(resultDetailTree, SWT.LEFT, i);
			column.setText(columnNames[i]);

			// TIP: Don't forget to set the width. If not set it is set to
			// 0 and it will look as if the column didn't exist.
			switch (i) {
			case 0:
				column.setWidth(180);
				break;
			case 1:
				column.setWidth(150);
				break;
			case 2:
				column.setWidth(150);
				break;
			default:
				column.setWidth(70);
			}
		}
		viewer = new TreeViewer(resultDetailTree);
		viewer.setColumnProperties(columnNames);

		viewer.setContentProvider(contentProvider);
		// viewer.setLabelProvider(new ResultDetailLabelProvider());
		if (log.isTraceEnabled())
			log.debug("Injected LabelProvider :" + labelProvider.toString());

		// TIP: It seems, that if the table has not defined any TreeColumns then
		// a plain LabelProvider will be used. Since, we don't provide an
		// instance of LabelProvider, a default one will be used and
		// the TableLabelProvider is ignored without notice. Took me quite
		// a while to find that one out.
		viewer.setLabelProvider(labelProvider);
		if (log.isTraceEnabled())
			log.debug("Persisted labelProvider :"
					+ viewer.getLabelProvider().toString());

		// viewer.expandAll();

	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	public void retrieveResults() {
		ttr = treeTestResultDao.getTestResult(uuid);
		viewer.setInput(ttr);
		// viewer.setInput(getViewSite());

		// setFocus();
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	// IoC
	public void setContentProvider(IContentProvider contentProvider) {
		this.contentProvider = contentProvider;
	}

	public void setLabelProvider(ITableLabelProvider labelProvider) {
		this.labelProvider = labelProvider;
	}

	public void setTreeTestResultDao(TreeTestResultDao treeTestResultDao) {
		this.treeTestResultDao = treeTestResultDao;
	}

}