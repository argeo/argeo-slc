/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.client.ui.dist.editors;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Selector;

import org.argeo.ArgeoException;
import org.argeo.eclipse.ui.ErrorFeedback;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistConstants;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableLayout;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/**
 * Presents main information of a given OSGI bundle
 */

public class BundleDetailsPage extends FormPage implements SlcNames, SlcTypes {
	// private final static Log log =
	// LogFactory.getLog(ArtifactDetailsPage.class);

	// Main business Objects
	private Node currBundle;

	// This page widgets
	private Text mavenSnippet;
	private FormToolkit toolkit;

	public BundleDetailsPage(FormEditor editor, String title, Node currentNode) {
		super(editor, "id", title);
		this.currBundle = currentNode;
	}

	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		toolkit = managedForm.getToolkit();
		try {
			form.setText(currBundle.hasProperty(DistConstants.SLC_BUNDLE_NAME) ? currBundle
					.getProperty(DistConstants.SLC_BUNDLE_NAME).getString()
					: "");
			Composite body = form.getBody();
			GridLayout layout = new GridLayout(1, false);
			layout.marginWidth = 5;
			layout.verticalSpacing = 15;

			body.setLayout(layout);
			createdetailsPart(body);

			createExportPackageSection(body);
			createImportPackageSection(body);
			createReqBundleSection(body);
			createMavenSnipet(body);

		} catch (RepositoryException e) {
			throw new SlcException("unexpected error "
					+ "while creating bundle details page");
		}
	}

	/** Add useful info for the current bundle */
	private void createdetailsPart(Composite parent) throws RepositoryException {
		Composite details = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 20;
		details.setLayout(layout);

		createField(details, "Symbolic name", SlcNames.SLC_SYMBOLIC_NAME);
		createField(details, "Version", SlcNames.SLC_BUNDLE_VERSION);
		createField(details, "Group Id", SlcNames.SLC_GROUP_ID);
		// Single sourcing issue: this does not works with rap
		// createHyperlink(details, "Licence",
		// DistConstants.SLC_BUNDLE_LICENCE);
		createField(details, "Licence", DistConstants.SLC_BUNDLE_LICENCE);

		createField(details, "Vendor", DistConstants.SLC_BUNDLE_VENDOR);

	}

	/** Import Package Section */
	private void createExportPackageSection(Composite parent)
			throws RepositoryException {

		// Define the TableViewer
		Section headerSection = addSection(parent, "Export packages");
		// TreeViewer viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL
		// | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		TreeViewer viewer = new TreeViewer(headerSection, SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		final Tree tree = viewer.getTree();
		tree.setHeaderVisible(false);
		tree.setLinesVisible(true);
		GridData gd = new GridData(SWT.LEFT, SWT.TOP, true, true);
		gd.heightHint = 300;
		tree.setLayoutData(gd);

		TreeViewerColumn col = new TreeViewerColumn(viewer, SWT.FILL);
		col.getColumn().setWidth(200);

		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SlcNames.SLC_NAME);
			}

			@Override
			public Image getImage(Object element) {
				return null;
			}
		});

		viewer.setContentProvider(new ITreeContentProvider() {

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
					Node pNode = (Node) parentElement;
					if (pNode.isNodeType(SlcTypes.SLC_EXPORTED_PACKAGE)) {
						List<Node> nodes = JcrUtils
								.nodeIteratorToList(listNodes(pNode,
										SlcTypes.SLC_JAVA_PACKAGE,
										SlcNames.SLC_NAME));
						return nodes.toArray();
					} else
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
					Node pNode = (Node) element;
					if (pNode.isNodeType(SlcTypes.SLC_EXPORTED_PACKAGE)) {
						// might return true even if there is no "valid" child
						return pNode.hasNodes();
					} else
						return false;
				} catch (RepositoryException e) {
					throw new SlcException("Cannot check children Nodes", e);
				}
			}
		});

		headerSection.setClient(tree);
		viewer.setInput("Initialize");
	}

	/** Import Package Section */
	private void createImportPackageSection(Composite parent)
			throws RepositoryException {

		// Define the TableViewer
		Section headerSection = addSection(parent, "Import packages");
		TableViewer viewer = new TableViewer(headerSection, SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		// Name
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(300);
		col.getColumn().setText("Name");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_NAME);
			}
		});

		// Version
		col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(100);
		col.getColumn().setText("Version");
		col.setLabelProvider(new ColumnLabelProvider() {
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
			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_OPTIONAL);
			}
		});

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		GridData gd = new GridData(SWT.LEFT, SWT.TOP, true, true);
		gd.heightHint = 300;
		table.setLayoutData(gd);

		viewer.setContentProvider(new TableContentProvider(
				SLC_IMPORTED_PACKAGE, SLC_NAME));
		headerSection.setClient(viewer.getTable());

		viewer.setInput("Initialize");
	}

	/** Required Bundle Section */
	private void createReqBundleSection(Composite parent)
			throws RepositoryException {

		// Define the TableViewer
		Section headerSection = addSection(parent, "Required bundles");
		TableViewer viewer = new TableViewer(headerSection, SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		// Name
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(300);
		col.getColumn().setText("Name");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_SYMBOLIC_NAME);
			}
		});

		// Version
		col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(100);
		col.getColumn().setText("Version");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_BUNDLE_VERSION);
			}
		});

		// Version
		col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(100);
		col.getColumn().setText("Optional");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return JcrUtils.get((Node) element, SLC_OPTIONAL);
			}
		});

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new TableContentProvider(SLC_REQUIRED_BUNDLE,
				SLC_SYMBOLIC_NAME));
		headerSection.setClient(viewer.getTable());

		viewer.setInput("Initialize");
	}

	/** Build repository request */
	private NodeIterator listNodes(Node parent, String nodeType, String orderBy)
			throws RepositoryException {
		QueryManager queryManager = currBundle.getSession().getWorkspace()
				.getQueryManager();
		QueryObjectModelFactory factory = queryManager.getQOMFactory();

		final String nodeSelector = "nodes";
		Selector source = factory.selector(nodeType, nodeSelector);

		Constraint childOf = factory.childNode(nodeSelector, parent.getPath());

		Ordering order = factory.ascending(factory.propertyValue(nodeSelector,
				orderBy));
		Ordering[] orderings = { order };

		QueryObjectModel query = factory.createQuery(source, childOf,
				orderings, null);

		QueryResult result = query.execute();
		return result.getNodes();

	}

	private class TableContentProvider implements IStructuredContentProvider {
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
	private void createField(Composite parent, String label, String jcrPropName)
			throws RepositoryException {
		toolkit.createLabel(parent, label, SWT.NONE);
		Text txt = toolkit.createText(parent, "", SWT.SINGLE);
		txt.setText(currBundle.hasProperty(jcrPropName) ? currBundle
				.getProperty(jcrPropName).getString() : "");
		txt.setEditable(false);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		txt.setLayoutData(gd);
	}

	private Section addSection(Composite parent, String title) {
		Section section = toolkit.createSection(parent, Section.TWISTIE);
		section.setText(title);
		section.setExpanded(false);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		section.setLayoutData(gd);
		Composite body = new Composite(section, SWT.FILL);
		section.setClient(body);
		// Layout
		body.setLayout(new TableLayout());
		return section;
	}

	// private void createHyperlink(Composite parent, String label,
	// String jcrPropName) throws RepositoryException {
	// toolkit.createLabel(parent, label, SWT.NONE);
	// if (currBundle.hasProperty(jcrPropName)) {
	// final Hyperlink link = toolkit.createHyperlink(parent, currBundle
	// .getProperty(jcrPropName).getString(), SWT.NONE);
	// link.addHyperlinkListener(new AbstractHyperlinkListener() {
	// @Override
	// public void linkActivated(HyperlinkEvent e) {
	// try {
	// IWorkbenchBrowserSupport browserSupport = PlatformUI
	// .getWorkbench().getBrowserSupport();
	// IWebBrowser browser = browserSupport
	// .createBrowser(
	// IWorkbenchBrowserSupport.LOCATION_BAR
	// | IWorkbenchBrowserSupport.NAVIGATION_BAR,
	// "SLC Distribution browser",
	// "SLC Distribution browser",
	// "A tool tip");
	// browser.openURL(new URL(link.getText()));
	// } catch (Exception ex) {
	//						throw new SlcException("error opening browser", ex); //$NON-NLS-1$
	// }
	// }
	// });
	// } else
	// toolkit.createLabel(parent, "", SWT.NONE);
	// }

	/** Creates a text area with corresponding maven snippet */
	private void createMavenSnipet(Composite parent) {
		mavenSnippet = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		gd.heightHint = 100;
		mavenSnippet.setLayoutData(gd);
		mavenSnippet.setText(generateXmlSnippet());
	}

	// Helpers
	private String generateXmlSnippet() {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("<dependency>\n");
			sb.append("\t<groupeId>");
			sb.append(currBundle.getProperty(SLC_GROUP_ID).getString());
			sb.append("</groupeId>\n");
			sb.append("\t<artifactId>");
			sb.append(currBundle.getProperty(SLC_ARTIFACT_ID).getString());
			sb.append("</artifactId>\n");
			sb.append("\t<version>");
			sb.append(currBundle.getProperty(SLC_ARTIFACT_VERSION).getString());
			sb.append("</version>\n");
			sb.append("</dependency>");
			return sb.toString();
		} catch (RepositoryException re) {
			throw new ArgeoException(
					"unexpected error while generating maven snippet");
		}
	}
}