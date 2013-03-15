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

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Selector;

import org.argeo.ArgeoException;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistConstants;
import org.argeo.slc.client.ui.dist.DistImages;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.argeo.slc.repo.RepoConstants;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * Exposes the various group id of a distribution as a tree.
 */
public class ArtifactsBrowserPage extends FormPage implements DistConstants,
		RepoConstants {

	final static String PAGE_ID = "artifactsBrowserPage";
	// private final static Log log = LogFactory
	// .getLog(ArtifactsBrowserPage.class);

	// Business object
	private Session session;

	// This page widgets
	private TreeViewer artifactTreeViewer;

	public ArtifactsBrowserPage(FormEditor editor, String title, Session session) {
		super(editor, PAGE_ID, title);
		this.session = session;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		try {
			ScrolledForm form = managedForm.getForm();
			Composite parent = form.getBody();
			// Enable the different parts to fill the whole page when the tab is
			// maximized
			parent.setLayout(new FillLayout());
			createExportPackageSection(parent);
			getEditor().getSite().setSelectionProvider(artifactTreeViewer);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot create artifact browser page", e);
		}
	}

	private NodeIterator listNodes(String nodeType, String orderBy)
			throws RepositoryException {
		QueryManager queryManager = session.getWorkspace().getQueryManager();
		QueryObjectModelFactory factory = queryManager.getQOMFactory();

		final String nodeSelector = "nodes";
		Selector source = factory.selector(nodeType, nodeSelector);

		Ordering order = factory.ascending(factory.propertyValue(nodeSelector,
				orderBy));
		Ordering[] orderings = { order };

		QueryObjectModel query = factory.createQuery(source, null, orderings,
				null);

		QueryResult result = query.execute();

		return result.getNodes();
	}

	/** Export Package Section */
	private void createExportPackageSection(Composite parent)
			throws RepositoryException {

		int style = SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.BORDER;
		Tree tree = new Tree(parent, style);
		createColumn(tree, "Artifacts", SWT.LEFT, 300);
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);

		artifactTreeViewer = new TreeViewer(tree);

		artifactTreeViewer.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Node node = (Node) element;
				try {
					if (node.isNodeType(SlcTypes.SLC_GROUP_BASE))
						return JcrUtils.get((Node) element,
								SlcNames.SLC_GROUP_BASE_ID);
					else if (node.isNodeType(SlcTypes.SLC_ARTIFACT_BASE))
						return JcrUtils.get((Node) element,
								SlcNames.SLC_ARTIFACT_ID);
					else
						return node.getName();
				} catch (RepositoryException e) {
					throw new SlcException("Cannot browse artifacts", e);
				}
			}

			@Override
			public Image getImage(Object element) {
				Node node = (Node) element;
				try {

					if (node.isNodeType(SlcTypes.SLC_GROUP_BASE))
						return DistImages.IMG_WKSP;
					else if (node.isNodeType(SlcTypes.SLC_ARTIFACT_BASE))
						return DistImages.IMG_ARTIFACT_BASE;
					else if (node
							.isNodeType(SlcTypes.SLC_ARTIFACT_VERSION_BASE))
						return DistImages.IMG_ARTIFACT_VERSION_BASE;
					else
						return null;
				} catch (RepositoryException e) {
					throw new SlcException("Cannot get images for artifacts", e);
				}
			}
		});

		artifactTreeViewer.setContentProvider(new ITreeContentProvider() {

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

			public Object[] getElements(Object inputElement) {
				try {
					List<Node> nodes = JcrUtils.nodeIteratorToList(listNodes(
							SlcTypes.SLC_GROUP_BASE, SlcNames.SLC_NAME));
					return nodes.toArray();
				} catch (RepositoryException e) {
					throw new SlcException("Cannot list children Nodes", e);
				}
			}

			public Object[] getChildren(Object parentElement) {
				// Only 3 levels for the time being
				try {
					Node pNode = (Node) parentElement;
					if (pNode.isNodeType(SlcTypes.SLC_GROUP_BASE)) {
						return getArtifactBase(pNode,
								SlcTypes.SLC_ARTIFACT_BASE);
					} else if (pNode.isNodeType(SlcTypes.SLC_ARTIFACT_BASE)) {
						return getArtifactBase(pNode,
								SlcTypes.SLC_ARTIFACT_VERSION_BASE);
					}
					return null;
				} catch (RepositoryException e) {
					throw new SlcException("Cannot list children Nodes", e);
				}
			}

			// Helper to get children because current version of Jackrabbit is
			// buggy in remote
			private Object[] getArtifactBase(Node parent, String nodeType)
					throws RepositoryException {
				List<Node> nodes = new ArrayList<Node>();
				NodeIterator ni = parent.getNodes();
				while (ni.hasNext()) {
					Node node = ni.nextNode();
					if (node.isNodeType(nodeType))
						nodes.add(node);
				}
				return nodes.toArray();
			}

			public Object getParent(Object element) {
				return null;
			}

			public boolean hasChildren(Object element) {
				try {
					Node pNode = (Node) element;
					if (pNode.isNodeType(SlcTypes.SLC_GROUP_BASE)
							|| pNode.isNodeType(SlcTypes.SLC_ARTIFACT_BASE)) {
						// might return true even if there is no "valid" child
						return pNode.hasNodes();
					} else
						return false;
				} catch (RepositoryException e) {
					throw new SlcException("Cannot check children Nodes", e);
				}
			}
		});

		artifactTreeViewer.addDoubleClickListener(new DoubleClickListener());

		artifactTreeViewer.setInput("Initialize");
	}

	private class DoubleClickListener implements IDoubleClickListener {

		public void doubleClick(DoubleClickEvent event) {
			Object obj = ((IStructuredSelection) event.getSelection())
					.getFirstElement();
			try {
				if (obj instanceof Node) {
					Node node = (Node) obj;
					if (node.isNodeType(SlcTypes.SLC_ARTIFACT_VERSION_BASE)) {
						// TODO fix using QOM after jcr upgrade
						NodeIterator ni = node.getNodes();
						while (ni.hasNext()) {
							Node curr = ni.nextNode();
							if (curr.isNodeType(SlcTypes.SLC_BUNDLE_ARTIFACT)) {
								GenericBundleEditorInput gaei = new GenericBundleEditorInput(
										curr);
								DistPlugin
										.getDefault()
										.getWorkbench()
										.getActiveWorkbenchWindow()
										.getActivePage()
										.openEditor(gaei,
												GenericBundleEditor.ID);
							}
						}
					}
				}
			} catch (RepositoryException re) {
				throw new ArgeoException(
						"Repository error while getting node info", re);
			} catch (PartInitException pie) {
				throw new ArgeoException(
						"Unexepected exception while opening artifact editor",
						pie);
			}
		}
	}

	private static TreeColumn createColumn(Tree parent, String name, int style,
			int width) {
		TreeColumn result = new TreeColumn(parent, style);
		result.setText(name);
		result.setWidth(width);
		result.setMoveable(true);
		result.setResizable(true);
		return result;
	}

}
