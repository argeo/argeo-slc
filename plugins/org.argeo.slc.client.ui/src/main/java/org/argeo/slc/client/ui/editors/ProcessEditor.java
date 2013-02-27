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
package org.argeo.slc.client.ui.editors;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.eclipse.ui.ErrorFeedback;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.client.ui.controllers.ProcessController;
import org.argeo.slc.execution.ExecutionModulesManager;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.jcr.SlcJcrUtils;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;

/** Editor for an execution process. */
public class ProcessEditor extends FormEditor implements SlcTypes, SlcNames {
	public final static String ID = ClientUiPlugin.ID + ".processEditor";

	private Session session;
	private Node processNode;
	private ProcessController processController;

	private ProcessBuilderPage builderPage;

	private ExecutionModulesManager modulesManager;

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		ProcessEditorInput pei = (ProcessEditorInput) input;
		String processPath = pei.getProcessPath();
		try {
			if (processPath != null) {
				if (!session.itemExists(processPath))
					throw new SlcException("Process " + processPath
							+ " does not exist");
				processNode = session.getNode(processPath);
			} else {// new
				processNode = newProcessNode(pei);
			}
			setPartName(processNode.getName());
		} catch (RepositoryException e) {
			throw new SlcException("Cannot initialize editor for " + pei, e);
		}

	}

	protected Node newProcessNode(ProcessEditorInput pei)
			throws RepositoryException {
		String uuid = UUID.randomUUID().toString();
		String processPath = SlcJcrUtils.createExecutionProcessPath(session,
				uuid);
		Node processNode = JcrUtils.mkdirs(session, processPath, SLC_PROCESS);
		processNode.setProperty(SLC_UUID, uuid);
		processNode.setProperty(SLC_STATUS, ExecutionProcess.NEW);
		Node processFlow = processNode.addNode(SLC_FLOW);
		processFlow.addMixin(SLC_REALIZED_FLOW);
		return processNode;
	}

	@Override
	public boolean isDirty() {
		if (getProcessStatus().equals(ExecutionProcess.NEW))
			return true;
		return super.isDirty();
	}

	protected String getProcessStatus() {
		try {
			return processNode.getProperty(SLC_STATUS).getString();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot retrieve status for " + processNode,
					e);
		}
	}

	@Override
	public void dispose() {
		JcrUtils.logoutQuietly(session);
	}

	/** Actually runs the process. */
	void process() {
		// the modifications have to be saved before execution
		try {
			processNode.setProperty(SLC_STATUS, ExecutionProcess.SCHEDULED);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot update status of " + processNode, e);
		}

		// save
		doSave(null);

		try {
			// make sure modules are started for all nodes
			for (NodeIterator nit = processNode.getNode(SLC_FLOW).getNodes(); nit
					.hasNext();) {
				Node flowNode = nit.nextNode();
				try {
					String flowDefPath = flowNode.getNode(SLC_ADDRESS)
							.getProperty(Property.JCR_PATH).getString();
					Node executionModuleNode = flowNode.getSession().getNode(
							SlcJcrUtils.modulePath(flowDefPath));
					if (!executionModuleNode.getProperty(SLC_STARTED)
							.getBoolean())
						ClientUiPlugin.startStopExecutionModule(modulesManager,
								executionModuleNode);
				} catch (Exception e) {
					ErrorFeedback.show(
							"Cannot start execution module related to "
									+ flowNode, e);
				}
			}

			// Actually process
			ExecutionProcess process = processController.process(processNode);
			Map<String, String> properties = new HashMap<String, String>();
			properties.put(ExecutionModulesManager.SLC_PROCESS_ID,
					process.getUuid());
			// modulesManager.registerProcessNotifier(this, properties);
		} catch (Exception e) {
			ErrorFeedback.show("Execution of " + processNode + " failed", e);
		}
	}

	void kill() {
		processController.kill(processNode);
	}

	/** Opens a new editor with a copy of this process */
	void relaunch() {
		try {
			Node duplicatedNode = duplicateProcess();
			IWorkbenchPage activePage = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			activePage.openEditor(
					new ProcessEditorInput(duplicatedNode.getPath()),
					ProcessEditor.ID);
			close(false);
		} catch (Exception e1) {
			throw new SlcException("Cannot relaunch " + processNode, e1);
		}
	}

	/** Duplicates the process */
	protected Node duplicateProcess() {
		try {
			Session session = processNode.getSession();
			String uuid = UUID.randomUUID().toString();
			String destPath = SlcJcrUtils.createExecutionProcessPath(session,
					uuid);
			Node newNode = JcrUtils.mkdirs(session, destPath,
					SlcTypes.SLC_PROCESS);

			Node rootRealizedFlowNode = newNode.addNode(SLC_FLOW);
			// copy node
			JcrUtils.copy(processNode.getNode(SLC_FLOW), rootRealizedFlowNode);

			newNode.setProperty(SLC_UUID, uuid);
			newNode.setProperty(SLC_STATUS, ExecutionProcess.INITIALIZED);

			// reset realized flow status
			// we just manage one level for the time being
			NodeIterator nit = rootRealizedFlowNode.getNodes(SLC_FLOW);
			while (nit.hasNext()) {
				nit.nextNode().setProperty(SLC_STATUS,
						ExecutionProcess.INITIALIZED);
			}

			session.save();
			return newNode;
		} catch (RepositoryException e) {
			throw new SlcException("Cannot duplicate process", e);
		}
	}

	@Override
	protected void addPages() {
		try {
			builderPage = new ProcessBuilderPage(this, processNode,
					modulesManager);
			addPage(builderPage);
			firePropertyChange(PROP_DIRTY);
		} catch (PartInitException e) {
			throw new SlcException("Cannot add pages", e);
		}

	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			String status = processNode.getProperty(SLC_STATUS).getString();
			if (status.equals(ExecutionProcess.NEW))
				processNode.setProperty(SLC_STATUS,
						ExecutionProcess.INITIALIZED);
			session.save();
			builderPage.commit(true);
			editorDirtyStateChanged();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot save " + processNode, e);
		} finally {
			JcrUtils.discardQuietly(session);
		}
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	// public void updateStatus(ExecutionProcess process, String oldStatus,
	// String newStatus) {
	// }
	//
	// public void addSteps(ExecutionProcess process, List<ExecutionStep> steps)
	// {
	// }

	/** Expects one session per editor. */
	public void setSession(Session session) {
		this.session = session;
	}

	public void setProcessController(ProcessController processController) {
		this.processController = processController;
	}

	public void setModulesManager(ExecutionModulesManager modulesManager) {
		this.modulesManager = modulesManager;
	}

}
