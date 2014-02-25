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
package org.argeo.slc.client.ui.dist.commands;

import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.argeo.ArgeoException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.utils.CommandHelpers;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Delete chosen artifacts from the current workspace.
 */

public class DeleteArtifacts extends AbstractHandler {
	// private static final Log log = LogFactory.getLog(DeleteWorkspace.class);
	public final static String ID = DistPlugin.ID + ".deleteArtifacts";
	public final static String DEFAULT_LABEL = "Delete selected items";
	// public final static String DEFAULT_ICON_PATH = "icons/removeItem.gif";

	public final static ImageDescriptor DEFAULT_ICON = DistPlugin
			.getImageDescriptor("icons/removeItem.gif");

	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IWorkbenchPart activePart = DistPlugin.getDefault().getWorkbench()
					.getActiveWorkbenchWindow().getActivePage().getActivePart();

			if (activePart instanceof IEditorPart) {
				ISelection selector = ((IEditorPart) activePart)
						.getEditorSite().getSelectionProvider().getSelection();
				if (selector != null
						&& selector instanceof IStructuredSelection) {
					Iterator<?> it = ((IStructuredSelection) selector)
							.iterator();

					String msg = "Your are about to definitively remove the "
							+ ((IStructuredSelection) selector).size()
							+ " selected artifacts.\n"
							+ "Are you sure you want to proceed ?";

					boolean result = MessageDialog.openConfirm(DistPlugin
							.getDefault().getWorkbench().getDisplay()
							.getActiveShell(), "Confirm Delete", msg);

					if (result) {
						while (it.hasNext()) {
							Node node = (Node) it.next();
							// we remove the artifactVersion, that is the parent
							node.getParent().remove();
							node.getSession().save();
						}
					}
				}
			}
			CommandHelpers.callCommand(RefreshDistributionOverviewPage.ID);
		} catch (RepositoryException re) {
			throw new ArgeoException(
					"Unexpected error while deleting artifacts.", re);
		}
		return null;
	}
}