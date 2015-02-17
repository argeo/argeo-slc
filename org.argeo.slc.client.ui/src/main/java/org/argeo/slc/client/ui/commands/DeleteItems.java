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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.client.ui.model.ResultFolder;
import org.argeo.slc.client.ui.model.ResultParent;
import org.argeo.slc.client.ui.model.ResultParentUtils;
import org.argeo.slc.client.ui.model.SingleResultNode;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/** Deletes one or many results */
public class DeleteItems extends AbstractHandler {
	public final static String ID = ClientUiPlugin.ID + ".deleteItems";
	public final static ImageDescriptor DEFAULT_IMG_DESCRIPTOR = ClientUiPlugin
			.getImageDescriptor("icons/removeAll.png");
	public final static String DEFAULT_LABEL = "Delete selected item(s)";

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil
				.getActiveWorkbenchWindow(event).getActivePage().getSelection();

		// confirmation
		StringBuilder buf = new StringBuilder("");
		Iterator<?> lst = ((IStructuredSelection) selection).iterator();
		while (lst.hasNext()) {
			Object obj = lst.next();
			if (obj instanceof ResultParent) {
				ResultParent rp = ((ResultParent) obj);
				buf.append(rp.getName()).append(", ");
			}
		}

		String msg = "Nothing to delete";
		// remove last separator
		if (buf.lastIndexOf(", ") > -1) {
			msg = "Do you want to delete following objects (and their children): "
					+ buf.substring(0, buf.lastIndexOf(", ")) + "?";
		}
		Boolean ok = MessageDialog.openConfirm(
				HandlerUtil.getActiveShell(event), "Confirm deletion", msg);

		if (!ok)
			return null;

		Job job = new Job("Delete results") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				if (selection != null
						&& selection instanceof IStructuredSelection) {
					Map<String, Node> nodes = new HashMap<String, Node>();
					Iterator<?> it = ((IStructuredSelection) selection)
							.iterator();
					Object obj = null;
					try {

						while (it.hasNext()) {
							obj = it.next();
							if (obj instanceof ResultFolder) {
								Node node = ((ResultFolder) obj).getNode();
								nodes.put(node.getPath(), node);
							} else if (obj instanceof SingleResultNode) {
								Node node = ((SingleResultNode) obj).getNode();
								nodes.put(node.getPath(), node);
							}
						}
						if (!nodes.isEmpty()) {
							Session session = null;
							monitor.beginTask("Delete results", nodes.size());
							for (String path : nodes.keySet()) {
								if (session == null)
									session = nodes.get(path).getSession();

								// check if the item has not already been
								// deleted while deleting one of its ancestor
								if (session.itemExists(path)) {
									Node parent = nodes.get(path).getParent();
									nodes.get(path).remove();
									ResultParentUtils.updatePassedStatus(
											parent, true);
								}
								monitor.worked(1);
							}
							session.save();
						}

					} catch (RepositoryException e) {
						throw new SlcException(
								"Unexpected error while deleteting node(s)", e);
					}
					monitor.done();
				}
				return Status.OK_STATUS;
			}

		};
		job.setUser(true);
		job.schedule();
		return null;
	}
}