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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.BasicNameVersion;
import org.argeo.slc.NameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.deploy.ModulesManager;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/** Deletes one or many results */
public class UpdateModule extends AbstractHandler {
	private final static Log log = LogFactory.getLog(UpdateModule.class);

	private ModulesManager modulesManager;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil
				.getActiveWorkbenchWindow(event).getActivePage().getSelection();
		if (selection != null && selection instanceof IStructuredSelection) {
			UpdateJob job = new UpdateJob(selection);
			job.setUser(true);
			job.schedule();
		}
		return null;
	}

	private class UpdateJob extends Job {
		private final IStructuredSelection selection;

		public UpdateJob(ISelection selection) {
			super("Update modules");
			this.selection = ((IStructuredSelection) selection);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			Iterator<?> it = selection.iterator();
			Object obj = null;
			try {
				Map<String, Node> nodes = new HashMap<String, Node>();
				nodes: while (it.hasNext()) {
					obj = it.next();
					if (obj instanceof Node) {
						Node node = (Node) obj;
						Node executionModuleNode = null;
						while (executionModuleNode == null) {
							if (node.isNodeType(SlcTypes.SLC_EXECUTION_MODULE)) {
								executionModuleNode = node;
							}
							node = node.getParent();
							if (node.getPath().equals("/"))// root
								continue nodes;
						}

						if (!nodes.containsKey(executionModuleNode.getPath()))
							nodes.put(executionModuleNode.getPath(),
									executionModuleNode);
					}
				}

				monitor.beginTask("Update modules", nodes.size());
				for (Node executionModuleNode : nodes.values()) {
					monitor.subTask("Update " + executionModuleNode.getName());
					NameVersion nameVersion = new BasicNameVersion(
							executionModuleNode.getProperty(SlcNames.SLC_NAME)
									.getString(), executionModuleNode
									.getProperty(SlcNames.SLC_VERSION)
									.getString());
					modulesManager.upgrade(nameVersion);
					monitor.worked(1);
					log.info("Module " + nameVersion + " updated");
					if (monitor.isCanceled())
						return Status.CANCEL_STATUS;
				}
				return Status.OK_STATUS;
			} catch (Exception e) {
				throw new SlcException("Cannot update module " + obj, e);
				// return Status.CANCEL_STATUS;
			}
		}

		@Override
		protected void canceling() {
			getThread().interrupt();
			super.canceling();
		}

	}

	public void setModulesManager(ModulesManager modulesManager) {
		this.modulesManager = modulesManager;
	}

}
