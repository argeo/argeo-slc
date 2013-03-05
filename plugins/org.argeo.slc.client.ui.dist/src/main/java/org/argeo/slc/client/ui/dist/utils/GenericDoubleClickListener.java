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
package org.argeo.slc.client.ui.dist.utils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Row;

import org.argeo.ArgeoException;
import org.argeo.eclipse.ui.jcr.utils.JcrFileProvider;
import org.argeo.eclipse.ui.specific.FileHandler;
import org.argeo.slc.client.ui.dist.DistConstants;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.editors.GenericArtifactEditor;
import org.argeo.slc.client.ui.dist.editors.GenericArtifactEditorInput;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PartInitException;

/**
 * Centralizes the management of double click on an ArtifactTreeViewer
 */
public class GenericDoubleClickListener implements IDoubleClickListener,
		SlcTypes, SlcNames, DistConstants {

	// private final static Log log = LogFactory
	// .getLog(GenericDoubleClickListener.class);

	//private TreeViewer viewer;

	private JcrFileProvider jfp;
	private FileHandler fileHandler;

	public GenericDoubleClickListener(TreeViewer viewer) {
		// this.viewer = viewer;
		jfp = new JcrFileProvider();
		fileHandler = new FileHandler(jfp);
	}

	public void doubleClick(DoubleClickEvent event) {
		if (event.getSelection() == null || event.getSelection().isEmpty())
			return;
		Object obj = ((IStructuredSelection) event.getSelection())
				.getFirstElement();
		try {
			if (obj instanceof Node) {
				Node node = (Node) obj;
				if (node.isNodeType(SLC_ARTIFACT_VERSION_BASE)) {
					GenericArtifactEditorInput gaei = new GenericArtifactEditorInput(
							node);
					DistPlugin.getDefault().getWorkbench()
							.getActiveWorkbenchWindow().getActivePage()
							.openEditor(gaei, GenericArtifactEditor.ID);
				} else if (node.isNodeType(NodeType.NT_FILE)) {
					String name = node.getName();
					String id = node.getIdentifier();
					jfp.setReferenceNode(node);
					fileHandler.openFile(name, id);
				}

			} else if (obj instanceof Row) {
				Row row = (Row) obj;
				// String uuid;
				// try {
				// uuid = row.getValue(
				// SLC_ARTIFACT_VERSION_BASE + "." + JCR_IDENTIFIER)
				// .getString();
				// } catch (ItemNotFoundException infe) {
				// MessageDialog.openError(DistPlugin.getDefault()
				// .getWorkbench().getActiveWorkbenchWindow()
				// .getShell(), "Invalid request",
				// "The request must return a value for "
				// + SLC_ARTIFACT_VERSION_BASE + "."
				// + JCR_IDENTIFIER
				// + " in order to open the artifact editor");
				// return;
				// }
				// Node node =
				// row.getNode(SLC_ARTIFACT_VERSION_BASE).getSession()
				// .getNodeByIdentifier(uuid);

				Node node = row.getNode(SLC_ARTIFACT_VERSION_BASE);
				if (node == null)
					MessageDialog.openError(DistPlugin.getDefault()
							.getWorkbench().getActiveWorkbenchWindow()
							.getShell(), "Invalid request",
							"The request must return a "
									+ SLC_ARTIFACT_VERSION_BASE + " node "
									+ " in order to open the artifact editor");
				else {
					GenericArtifactEditorInput gaei = new GenericArtifactEditorInput(
							node);
					DistPlugin.getDefault().getWorkbench()
							.getActiveWorkbenchWindow().getActivePage()
							.openEditor(gaei, GenericArtifactEditor.ID);
				}
			}
		} catch (RepositoryException re) {
			throw new ArgeoException(
					"Repository error while getting node info", re);
		} catch (PartInitException pie) {
			throw new ArgeoException(
					"Unexepected exception while opening artifact editor", pie);
		}
	}
}
