package org.argeo.slc.client.ui.dist.controllers;

import org.argeo.eclipse.ui.ErrorFeedback;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.editors.DistributionEditor;
import org.argeo.slc.client.ui.dist.editors.DistributionEditorInput;
import org.argeo.slc.client.ui.dist.model.WorkspaceElem;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PartInitException;

/** Listen to double-clicks */
public class DistTreeDoubleClickListener implements IDoubleClickListener {

	public void doubleClick(DoubleClickEvent event) {
		if (event.getSelection() == null || event.getSelection().isEmpty())
			return;
		Object obj = ((IStructuredSelection) event.getSelection())
				.getFirstElement();
		if (obj instanceof WorkspaceElem) {
			WorkspaceElem we = (WorkspaceElem) obj;
			DistributionEditorInput dei = new DistributionEditorInput(we
					.getRepoElem().getRepository(), we.getRepoElem()
					.getCredentials(), we.getRepoElem().getLabel(), we
					.getRepoElem().getDescription(), we.getWorkspaceName());
			try {
				DistPlugin.getDefault().getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.openEditor(dei, DistributionEditor.ID);
			} catch (PartInitException e) {
				ErrorFeedback.show(
						"Cannot open editor for " + we.getWorkspaceName(), e);
			}
		}
	}
}