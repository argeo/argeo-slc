package org.argeo.slc.client.ui.dist.views;

import org.argeo.cms.ArgeoNames;
import org.argeo.slc.SlcNames;
import org.argeo.slc.client.ui.dist.DistConstants;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.controllers.DistTreeComparator;
import org.argeo.slc.client.ui.dist.controllers.DistTreeDoubleClickListener;
import org.argeo.slc.client.ui.dist.controllers.DistTreeLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;

/**
 * Browse, manipulate and manage distributions accross multiple repositories
 * (like fetch, merge, publish, etc.).
 */
public class AnonymousDistributionsView extends ViewPart implements SlcNames,
		ArgeoNames {
	// private final static Log log = LogFactory
	// .getLog(AnonymousDistributionsView.class);
	public final static String ID = DistPlugin.PLUGIN_ID
			+ ".anonymousDistributionsView";

	/* DEPENDENCY INJECTION */
	private ITreeContentProvider treeContentProvider;

	// This view widgets
	private TreeViewer viewer;

	@Override
	public void createPartControl(Composite parent) {
		// Define the TableViewer
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.BORDER);

		TreeViewerColumn col = new TreeViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(400);
		col.setLabelProvider(new DistTreeLabelProvider());

		final Tree tree = viewer.getTree();
		tree.setHeaderVisible(false);
		tree.setLinesVisible(false);

		// viewer.setContentProvider(new DistTreeContentProvider());
		viewer.setContentProvider(treeContentProvider);
		viewer.addDoubleClickListener(new DistTreeDoubleClickListener(viewer));
		viewer.setComparator(new DistTreeComparator());

		// Initialize
		refresh();
	}

	/**
	 * Force refresh of the whole view
	 */
	public void refresh() {
		Object[] ee = viewer.getExpandedElements();
		viewer.setInput(DistConstants.DEFAULT_PUBLIC_REPOSITORY_URI);
		// viewer.expandToLevel(2);
		viewer.setExpandedElements(ee);
	}

	@Override
	public void setFocus() {
		viewer.getTree().setFocus();
	}

	/*
	 * DEPENDENCY INJECTION
	 */
	public void setTreeContentProvider(ITreeContentProvider treeContentProvider) {
		this.treeContentProvider = treeContentProvider;
	}
}