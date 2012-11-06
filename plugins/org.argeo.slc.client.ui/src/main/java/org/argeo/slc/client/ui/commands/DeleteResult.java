/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.eclipse.ui.ErrorFeedback;
import org.argeo.slc.client.ui.model.ResultFolder;
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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/** Deletes one or many results */
public class DeleteResult extends AbstractHandler {
	/* DEPENDENCY INJECTION */
	private Session session;

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil
				.getActiveWorkbenchWindow(event).getActivePage().getSelection();

		if (!MessageDialog.openConfirm(HandlerUtil.getActiveShell(event),
				"Confirm",
				"Are you sure that you want to delete these results?"))
			return null;

		Job job = new Job("Delete results") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				if (selection != null
						&& selection instanceof IStructuredSelection) {
					List<String> nodes = new ArrayList<String>();
					Iterator<?> it = ((IStructuredSelection) selection)
							.iterator();
					Object obj = null;
					try {
						while (it.hasNext()) {
							obj = it.next();
							if (obj instanceof ResultFolder) {
								Node node = ((ResultFolder) obj).getNode();
								nodes.add(node.getPath());
							} else if (obj instanceof SingleResultNode) {
								Node node = ((SingleResultNode) obj).getNode();
								nodes.add(node.getPath());
							}
						}
					} catch (RepositoryException e) {
						ErrorFeedback.show("Cannot list nodes", e);
						return null;
					}
					monitor.beginTask("Delete results", nodes.size());
					Node node = null;
					try {
						for (final String path : nodes) {
							if (session.itemExists(path)) {
								node = session.getNode(path);
								Node parent = node.getParent();
								node.remove();
								ResultParentUtils.updateStatusOnRemoval(parent);
							}
							monitor.worked(1);
						}
						session.save();
					} catch (RepositoryException e) {
						ErrorFeedback.show("Cannot delete node " + node, e);
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

	/* DEPENDENCY INJECTION */
	public void setSession(Session session) {
		this.session = session;
	}
}
