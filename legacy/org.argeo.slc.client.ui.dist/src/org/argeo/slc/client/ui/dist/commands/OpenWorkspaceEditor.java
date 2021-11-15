package org.argeo.slc.client.ui.dist.commands;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.cms.ArgeoNames;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.editors.DistWkspEditorInput;
import org.argeo.slc.client.ui.dist.editors.DistWorkspaceEditor;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Open a distribution workspace editor for a given workspace in a repository
 */
public class OpenWorkspaceEditor extends AbstractHandler {
	public final static String ID = DistPlugin.PLUGIN_ID
			+ ".openWorkspaceEditor";
	public final static String DEFAULT_LABEL = "Open editor";
	public final static ImageDescriptor DEFAULT_ICON = DistPlugin
			.getImageDescriptor("icons/distribution_perspective.gif");

	// Use local node repo and repository factory to retrieve and log to
	// relevant repository
	public final static String PARAM_REPO_NODE_PATH = "param.repoNodePath";
	// Use URI and repository factory to retrieve and ANONYMOUSLY log in
	// relevant repository
	public final static String PARAM_REPO_URI = "param.repoUri";
	public final static String PARAM_WORKSPACE_NAME = "param.workspaceName";

	/* DEPENDENCY INJECTION */
	private Repository localRepository;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String repoNodePath = event.getParameter(PARAM_REPO_NODE_PATH);
		String repoUri = event.getParameter(PARAM_REPO_URI);
		String workspaceName = event.getParameter(PARAM_WORKSPACE_NAME);

		Session defaultSession = null;
		if (repoNodePath != null && repoUri == null) {
			try {
				defaultSession = localRepository.login();
				if (defaultSession.nodeExists(repoNodePath)) {
					Node repoNode = defaultSession.getNode(repoNodePath);
					repoUri = repoNode.getProperty(ArgeoNames.ARGEO_URI)
							.getString();
				}
			} catch (RepositoryException e) {
				throw new SlcException("Unexpected error while "
						+ "getting repoNode at path " + repoNodePath, e);
			} finally {
				JcrUtils.logoutQuietly(defaultSession);
			}
		}

		DistWkspEditorInput wei = new DistWkspEditorInput(repoNodePath,
				repoUri, workspaceName);
		try {
			HandlerUtil.getActiveWorkbenchWindow(event).getActivePage()
					.openEditor(wei, DistWorkspaceEditor.ID);
		} catch (PartInitException e) {
			throw new SlcException("Unexpected error while "
					+ "opening editor for workspace " + workspaceName
					+ " with URI " + repoUri + " and repoNode at path "
					+ repoNodePath, e);
		}
		return null;
	}

	/* DEPENDENCY INJECTION */
	public void setLocalRepository(Repository localRepository) {
		this.localRepository = localRepository;
	}
}