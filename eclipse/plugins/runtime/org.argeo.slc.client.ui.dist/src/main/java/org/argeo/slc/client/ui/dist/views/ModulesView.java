package org.argeo.slc.client.ui.dist.views;

import java.util.Set;
import java.util.TreeSet;

import org.argeo.eclipse.ui.TreeParent;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class ModulesView extends ViewPart {
	private TreeViewer viewer;

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ModulesContentProvider());
		viewer.setLabelProvider(new ModulesLabelProvider());
		viewer.setInput(DistPlugin.getBundleContext());
	}

	@Override
	public void setFocus() {
		viewer.getTree().setFocus();
	}

	private class ModulesContentProvider implements ITreeContentProvider {

		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof BundleContext) {
				BundleContext bundleContext = (BundleContext) parentElement;
				Bundle[] bundles = bundleContext.getBundles();
				Set<ModuleNode> moduleNodes = new TreeSet<ModulesView.ModuleNode>();
				for (Bundle bundle : bundles) {
					if (bundle.getState() == Bundle.ACTIVE)
						moduleNodes.add(new ModuleNode(bundle));
				}
				return moduleNodes.toArray();
			} else {
				return null;
			}
		}

		public Object getParent(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean hasChildren(Object element) {
			return false;
		}

		public void dispose() {
			// TODO Auto-generated method stub

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub

		}

	}

	private class ModulesLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			return getText(element);
		}

	}

	private class ModuleNode extends TreeParent {
		private final Bundle bundle;

		public ModuleNode(Bundle bundle) {
			super(bundle.getSymbolicName());
			this.bundle = bundle;
		}

		public Bundle getBundle() {
			return bundle;
		}

	}
}
