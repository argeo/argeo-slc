package org.argeo.cms.ui.workbench.internal.jcr.parts;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

import org.argeo.cms.ui.jcr.PropertyLabelProvider;
import org.argeo.cms.ui.workbench.WorkbenchUiPlugin;
import org.argeo.cms.ui.workbench.internal.WorkbenchConstants;
import org.argeo.eclipse.ui.EclipseUiException;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * Generic editor property page. Lists all properties of current node as a
 * complex tree. TODO: enable editing
 */
public class GenericPropertyPage extends FormPage implements WorkbenchConstants {
	// private final static Log log =
	// LogFactory.getLog(GenericPropertyPage.class);

	// Main business Objects
	private Node currentNode;

	public GenericPropertyPage(FormEditor editor, String title, Node currentNode) {
		super(editor, "id", title);
		this.currentNode = currentNode;
	}

	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		form.setText(WorkbenchUiPlugin.getMessage("genericNodePageTitle"));
		Composite innerBox = form.getBody();
		//Composite innerBox = new Composite(body, SWT.NO_FOCUS);
		FillLayout layout = new FillLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		innerBox.setLayout(layout);
		createComplexTree(innerBox);
		// TODO TreeColumnLayout triggers a scroll issue with the form:
		// The inside body is always to big and a scroll bar is shown
		// Composite tableCmp = new Composite(body, SWT.NO_FOCUS);
		// createComplexTree(tableCmp);
	}

	private TreeViewer createComplexTree(Composite parent) {
		int style = SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION;
		Tree tree = new Tree(parent, style);
		TreeColumnLayout tableColumnLayout = new TreeColumnLayout();

		createColumn(tree, tableColumnLayout, "Property", SWT.LEFT, 200, 30);
		createColumn(tree, tableColumnLayout, "Value(s)", SWT.LEFT, 300, 60);
		createColumn(tree, tableColumnLayout, "Type", SWT.LEFT, 75, 10);
		createColumn(tree, tableColumnLayout, "Attributes", SWT.LEFT, 75, 0);
		// Do not apply the treeColumnLayout it does not work yet
		// parent.setLayout(tableColumnLayout);

		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);

		TreeViewer treeViewer = new TreeViewer(tree);
		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.setLabelProvider(new PropertyLabelProvider());
		treeViewer.setInput(currentNode);
		treeViewer.expandAll();
		return treeViewer;
	}

	private static TreeColumn createColumn(Tree parent, TreeColumnLayout tableColumnLayout, String name, int style,
			int width, int weight) {
		TreeColumn column = new TreeColumn(parent, style);
		column.setText(name);
		column.setWidth(width);
		column.setMoveable(true);
		column.setResizable(true);
		tableColumnLayout.setColumnData(column, new ColumnWeightData(weight, width, true));
		return column;
	}

	private class TreeContentProvider implements ITreeContentProvider {
		private static final long serialVersionUID = -6162736530019406214L;

		public Object[] getElements(Object parent) {
			Object[] props = null;
			try {

				if (parent instanceof Node) {
					Node node = (Node) parent;
					PropertyIterator pi;
					pi = node.getProperties();
					List<Property> propList = new ArrayList<Property>();
					while (pi.hasNext()) {
						propList.add(pi.nextProperty());
					}
					props = propList.toArray();
				}
			} catch (RepositoryException e) {
				throw new EclipseUiException("Unexpected exception while listing node properties", e);
			}
			return props;
		}

		public Object getParent(Object child) {
			return null;
		}

		public Object[] getChildren(Object parent) {
			if (parent instanceof Property) {
				Property prop = (Property) parent;
				try {
					if (prop.isMultiple())
						return prop.getValues();
				} catch (RepositoryException e) {
					throw new EclipseUiException("Cannot get multi-prop values on " + prop, e);
				}
			}
			return null;
		}

		public boolean hasChildren(Object parent) {
			try {
				return (parent instanceof Property && ((Property) parent).isMultiple());
			} catch (RepositoryException e) {
				throw new EclipseUiException("Cannot check if property is multiple for " + parent, e);
			}
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}
	}
}
