package org.argeo.slc.client.ui.dist.editors;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;

import org.argeo.cms.security.Keyring;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.repo.RepoUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

/** Browse, analyse and modify a workspace containing software distributions */
public class DistWorkspaceEditor extends FormEditor implements SlcNames {
	private static final long serialVersionUID = 5373719580281643675L;

	// private final static Log log =
	// LogFactory.getLog(DistributionEditor.class);
	public final static String ID = DistPlugin.PLUGIN_ID + ".distWorkspaceEditor";

	/* DEPENDENCY INJECTION */
	private RepositoryFactory repositoryFactory;
	private Repository localRepository;
	private Keyring keyring;

	// Business objects
	private Node repoNode;
	// Session that provides the node in the home of the local repository
	private Session localSession = null;
	// The business Session on optionally remote repository
	private Session businessSession;
	private DistWkspEditorInput editorInput;

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		editorInput = (DistWkspEditorInput) input;
		try {
			localSession = localRepository.login();
			if (editorInput.getRepoNodePath() != null
					&& localSession.nodeExists(editorInput.getRepoNodePath()))
				repoNode = localSession.getNode(editorInput.getRepoNodePath());

			businessSession = RepoUtils.getRemoteSession(
					repositoryFactory, keyring, repoNode, editorInput.getUri(),
					editorInput.getWorkspaceName());
		} catch (RepositoryException e) {
			throw new PartInitException("Cannot log to workspace "
					+ editorInput.getName(), e);
		}
		setPartName(editorInput.getWorkspaceName());
		super.init(site, input);
	}

	@Override
	protected void addPages() {
		try {
			addPage(new DistWkspSearchPage(this, "Details ", businessSession));
			addPage(new DistWkspBrowserPage(this, "Maven ", businessSession));
			addPage(new WkspCategoryBaseListPage(this, "Groups ",
					businessSession));
		} catch (PartInitException e) {
			throw new SlcException("Cannot add distribution editor pages", e);
		}
	}

	@Override
	public void doSave(IProgressMonitor arg0) {
	}

	@Override
	public void dispose() {
		JcrUtils.logoutQuietly(businessSession);
		JcrUtils.logoutQuietly(localSession);
		super.dispose();
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	protected Node getRepoNode() {
		return repoNode;
	}

	protected Session getSession() {
		return businessSession;
	}

	/* DEPENDENCY INJECTION */
	public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
		this.repositoryFactory = repositoryFactory;
	}

	public void setKeyring(Keyring keyring) {
		this.keyring = keyring;
	}

	public void setLocalRepository(Repository localRepository) {
		this.localRepository = localRepository;
	}
}