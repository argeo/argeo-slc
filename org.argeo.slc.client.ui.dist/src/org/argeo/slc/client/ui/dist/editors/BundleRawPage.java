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
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeType;

import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;
import org.argeo.slc.client.ui.dist.DistImages;
import org.argeo.slc.client.ui.dist.utils.DistUiHelpers;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/** List all properties of current bundle as a tree */
public class BundleRawPage extends FormPage implements SlcNames, SlcTypes {
	// private final static Log log =
	// LogFactory.getLog(ArtifactDetailsPage.class);

	// Main business Objects
	private Node currBundle;

	// This page widgets
	private TreeViewer complexTree;

	public BundleRawPage(FormEditor editor, String title, Node currentNode) {
		super(editor, "id", title);
		this.currBundle = currentNode;
	}

	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 5;
		form.getBody().setLayout(layout);
		createViewer(form.getBody());
	}

	private void createViewer(Composite parent) {

		// Create the viewer
		int style = SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION;
		Tree tree = new Tree(parent, style);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		tree.setLayoutData(gd);
		createColumn(tree, "Attribute", SWT.LEFT, 200);
		createColumn(tree, "Value", SWT.LEFT, 200);
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);

		complexTree = new TreeViewer(tree);
		complexTree.setContentProvider(new TreeContentProvider());
		complexTree.setLabelProvider(new TreeLabelProvider());

		// Initialize
		complexTree.setInput(currBundle);
		// result.expandAll();
		complexTree.expandToLevel(2);

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

	// View specific object
	private class ViewSpecificItems {
		private String key;
		private Object value;
		private boolean isFolder;
		private Node curNode;

		public ViewSpecificItems(String key, Object value, boolean isFolder) {
			this.key = key;
			this.value = value;
			this.isFolder = isFolder;
		}

		public String getKey() {
			return key;
		}

		public void setNode(Node node) {
			this.curNode = node;
		}

		public Node getNode() {
			return curNode;
		}

		public Object getValue() {
			return value;
		}

		public boolean isFolder() {
			return isFolder;
		}

	}

	// providers
	private class TreeLabelProvider extends ColumnLabelProvider implements
			SlcTypes, SlcNames {
		private static final long serialVersionUID = -6385445983392621937L;

		public void update(ViewerCell cell) {
			try {

				int colIndex = cell.getColumnIndex();
				Object element = cell.getElement();
				if (element instanceof Property) {
					Property prop = (Property) element;
					if (colIndex == 0)
						cell.setText(DistUiHelpers.getLabelJcrName(prop
								.getName()));
					else if (colIndex == 1)
						cell.setText(DistUiHelpers.formatValueAsString(prop
								.getValue()));

				} else if (element instanceof ViewSpecificItems) {
					if (colIndex == 0)
						cell.setText(((ViewSpecificItems) element).getKey());
					else if (colIndex == 1)
						cell.setText(DistUiHelpers
								.formatAsString(((ViewSpecificItems) element)
										.getValue()));

				} else if (element instanceof Node) {
					Node node = (Node) element;
					if (colIndex == 0) {
						if (node.isNodeType(NodeType.NT_FILE)) {
							cell.setImage(DistImages.IMG_FILE);
							cell.setText(node.getName());
						} else if (node.isNodeType(SLC_IMPORTED_PACKAGE))
							cell.setText("Import package");
						else if (node.isNodeType(SLC_EXPORTED_PACKAGE))
							cell.setText("Export package");

					} else if (colIndex == 1) {
						if (node.isNodeType(SLC_ARTIFACT)) {
							StringBuffer sb = new StringBuffer("");
							if (node.hasProperty(SLC_ARTIFACT_CLASSIFIER)) {
								sb.append(node.getProperty(
										SLC_ARTIFACT_CLASSIFIER).getString());
								sb.append(" ");
							}
							if (node.hasProperty(SLC_ARTIFACT_EXTENSION))
								sb.append(node.getProperty(
										SLC_ARTIFACT_EXTENSION).getString());
							cell.setText(sb.toString());
						} else if (node.isNodeType(SLC_IMPORTED_PACKAGE)
								|| node.isNodeType(SLC_EXPORTED_PACKAGE))
							cell.setText(node.getProperty(SLC_NAME).getString());
					}
				}
			} catch (RepositoryException e) {
				throw new SlcException(
						"unexpected error while getting artifact information",
						e);
			}
		}
	}

	private class TreeContentProvider implements ITreeContentProvider {
		private static final long serialVersionUID = -4315686158836938052L;

		public Object[] getElements(Object parent) {
			List<Object> elements = new ArrayList<Object>();

			try {
				Node node = (Node) parent;
				elements = new ArrayList<Object>();

				// Maven coordinates
//				elements.add(node.getProperty(SLC_GROUP_ID));
//				elements.add(node.getProperty(SLC_ARTIFACT_ID));
//				elements.add(node.getProperty(SLC_ARTIFACT_VERSION));

				// Meta information
				// boolean gotSource = false;
				// // TODO: implement this check
				// elements.add(new ViewSpecificItems("Sources available",
				// gotSource));

				// Jars
				NodeIterator ni = node.getNodes();
				while (ni.hasNext()) {
					Node child = ni.nextNode();
					if (child.isNodeType(SLC_ARTIFACT)) {
						// we skip sha1 files for the time being.
						elements.add(child);
					}
				}

				// Properties
				PropertyIterator pi = node.getProperties();
				while (pi.hasNext()) {
					Property curProp = pi.nextProperty();
					if (!curProp.getName().startsWith("jcr:")
							&& !curProp.isMultiple())
						elements.add(curProp);
				}

			} catch (RepositoryException e) {
				throw new SlcException(
						"Unexpected exception while listing node properties", e);
			}
			return elements.toArray();
		}

		public Object getParent(Object child) {
			return null;
		}

		public Object[] getChildren(Object parent) {
			Object[] result = null;
			try {
				if (parent instanceof Property) {
					Property prop = (Property) parent;
					if (prop.isMultiple()) {
						Value[] values = prop.getValues();
						return values;
					}
				} else if (parent instanceof Node) {
					Node node = (Node) parent;
					if (node.hasNodes()) {
						List<Object> elements = new ArrayList<Object>();
						PropertyIterator pi = node.getProperties();
						while (pi.hasNext()) {
							Property curProp = pi.nextProperty();
							if (!curProp.getName().startsWith("jcr:")
									&& !curProp.isMultiple())
								elements.add(curProp);
						}

						NodeIterator ni = node.getNodes();
						while (ni.hasNext()) {
							Node curNode = ni.nextNode();
							if (curNode.isNodeType(SLC_IMPORTED_PACKAGE)
									|| curNode.isNodeType(SLC_EXPORTED_PACKAGE)) {
								ViewSpecificItems vsi = new ViewSpecificItems(
										"Bundle dependencies", "", true);
								vsi.setNode(node);
								elements.add(vsi);
								break;
							}
						}
						return elements.toArray();
					}
				} else if (parent instanceof ViewSpecificItems
						&& ((ViewSpecificItems) parent).isFolder()) {
					NodeIterator ni = ((ViewSpecificItems) parent).getNode()
							.getNodes();
					List<Node> elements = new ArrayList<Node>();
					while (ni.hasNext()) {
						Node curNode = ni.nextNode();
						if (curNode.isNodeType(SLC_IMPORTED_PACKAGE)
								|| curNode.isNodeType(SLC_EXPORTED_PACKAGE)) {
							elements.add(curNode);
						}
					}
					return elements.toArray();
				}
			} catch (RepositoryException e) {
				throw new SlcException(
						"Unexpected error getting multiple values property.", e);
			}
			return result;
		}

		public boolean hasChildren(Object parent) {
			try {
				if (parent instanceof Property
						&& ((Property) parent).isMultiple()) {
					return true;
				} else if (parent instanceof Node && ((Node) parent).hasNodes()
						&& ((Node) parent).isNodeType(SLC_BUNDLE_ARTIFACT)) {
					return true;
				} else if (parent instanceof ViewSpecificItems
						&& ((ViewSpecificItems) parent).isFolder()) {
					return true;
				}
			} catch (RepositoryException e) {
				throw new SlcException(
						"Unexpected exception while checking if property is multiple",
						e);
			}
			return false;
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public void dispose() {
		}
	}
}