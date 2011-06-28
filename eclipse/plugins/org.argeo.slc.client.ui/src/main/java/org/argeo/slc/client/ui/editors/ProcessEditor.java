package org.argeo.slc.client.ui.editors;

import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.eclipse.ui.Error;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.client.ui.controllers.ProcessController;
import org.argeo.slc.execution.ExecutionProcess;
import org.argeo.slc.jcr.SlcJcrUtils;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

public class ProcessEditor extends FormEditor implements SlcTypes, SlcNames {
	public final static String ID = ClientUiPlugin.ID + ".processEditor";

	private Session session;
	private Node processNode;
	private ProcessController processController;

	private ProcessBuilderPage builderPage;
	private ProcessLogPage logPage;

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
		String processPath = SlcJcrUtils.createExecutionProcessPath(uuid);
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
	public void process() {
		// the modifications have to be saved before execution
		try {
			processNode.setProperty(SLC_STATUS, ExecutionProcess.SCHEDULED);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot update status of " + processNode, e);
		}
		doSave(null);
		try {
			processController.process(processNode);
		} catch (Exception e) {
			Error.show("Execution of " + processNode + " failed", e);
		}
	}

	@Override
	protected void addPages() {
		try {
			builderPage = new ProcessBuilderPage(this, processNode);
			addPage(builderPage);
			firePropertyChange(PROP_DIRTY);
			logPage = new ProcessLogPage(this);
			addPage(logPage);
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

	/** Expects one session per editor. */
	public void setSession(Session session) {
		this.session = session;
	}

	public void setProcessController(ProcessController processController) {
		this.processController = processController;
	}

}
