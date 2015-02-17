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
package org.argeo.slc.client.ui.commands;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.argeo.eclipse.ui.ErrorFeedback;
import org.argeo.eclipse.ui.dialogs.SingleValue;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.client.ui.model.ParentNodeFolder;
import org.argeo.slc.client.ui.model.ResultFolder;
import org.argeo.slc.jcr.SlcJcrResultUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Add a new SlcType.SLC_RESULT_FOLDER node to the current user "my result"
 * tree. This handler is only intended to bu used with JcrResultTreeView and its
 * descendants.
 */

public class AddResultFolder extends AbstractHandler {
	public final static String ID = ClientUiPlugin.ID + ".addResultFolder";
	public final static String DEFAULT_ICON_REL_PATH = "icons/addFolder.gif";
	public final static String DEFAULT_LABEL = "Add folder...";

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil
				.getActiveWorkbenchWindow(event).getActivePage().getSelection();

		// Sanity check, already done when populating the corresponding popup
		// menu.
		if (selection != null && selection.size() == 1) {
			Object obj = selection.getFirstElement();
			try {
				Node parentNode = null;
				if (obj instanceof ResultFolder) {
					ResultFolder rf = (ResultFolder) obj;
					parentNode = rf.getNode();
				} else if (obj instanceof ParentNodeFolder) {
					Node node = ((ParentNodeFolder) obj).getNode();
					if (node.getPath().startsWith(
							SlcJcrResultUtils.getMyResultsBasePath(node
									.getSession())))
						parentNode = node;
				}

				if (parentNode != null) {
					String folderName = SingleValue.ask("Folder name",
							"Enter folder name");
					if (folderName != null) {
						if (folderName.contains("/")) {
							ErrorFeedback
									.show("Folder names can't contain a '/'.");
							return null;
						}

						String absPath = parentNode.getPath() + "/"
								+ folderName;
						SlcJcrResultUtils.createResultFolderNode(
								parentNode.getSession(), absPath);
					}
				}
			} catch (RepositoryException e) {
				throw new SlcException(
						"Unexpected exception while creating result folder", e);
			}
		} else {
			ErrorFeedback.show("Can only add file folder to a node");
		}
		return null;
	}
}