package org.argeo.slc.client.ui.commands;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Node;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.eclipse.ui.Error;
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

			Job job = new Job("Update modules") {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					Iterator<?> it = ((IStructuredSelection) selection)
							.iterator();
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

								if (!nodes.containsKey(executionModuleNode
										.getPath()))
									nodes.put(executionModuleNode.getPath(),
											executionModuleNode);
							}
						}

						monitor.beginTask("Update modules", nodes.size());
						for (Node executionModuleNode : nodes.values()) {
							monitor.subTask("Update "
									+ executionModuleNode.getName());
							NameVersion nameVersion = new BasicNameVersion(
									executionModuleNode.getProperty(
											SlcNames.SLC_NAME).getString(),
									executionModuleNode.getProperty(
											SlcNames.SLC_VERSION).getString());
							modulesManager.upgrade(nameVersion);
							monitor.worked(1);
							log.info("Module " + nameVersion + " updated");
						}
						return Status.OK_STATUS;
					} catch (Exception e) {
						throw new SlcException("Cannot update module " + obj, e);
						// return Status.CANCEL_STATUS;
					}
				}
			};
			job.setUser(true);
			job.schedule();
		}
		return null;
	}

	public void setModulesManager(ModulesManager modulesManager) {
		this.modulesManager = modulesManager;
	}

}
