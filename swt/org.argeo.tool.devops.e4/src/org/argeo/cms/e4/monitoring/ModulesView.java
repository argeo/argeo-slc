package org.argeo.cms.e4.monitoring;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.argeo.cms.ux.widgets.TreeParent;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/** The OSGi runtime from a module perspective. */
public class ModulesView {
	private final static BundleContext bc = FrameworkUtil.getBundle(ModulesView.class).getBundleContext();
	private TreeViewer viewer;

	@PostConstruct
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ModulesContentProvider());
		viewer.setLabelProvider(new ModulesLabelProvider());
		viewer.setInput(bc);
	}

	@Focus
	public void setFocus() {
		viewer.getTree().setFocus();
	}

	private class ModulesContentProvider implements ITreeContentProvider {
		private static final long serialVersionUID = 3819934804640641721L;

		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof BundleContext) {
				BundleContext bundleContext = (BundleContext) parentElement;
				Bundle[] bundles = bundleContext.getBundles();

				List<BundleNode> modules = new ArrayList<BundleNode>();
				for (Bundle bundle : bundles) {
					if (bundle.getState() == Bundle.ACTIVE)
						modules.add(new BundleNode(bundle, true));
				}
				return modules.toArray();
			} else if (parentElement instanceof TreeParent) {
				return ((TreeParent) parentElement).getChildren();
			} else {
				return null;
			}
		}

		public Object getParent(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof TreeParent) {
				return ((TreeParent) element).hasChildren();
			}
			return false;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	private class ModulesLabelProvider extends StateLabelProvider {
		private static final long serialVersionUID = 5290046145534824722L;

		@Override
		public String getText(Object element) {
			if (element instanceof BundleNode)
				return element.toString() + " [" + ((BundleNode) element).getBundle().getBundleId() + "]";
			return element.toString();
		}
	}
}
