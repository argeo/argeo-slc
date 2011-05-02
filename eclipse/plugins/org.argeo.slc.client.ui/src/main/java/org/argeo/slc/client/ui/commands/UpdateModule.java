package org.argeo.slc.client.ui.commands;

import java.util.Iterator;

import javax.jcr.Node;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.eclipse.ui.Error;
import org.argeo.slc.BasicNameVersion;
import org.argeo.slc.NameVersion;
import org.argeo.slc.deploy.ModulesManager;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/** Deletes one or many results */
public class UpdateModule extends AbstractHandler {
	private final static Log log = LogFactory.getLog(UpdateModule.class);

	private ModulesManager modulesManager;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event)
				.getActivePage().getSelection();
		if (selection != null && selection instanceof IStructuredSelection) {
			Iterator<?> it = ((IStructuredSelection) selection).iterator();
			Object obj = null;
			try {
				while (it.hasNext()) {
					obj = it.next();
					if (obj instanceof Node) {
						Node node = (Node) obj;
						if (node.isNodeType(SlcTypes.SLC_EXECUTION_MODULE)) {
							NameVersion nameVersion = new BasicNameVersion(
									node.getProperty(SlcNames.SLC_NAME)
											.getString(), node.getProperty(
											SlcNames.SLC_VERSION).getString());
							modulesManager.upgrade(nameVersion);
							log.info("Module " + nameVersion + " updated");
						}
					}
				}
			} catch (Exception e) {
				Error.show("Cannot update " + obj, e);
			}
		}
		return null;
	}

	public void setModulesManager(ModulesManager modulesManager) {
		this.modulesManager = modulesManager;
	}

}
