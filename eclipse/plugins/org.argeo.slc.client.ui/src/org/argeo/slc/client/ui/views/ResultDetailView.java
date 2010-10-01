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

	private TreeViewer viewer;

	private String uuid;
	private TreeTestResult ttr;

	// IoC
	private IContentProvider contentProvider;
	private ITableLabelProvider labelProvider;
	private TreeTestResultDao treeTestResultDao;

	public void createPartControl(Composite parent) {
		// log.debug("In  create part Control &&& uuid = " + uuid);
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(labelProvider);
		// viewer.setInput(getViewSite());
		if (log.isDebugEnabled())
			log.debug("PartControl CREATED.");
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	public void retrieveResults() {
		ttr = treeTestResultDao.getTestResult(uuid);
		log.debug("========= ttr: " + ttr);
		viewer.setInput(ttr);
		log.debug("Input SET");
		setFocus();
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