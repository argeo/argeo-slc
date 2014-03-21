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
package org.argeo.slc.client.ui.dist.views;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.ArgeoException;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.client.ui.dist.DistConstants;
import org.argeo.slc.client.ui.dist.DistImages;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.controllers.ArtifactsTreeContentProvider;
import org.argeo.slc.jcr.SlcTypes;
import org.argeo.slc.repo.RepoConstants;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;

/**
 * Basic View to browse a maven based repository.
 * 
 * By Default size of the various bundles is not computed but it can be
 * activated the view command.
 */

public class ArtifactsBrowser extends ViewPart implements DistConstants,
		RepoConstants {
	// private final static Log log = LogFactory.getLog(ArtifactsBrowser.class);
	public final static String ID = DistPlugin.ID + ".artifactsBrowser";

	/* DEPENDENCY INJECTION */
	private Session jcrSession;

	// Business objects
	private Node rootNode;

	// This page widgets
	private TreeViewer artifactTreeViewer;
	private boolean isSizeVisible = false;

	// To be able to configure columns easily
	public static final int COLUMN_TREE = 0;
	public static final int COLUMN_DATE = 1;
	public static final int COLUMN_SIZE = 2;
	private static final int SIZE_COL_WIDTH = 55;

	@Override
	public void createPartControl(Composite parent) {
		// Enable the different parts to fill the whole page when the tab is
		// maximized
		parent.setLayout(new FillLayout());
		artifactTreeViewer = createArtifactsTreeViewer(parent);

		// context menu : it is completely defined in the plugin.xml file.
		// Nothing in the context menu for the time being
		// MenuManager menuManager = new MenuManager();
		// Menu menu =
		// menuManager.createContextMenu(artifactTreeViewer.getTree());
		// artifactTreeViewer.getTree().setMenu(menu);
		// getSite().registerContextMenu(menuManager, artifactTreeViewer);

		getSite().setSelectionProvider(artifactTreeViewer);
		// packagesViewer.setComparer(new NodeViewerComparer());

		// Model initialisation
		if (jcrSession != null) {
			try {
				rootNode = jcrSession.getNode(DEFAULT_ARTIFACTS_BASE_PATH);
				artifactTreeViewer.setInput(rootNode);
			} catch (RepositoryException e) {
				throw new ArgeoException("Cannot load base artifact nodes", e);
			}
		}
	}

	protected TreeViewer createArtifactsTreeViewer(Composite parent) {
		int style = SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION;
		Tree tree = new Tree(parent, style);
		createColumn(tree, "Artifacts", SWT.LEFT, 300);
		createColumn(tree, "Date created", SWT.LEFT, 105);
		createColumn(tree, "Size", SWT.RIGHT, 0);
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);

		TreeViewer viewer = new TreeViewer(tree);

		viewer.setContentProvider(new ArtifactsTreeContentProvider());
		viewer.setLabelProvider(new ArtifactLabelProvider());
		viewer.addSelectionChangedListener(new ArtifactTreeSelectionListener());
		// viewer.addDoubleClickListener(new GenericDoubleClickListener());
		viewer.setInput(rootNode);

		return viewer;
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

	protected TreeViewer getArtifactTreeViewer() {
		return artifactTreeViewer;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/**
	 * Refresh the given element of the tree browser. If null is passed as a
	 * parameter, it refreshes the whole tree
	 */
	public void refresh(Object element) {
		if (element == null) {
			artifactTreeViewer.refresh(rootNode);
		} else
			artifactTreeViewer.refresh(element);
	}

	/** Returns wether size column is visible or not */
	public boolean isSizeVisible() {
		return isSizeVisible;
	}

	/** Sets the visibility of the size column */
	public void setSizeVisible(boolean visible) {
		if (isSizeVisible == visible)
			return; // nothing has changed
		else
			isSizeVisible = visible;

		if (visible) {
			artifactTreeViewer.getTree().getColumn(COLUMN_SIZE)
					.setWidth(SIZE_COL_WIDTH);
		} else {
			// we just hide the column, we don't refresh the whole tree.
			artifactTreeViewer.getTree().getColumn(COLUMN_SIZE).setWidth(0);
		}
	}

	private class ArtifactLabelProvider extends ColumnLabelProvider implements
			DistConstants, SlcTypes {

		// Utils
		protected DateFormat timeFormatter = new SimpleDateFormat(
				DATE_TIME_FORMAT);

		public void update(ViewerCell cell) {
			int colIndex = cell.getColumnIndex();
			Object element = cell.getElement();
			cell.setText(getColumnText(element, colIndex));

			if (element instanceof Node && colIndex == 0) {
				Node node = (Node) element;
				try {
					if (node.isNodeType(SLC_ARTIFACT_BASE))
						cell.setImage(DistImages.IMG_ARTIFACT_BASE);
					else if (node.isNodeType(SLC_ARTIFACT_VERSION_BASE))
						cell.setImage(DistImages.IMG_ARTIFACT_VERSION_BASE);
				} catch (RepositoryException e) {
					// Silent
				}
			}
		}

		@Override
		public Image getImage(Object element) {

			if (element instanceof Node) {
				Node node = (Node) element;
				try {
					if (node.isNodeType(SLC_ARTIFACT_BASE)) {
						return DistImages.IMG_ARTIFACT_BASE;
					} else if (node.isNodeType(SLC_ARTIFACT_VERSION_BASE)) {
						return DistImages.IMG_ARTIFACT_VERSION_BASE;
					}
				} catch (RepositoryException e) {
					// Silent
				}
			}
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			try {
				if (element instanceof Node) {
					Node node = (Node) element;
					switch (columnIndex) {
					case COLUMN_TREE:
						return node.getName();
					case COLUMN_SIZE:
						if (isSizeVisible) {
							long size = JcrUtils.getNodeApproxSize(node) / 1024;
							if (size > 1024)
								return size / 1024 + " MB";
							else
								return size + " KB";
						} else
							return "";
					case COLUMN_DATE:
						if (node.hasProperty(Property.JCR_CREATED))
							return timeFormatter.format(node
									.getProperty(Property.JCR_CREATED)
									.getDate().getTime());
						else
							return null;
					}
				}
			} catch (RepositoryException re) {
				throw new ArgeoException(
						"Unexepected error while getting property values", re);
			}
			return null;
		}

		// private String formatValueAsString(Value value) {
		// // TODO enhance this method
		// try {
		// String strValue;
		//
		// if (value.getType() == PropertyType.BINARY)
		// strValue = "<binary>";
		// else if (value.getType() == PropertyType.DATE)
		// strValue = timeFormatter.format(value.getDate().getTime());
		// else
		// strValue = value.getString();
		// return strValue;
		// } catch (RepositoryException e) {
		// throw new ArgeoException(
		// "unexpected error while formatting value", e);
		// }
		// }
	}

	private class ArtifactTreeSelectionListener implements
			ISelectionChangedListener {

		public void selectionChanged(SelectionChangedEvent event) {
			ISelection selection = event.getSelection();
			if (selection != null && selection instanceof IStructuredSelection) {
				IStructuredSelection iss = (IStructuredSelection) selection;
				if (iss.size() == 1) {
					artifactTreeViewer.refresh(iss.getFirstElement());
				}
			}

		}

	}

	/* DEPENDENCY INJECTION */
	public void setJcrSession(Session jcrSession) {
		this.jcrSession = jcrSession;
	}
}
