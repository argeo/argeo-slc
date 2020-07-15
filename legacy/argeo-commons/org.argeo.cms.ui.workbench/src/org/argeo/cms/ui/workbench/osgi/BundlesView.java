//package org.argeo.eclipse.ui.workbench.osgi;
//public class BundlesView {}

package org.argeo.cms.ui.workbench.osgi;

import org.argeo.cms.ui.workbench.WorkbenchUiPlugin;
import org.argeo.eclipse.ui.ColumnViewerComparator;
import org.argeo.eclipse.ui.specific.EclipseUiSpecificUtils;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Overview of the bundles as a table. Equivalent to Equinox 'ss' console
 * command.
 */
public class BundlesView extends ViewPart {
	private TableViewer viewer;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent);
		viewer.setContentProvider(new BundleContentProvider());
		viewer.getTable().setHeaderVisible(true);

		EclipseUiSpecificUtils.enableToolTipSupport(viewer);

		// ID
		TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(30);
		column.getColumn().setText("ID");
		column.getColumn().setAlignment(SWT.RIGHT);
		column.setLabelProvider(new ColumnLabelProvider() {
			private static final long serialVersionUID = -3122136344359358605L;

			public String getText(Object element) {
				return Long.toString(((Bundle) element).getBundleId());
			}
		});
		new ColumnViewerComparator(column);

		// State
		column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(18);
		column.getColumn().setText("State");
		column.setLabelProvider(new StateLabelProvider());
		new ColumnViewerComparator(column);

		// Symbolic name
		column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(250);
		column.getColumn().setText("Symbolic Name");
		column.setLabelProvider(new ColumnLabelProvider() {
			private static final long serialVersionUID = -4280840684440451080L;

			public String getText(Object element) {
				return ((Bundle) element).getSymbolicName();
			}
		});
		new ColumnViewerComparator(column);

		// Version
		column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setWidth(150);
		column.getColumn().setText("Version");
		column.setLabelProvider(new ColumnLabelProvider() {
			private static final long serialVersionUID = 6871926308708629989L;

			public String getText(Object element) {
				Bundle bundle = (org.osgi.framework.Bundle) element;
				return bundle.getVersion().toString();
			}
		});
		new ColumnViewerComparator(column);

		viewer.setInput(WorkbenchUiPlugin.getDefault().getBundle().getBundleContext());

	}

	@Override
	public void setFocus() {
		if (viewer != null)
			viewer.getControl().setFocus();
	}

	/** Content provider managing the array of bundles */
	private static class BundleContentProvider implements IStructuredContentProvider {
		private static final long serialVersionUID = -8533792785725875977L;

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof BundleContext) {
				BundleContext bc = (BundleContext) inputElement;
				return bc.getBundles();
			}
			return null;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}
}
