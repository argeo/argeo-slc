package org.argeo.slc.client.ui.views;

import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.process.SlcExecution;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * Multi-instance view that enables to browse the details of a given
 * SlcExecution
 * 
 * @author bsinou
 * 
 */

public class ProcessDetailView extends ViewPart {
	// private final static Log log =
	// LogFactory.getLog(ProcessDetailView.class);
	public static final String ID = "org.argeo.slc.client.ui.processDetailView";

	private TreeViewer viewer;

	private String uuid;
	private SlcExecution se;

	// IoC
	private IContentProvider contentProvider;
	private ILabelProvider labelProvider;
	private SlcExecutionDao slcExecutionDao;

	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(labelProvider);
		// viewer.setInput(getViewSite());
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	public void retrieveResults() {
		se = slcExecutionDao.getSlcExecution(uuid);
		viewer.setInput(se);
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	// IoC
	public void setContentProvider(IContentProvider contentProvider) {
		this.contentProvider = contentProvider;
	}

	public void setLabelProvider(ILabelProvider labelProvider) {
		this.labelProvider = labelProvider;
	}

	public void setSlcExecutionDao(SlcExecutionDao slcExecutionDao) {
		this.slcExecutionDao = slcExecutionDao;
	}
}