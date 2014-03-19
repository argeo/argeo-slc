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
import org.argeo.slc.jcr.SlcTypes;
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
 * Add the {@code SlcNames.SLC_RELEVANT_CATEGORY} mixin to the selected node
 */
public class MarkAsRelevantCategory extends AbstractHandler {
	// private static final Log log = LogFactory.getLog(DeleteWorkspace.class);

	public final static String ID = DistPlugin.ID + ".markAsRelevantCategory";
	public final static String DEFAULT_LABEL = "Mark as relevant category base";
	public final static String DEFAULT_REMOVE_LABEL = "Remove this category from relevant list";
	public final static ImageDescriptor DEFAULT_ICON = DistPlugin
			.getImageDescriptor("icons/addItem.gif");
	public final static ImageDescriptor DEFAULT_REMOVE_ICON = DistPlugin
			.getImageDescriptor("icons/removeMark.gif");

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

					Node node = (Node) it.next();
					if (node.isNodeType(SlcTypes.SLC_CATEGORY)) {
						String msg = "Your are about to unlist this category from the relevant category list for current workspace"
								+ ".\n" + "Are you sure you want to proceed?";
						if (MessageDialog.openConfirm(DistPlugin.getDefault()
								.getWorkbench().getDisplay().getActiveShell(),
								"Confirm", msg)) {
							node.removeMixin(SlcTypes.SLC_CATEGORY);
							node.getSession().save();
						}
					} else {
						String msg = "Your are about to mark this group as category base in the current workspace"
								+ ".\n" + "Are you sure you want to proceed?";

						if (MessageDialog.openConfirm(DistPlugin.getDefault()
								.getWorkbench().getDisplay().getActiveShell(),
								"Confirm", msg)) {
							node.addMixin(SlcTypes.SLC_CATEGORY);
							node.getSession().save();
						}
					}
				}
			}
		} catch (RepositoryException re) {
			throw new ArgeoException(
					"Unexpected error while deleting artifacts.", re);
		}
		return null;
	}
}