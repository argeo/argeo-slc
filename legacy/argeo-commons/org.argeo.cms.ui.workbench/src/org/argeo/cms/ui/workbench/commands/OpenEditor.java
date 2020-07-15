package org.argeo.cms.ui.workbench.commands;

import javax.jcr.Node;

import org.argeo.cms.ui.workbench.WorkbenchUiPlugin;
import org.argeo.cms.ui.workbench.internal.jcr.parts.JcrQueryEditorInput;
import org.argeo.cms.ui.workbench.internal.jcr.parts.NodeEditorInput;
import org.argeo.cms.ui.workbench.jcr.DefaultNodeEditor;
import org.argeo.cms.ui.workbench.jcr.GenericJcrQueryEditor;
import org.argeo.eclipse.ui.EclipseUiException;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

/** Open a {@link Node} editor of a specific type given the node path */
public class OpenEditor extends AbstractHandler {
	public final static String ID = WorkbenchUiPlugin.PLUGIN_ID + ".openEditor";

	public final static String PARAM_PATH = "param.jcrNodePath";
	public final static String PARAM_EDITOR_ID = "param.editorId";

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String editorId = event.getParameter(PARAM_EDITOR_ID);
		try {
			IWorkbenchPage activePage = HandlerUtil.getActiveWorkbenchWindow(
					event).getActivePage();
			if (DefaultNodeEditor.ID.equals(editorId)) {
				String path = event.getParameter(PARAM_PATH);
				NodeEditorInput nei = new NodeEditorInput(path);
				activePage.openEditor(nei, DefaultNodeEditor.ID);
			} else if (GenericJcrQueryEditor.ID.equals(editorId)) {
				JcrQueryEditorInput editorInput = new JcrQueryEditorInput(
						GenericJcrQueryEditor.ID, null);
				activePage.openEditor(editorInput, editorId);
			}
		} catch (PartInitException e) {
			throw new EclipseUiException(
					"Cannot open editor of ID " + editorId, e);
		}
		return null;
	}
}
