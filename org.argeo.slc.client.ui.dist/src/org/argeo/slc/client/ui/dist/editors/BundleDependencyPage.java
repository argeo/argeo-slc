package org.argeo.slc.client.ui.dist.editors;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.argeo.eclipse.ui.dialogs.ErrorFeedback;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;
import org.argeo.slc.client.ui.dist.DistConstants;
import org.argeo.slc.client.ui.dist.DistImages;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/**
 * Present main information of a given OSGI bundle
 */
public class BundleDependencyPage extends FormPage implements SlcNames {
	// private final static Log log =
	// LogFactory.getLog(ArtifactDetailsPage.class);

	// Main business Objects
	private Node currBundle;

	// This page widgets
	private FormToolkit toolkit;

	public BundleDependencyPage(FormEditor editor, String title,
			Node currentNode) {
		super(editor, "id", title);
		this.currBundle = currentNode;
	}

	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		toolkit = managedForm.getToolkit();
		try {
			if (currBundle.hasProperty(DistConstants.SLC_BUNDLE_NAME))
				form.setText(currBundle.getProperty(
						DistConstants.SLC_BUNDLE_NAME).getString());
			Composite body = form.getBody();
			GridLayout layout = new GridLayout(1, false);
			layout.horizontalSpacing = layout.marginWidth = 0;
			layout.verticalSpacing = layout.marginHeight = 0;
			body.setLayout(layout);

			Composite part = toolkit.createComposite(body);
			createExportPackageSection(part);
			GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
			gd.heightHint = 180;
			part.setLayoutData(gd);

			part = toolkit.createComposite(body);
			createImportPackageSection(part);
			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			// gd.heightHint = 200;
			part.setLayoutData(gd);

			part = toolkit.createComposite(body);
			createReqBundleSection(part);
			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			part.setLayoutData(gd);

			managedForm.reflow(true);

		} catch (RepositoryException e) {
			throw new SlcException("unexpected error "
					+ "while creating bundle details page");
		}
	}

	// Workaround to add an artificial level to the export package browser
	private class LevelElem {
		private String label;
		private Object parent;

		public LevelElem(String label, Object parent) {
			this.label = label;
			this.parent = parent;
		}

		public String toString() {
			return label;
		}

		public Object getParent() {
			return parent;
		}
	}

	/** Export Package Section */
	private void createExportPackageSection(Composite parent)
			throws RepositoryException {
		parent.setLayout(new GridLayout());

		// Define the TableViewer

		Section section = addSection(parent, "Export packages");
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		TreeViewer viewer = new TreeViewer(section, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER);
		final Tree tree = viewer.getTree();
		tree.setHeaderVisible(false);
		tree.setLinesVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		TreeViewerColumn col = new TreeViewerColumn(viewer, SWT.FILL);
		col.getColumn().setWidth(400);

		col.setLabelProvider(new ColumnLabelProvider() {
			private static final long serialVersionUID = 1376400790495130862L;

			@Override
			public String getText(Object element) {
				if (element instanceof Node)
					return JcrUtils.get((Node) element, SlcNames.SLC_NAME);
				else
					return element.toString();
			}

			@Override
			public Image getImage(Object element) {
				if (element instanceof Node) {
					try {
						Node node = (Node) element;
						if (node.isNodeType(SlcTypes.SLC_EXPORTED_PACKAGE))
							return DistImages.IMG_PACKAGE;
					} catch (RepositoryException e) {
						throw new SlcException("Error retriving "
								+ "image for the labelProvider", e);
					}
				}
				return null;
			}
		});

		viewer.setContentProvider(new ITreeContentProvider() {
			private static final long serialVersionUID = 1898086304761992568L;

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

			public Object[] getElements(Object inputElement) {
				try {
					List<Node> nodes = JcrUtils.nodeIteratorToList(listNodes(
							currBundle, SlcTypes.SLC_EXPORTED_PACKAGE,
							SlcNames.SLC_NAME));
					return nodes.toArray();
				} catch (RepositoryException e) {
					throw new SlcException("Cannot list children Nodes", e);
				}
			}

			public Object[] getChildren(Object parentElement) {
				// Only 2 levels for the time being
				try {
					if (parentElement instanceof LevelElem) {
						Node node = (Node) ((LevelElem) parentElement)
								.getParent();
						List<Node> nodes = JcrUtils
								.nodeIteratorToList(listNodes(node,
										SlcTypes.SLC_JAVA_PACKAGE,
										SlcNames.SLC_NAME));
						return nodes.toArray();
					} else if (parentElement instanceof Node) {
						Node pNode = (Node) parentElement;
						if (pNode.isNodeType(SlcTypes.SLC_EXPORTED_PACKAGE)) {
							if (listNodes(pNode, SlcTypes.SLC_JAVA_PACKAGE,
									SlcNames.SLC_NAME).getSize() > 0) {
								Object[] result = { new LevelElem("uses", pNode) };
								return result;
							}
						}
					}
					return null;
				} catch (RepositoryException e) {
					throw new SlcException("Cannot list children Nodes", e);
				}
			}

			public Object getParent(Object element) {
				// useless
				return null;
			}

			public boolean hasChildren(Object element) {
				try {
					if (element instanceof LevelElem)
						return true;
					else {
						Node pNode = (Node) element;
						if (pNode.isNodeType(SlcTypes.SLC_EXPORTED_PACKAGE)) {
							return listNodes(pNode, SlcTypes.SLC_JAVA_PACKAGE,
									SlcNames.SLC_NAME).getSize() > 0;
						}
					}
					return false;
				} catch (RepositoryException e) {
					throw new SlcException("Cannot check children Nodes", e);
				}
			}
		});

		section.setClient(tree);
		viewer.setInput("Initialize");
		// work around a display problem : the tree table has only a few lines
		// when the tree is not expended
		// viewer.expandToLevel(2);
	}

	/** Import Package Section */
	private void createImportPackageSection(Composite parent)
			throws RepositoryException {
		parent.setLayout(new GridLayout());

		// Define the TableViewer
		// toolkit.createLabel(parent, "Import packages", SWT.NONE).setFont(
		// EclipseUiUtils.getBoldFont(parent));

		Section section = addSection(parent, "Import packages");
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		TableViewer viewer = new TableViewer(section, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER);

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Name
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(350);
		col.getColumn().setText("Name");
		col.setLabelProvider(new ColumnLabelProvider() {
			private static final long serialVersionUID = -7836022945221936898L;

			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_NAME);
			}

			public Image getImage(Object element) {
				return DistImages.IMG_PACKAGE;
			}

		});

		// Version
		col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(100);
		col.getColumn().setText("Version");
		col.setLabelProvider(new ColumnLabelProvider() {
			private static final long serialVersionUID = -8277731617775091641L;

			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_VERSION);
			}
		});

		// Optional
		col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(100);
		col.getColumn().setText("Optional");
		col.setLabelProvider(new ColumnLabelProvider() {
			private static final long serialVersionUID = -2388533169594840688L;

			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_OPTIONAL);
			}
		});

		viewer.setContentProvider(new TableContentProvider(
				SlcTypes.SLC_IMPORTED_PACKAGE, SLC_NAME));
		section.setClient(table);
		viewer.setInput("Initialize");
	}

	/** Required Bundle Section */
	private void createReqBundleSection(Composite parent)
			throws RepositoryException {
		parent.setLayout(new GridLayout());

		// Define the TableViewer
		Section section = addSection(parent, "Required bundles");
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// toolkit.createLabel(parent, "Required bundles", SWT.NONE).setFont(
		// EclipseUiUtils.getBoldFont(parent));
		TableViewer viewer = new TableViewer(section, SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.BORDER);

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Name
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(300);
		col.getColumn().setText("Name");
		col.setLabelProvider(new ColumnLabelProvider() {
			private static final long serialVersionUID = 4423640365819800247L;

			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_SYMBOLIC_NAME);
			}

			@Override
			public Image getImage(Object element) {
				return DistImages.IMG_BUNDLE;
			}
		});

		// Version
		col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(140);
		col.getColumn().setText("Version");
		col.setLabelProvider(new ColumnLabelProvider() {
			private static final long serialVersionUID = 1898477425996646270L;

			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_BUNDLE_VERSION);
			}
		});

		// Optional
		col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(100);
		col.getColumn().setText("Optional");
		col.setLabelProvider(new ColumnLabelProvider() {
			private static final long serialVersionUID = -7029999152302445581L;

			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_OPTIONAL);
			}
		});

		viewer.setContentProvider(new TableContentProvider(
				SlcTypes.SLC_REQUIRED_BUNDLE, SLC_SYMBOLIC_NAME));
		section.setClient(table);
		viewer.setInput("Initialize");
	}

	/**
	 * Build repository request
	 * 
	 * FIXME Workaround for remote repository, the path to bundleartifact (for
	 * instance
	 * .../org/argeo/slc/org.argeo.slc.client.ui.dist/1.1.12/org.argeo.slc
	 * .client.ui.dist-1.1.12/ ) is not valid for method factory.childNode(); it
	 * fails parsing the "1.1.12" part, trying to cast it as a BigDecimal
	 * 
	 * */
	private NodeIterator listNodes(Node parent, String nodeType, String orderBy)
			throws RepositoryException {
		// QueryManager queryManager = currBundle.getSession().getWorkspace()
		// .getQueryManager();
		// QueryObjectModelFactory factory = queryManager.getQOMFactory();
		//
		// final String nodeSelector = "nodes";
		// Selector source = factory.selector(nodeType, nodeSelector);
		//
		// Constraint childOf = factory.childNode(nodeSelector,
		// parent.getPath());
		//
		// Ordering order =
		// factory.ascending(factory.propertyValue(nodeSelector,
		// orderBy));
		// Ordering[] orderings = { order };
		//
		// QueryObjectModel query = factory.createQuery(source, childOf,
		// orderings, null);
		//
		// QueryResult result = query.execute();

		String pattern = null;
		if (SlcTypes.SLC_EXPORTED_PACKAGE.equals(nodeType))
			pattern = "slc:Export-Package*";
		else if (SlcTypes.SLC_JAVA_PACKAGE.equals(nodeType))
			pattern = "slc:uses*";
		else if (SlcTypes.SLC_IMPORTED_PACKAGE.equals(nodeType))
			pattern = "slc:Import-Package*";
		else if (SlcTypes.SLC_REQUIRED_BUNDLE.equals(nodeType))
			pattern = "slc:Require-Bundle*";

		return parent.getNodes(pattern);
	}

	private class TableContentProvider implements IStructuredContentProvider {
		private static final long serialVersionUID = 4133284637336320455L;
		private String nodeType;
		private String orderBy;

		TableContentProvider(String nodeType, String orderBy) {
			this.nodeType = nodeType;
			this.orderBy = orderBy;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object arg0) {
			try {
				List<Node> nodes = JcrUtils.nodeIteratorToList(listNodes(
						currBundle, nodeType, orderBy));
				return nodes.toArray();
			} catch (RepositoryException e) {
				ErrorFeedback.show("Cannot list children Nodes", e);
				return null;
			}
		}
	}

	/* HELPERS */
	private Section addSection(Composite parent, String title) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setText(title);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		return section;
	}

}