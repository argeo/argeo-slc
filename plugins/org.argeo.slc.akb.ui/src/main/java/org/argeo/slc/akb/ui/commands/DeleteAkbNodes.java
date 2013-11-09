package org.argeo.slc.akb.ui.commands;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.ui.AkbUiPlugin;
import org.argeo.slc.akb.ui.editors.AkbNodeEditorInput;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Deletes one or more akb nodes also closing the corresponding editors if
 * needed
 */
public class DeleteAkbNodes extends AbstractHandler {
	public final static String ID = AkbUiPlugin.PLUGIN_ID + ".deleteAkbNodes";

	/* DEPENDENCY INJECTION */
	private Repository repository;

	public final static String PARAM_NODE_JCR_ID = "param.nodeJcrId";

	// public final static String PARAM_NODE_TYPE = "param.nodeType";

	public Object execute(ExecutionEvent event) throws ExecutionException {

		// String nodeType = event.getParameter(PARAM_NODE_TYPE);
		String nodeJcrId = event.getParameter(PARAM_NODE_JCR_ID);

		Session session = null;
		try {
			// caches current Page
			IWorkbenchPage currentPage = HandlerUtil.getActiveWorkbenchWindow(
					event).getActivePage();

			session = repository.login();
			Node node = null;

			if (nodeJcrId != null)
				node = session.getNodeByIdentifier(nodeJcrId);

			// no node has been found or created, return silently
			if (node == null)
				return null;

			IEditorPart currPart = currentPage
					.findEditor(new AkbNodeEditorInput(nodeJcrId));
			if (currPart != null)
				currPart.dispose();
		} catch (RepositoryException e) {
			throw new AkbException("JCR error while deleting node" + nodeJcrId
					+ " editor", e);
		} finally {
			JcrUtils.logoutQuietly(session);
		}
		return null;
	}

	/* DEPENDENCY INJECTION */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
}