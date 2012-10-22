/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
package org.argeo.slc.client.ui.dist.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.nodetype.NodeType;

import org.argeo.eclipse.ui.ErrorFeedback;
import org.argeo.eclipse.ui.TreeParent;
import org.argeo.jcr.ArgeoJcrUtils;
import org.argeo.jcr.ArgeoNames;
import org.argeo.jcr.ArgeoTypes;
import org.argeo.jcr.JcrUtils;
import org.argeo.jcr.UserJcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.commands.CopyWorkspace;
import org.argeo.slc.client.ui.dist.commands.CreateWorkspace;
import org.argeo.slc.client.ui.dist.commands.DeleteWorkspace;
import org.argeo.slc.client.ui.dist.commands.ManageWorkspaceAuth;
import org.argeo.slc.client.ui.dist.commands.NormalizeDistribution;
import org.argeo.slc.client.ui.dist.editors.DistributionEditor;
import org.argeo.slc.client.ui.dist.editors.DistributionEditorInput;
import org.argeo.slc.client.ui.dist.utils.CommandHelpers;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.repo.RepoConstants;
import org.argeo.util.security.Keyring;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * Browse and manipulate distributions (like merge, rename, etc.). Only support
 * one single repository currently.
 */
public class DistributionsView extends ViewPart implements SlcNames, ArgeoNames {
	// private final static Log log =
	// LogFactory.getLog(DistributionsView.class);
	public final static String ID = DistPlugin.ID + ".distributionsView";

	private Repository nodeRepository;
	private RepositoryFactory repositoryFactory;
	private Keyring keyring;

	private TreeViewer viewer;

	// private List<RepositoryElem> repositories = new
	// ArrayList<DistributionsView.RepositoryElem>();

	// private Session nodeSession = null;

	@Override
	public void createPartControl(Composite parent) {
		// Define the TableViewer
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.BORDER);

		TreeViewerColumn col = new TreeViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(200);
		// col.getColumn().setText("Workspace");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof RepoElem)
					return ((RepoElem) element).getLabel();
				return element.toString();
			}
		});

		final Tree table = viewer.getTree();
		table.setHeaderVisible(false);
		table.setLinesVisible(false);

		viewer.setContentProvider(new DistributionsContentProvider());
		viewer.addDoubleClickListener(new DistributionsDCL());

		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(viewer.getTree());
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				contextMenuAboutToShow(manager);
			}
		});
		viewer.getTree().setMenu(menu);
		getSite().registerContextMenu(menuManager, viewer);

		Session nodeSession = null;
		try {
			nodeSession = nodeRepository.login();

			// make sure base directory is available
			Node repos = JcrUtils.mkdirs(nodeSession,
					UserJcrUtils.getUserHome(nodeSession).getPath()
							+ RepoConstants.REPOSITORIES_BASE_PATH);
			nodeSession.save();

			// register default local java repository
			String alias = RepoConstants.DEFAULT_JAVA_REPOSITORY_ALIAS;
			Repository javaRepository = ArgeoJcrUtils.getRepositoryByAlias(
					repositoryFactory, alias);
			if (javaRepository != null) {
				if (!repos.hasNode(alias)) {
					Node repoNode = repos.addNode(alias,
							ArgeoTypes.ARGEO_REMOTE_REPOSITORY);
					repoNode.setProperty(ARGEO_URI, alias);
					repoNode.addMixin(NodeType.MIX_TITLE);
					repoNode.setProperty(Property.JCR_TITLE, "vm://" + alias);
					nodeSession.save();
				}
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot register repository", e);
		} finally {
			JcrUtils.logoutQuietly(nodeSession);
		}
		//
		// try {
		// nodeSession = nodeRepository.login();
		// NodeIterator repos = JcrUtils.mkdirs(
		// nodeSession,
		// UserJcrUtils.getUserHome(nodeSession).getPath()
		// + RepoConstants.REPOSITORIES_BASE_PATH).getNodes();
		// while (repos.hasNext()) {
		// Node repository = repos.nextNode();
		// String label = null;
		// if (repository.isNodeType(NodeType.MIX_TITLE)) {
		// label = repository.getProperty(Property.JCR_TITLE)
		// .getString();
		// }
		//
		// if (repository.isNodeType(ArgeoTypes.ARGEO_REMOTE_REPOSITORY)) {
		// String uri = repository.getProperty(ARGEO_URI).getString();
		// Credentials credentials = null;
		// if (repository.hasProperty(ARGEO_USER_ID)) {
		// String userId = repository.getProperty(ARGEO_USER_ID)
		// .getString();
		// credentials = new SimpleCredentials(userId,
		// "".toCharArray());
		// }
		// Repository remoteRepository = ArgeoJcrUtils
		// .getRepositoryByUri(repositoryFactory, uri);
		// if (label == null)
		// label = repository.getName();
		// repositories.add(new RepositoryElem(label,
		// remoteRepository, credentials));
		// }
		// }
		// } catch (RepositoryException e) {
		// throw new ArgeoException("Cannot read registered repositories", e);
		// }

		// Remote
		// String uri = null;
		// Credentials credentials = null;
		// Repository remoteRepository = null;

		// try {
		// uri = "http://dev.argeo.org/org.argeo.jcr.webapp/pub/java";
		// credentials = new GuestCredentials();
		// remoteRepository =
		// ArgeoJcrUtils.getRepositoryByUri(repositoryFactory, uri);
		// repositories.add(new RepositoryElem("anonymous@dev.argeo.org//java",
		// remoteRepository, credentials));
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		// uri = "http://localhost:7070/org.argeo.jcr.webapp/pub/java";
		// credentials = new GuestCredentials();
		// remoteRepository =
		// ArgeoJcrUtils.getRepositoryByUri(repositoryFactory, uri);
		// repositories.add(new RepositoryElem("anonymous@localhost//java",
		// remoteRepository, credentials));

		// uri = "http://localhost:7070/org.argeo.jcr.webapp/remoting/java";
		// credentials = new SimpleCredentials(System.getProperty("user.name"),
		// "".toCharArray());
		// remoteRepository =
		// ArgeoJcrUtils.getRepositoryByUri(repositoryFactory, uri);
		// repositories.add(new RepositoryElem("@localhost//java",
		// remoteRepository, credentials));

		viewer.setInput(nodeRepository);

	}

	@Override
	public void setFocus() {
		viewer.getTree().setFocus();
	}

	/**
	 * Force refresh of the whole view
	 */
	public void refresh() {
		viewer.setContentProvider(new DistributionsContentProvider());
	}

	public void setNodeRepository(Repository repository) {
		this.nodeRepository = repository;
	}

	public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
		this.repositoryFactory = repositoryFactory;
	}

	public void setKeyring(Keyring keyring) {
		this.keyring = keyring;
	}

	/** Programatically configure the context menu */
	protected void contextMenuAboutToShow(IMenuManager menuManager) {
		IWorkbenchWindow window = DistPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow();
		// Get Current selected item :
		Object firstElement = ((IStructuredSelection) viewer.getSelection())
				.getFirstElement();

		if (firstElement instanceof TreeParent) {
			TreeParent tp = (TreeParent) firstElement;
			String wsName = tp.getName();

			// Build conditions depending on element type (repo or distribution
			// for the time being)
			boolean isDistribElem = false; // , isRepoElem = false;

			// if (tp instanceof RepositoryElem){
			// isRepoElem = true;
			// } else
			if (tp instanceof DistributionElem) {
				isDistribElem = true;
			}

			// create workspace
			CommandHelpers.refreshCommand(menuManager, window,
					CreateWorkspace.ID, CreateWorkspace.DEFAULT_LABEL,
					CreateWorkspace.DEFAULT_ICON_PATH, !isDistribElem);

			// Normalize workspace
			Map<String, String> params = new HashMap<String, String>();
			params.put(NormalizeDistribution.PARAM_WORKSPACE, wsName);
			CommandHelpers.refreshParameterizedCommand(menuManager, window,
					NormalizeDistribution.ID,
					NormalizeDistribution.DEFAULT_LABEL,
					NormalizeDistribution.DEFAULT_ICON_PATH, isDistribElem,
					params);

			// Copy workspace
			params = new HashMap<String, String>();
			params.put(CopyWorkspace.PARAM_WORKSPACE_NAME, wsName);
			CommandHelpers.refreshParameterizedCommand(menuManager, window,
					CopyWorkspace.ID, CopyWorkspace.DEFAULT_LABEL,
					CopyWorkspace.DEFAULT_ICON_PATH, isDistribElem, params);

			// Delete Workspace
			params = new HashMap<String, String>();
			params.put(DeleteWorkspace.PARAM_WORKSPACE_NAME, wsName);
			CommandHelpers.refreshParameterizedCommand(menuManager, window,
					DeleteWorkspace.ID, DeleteWorkspace.DEFAULT_LABEL,
					DeleteWorkspace.DEFAULT_ICON_PATH, isDistribElem, params);

			// Manage workspace authorizations
			params = new HashMap<String, String>();
			params.put(ManageWorkspaceAuth.PARAM_WORKSPACE_NAME, wsName);
			CommandHelpers.refreshParameterizedCommand(menuManager, window,
					ManageWorkspaceAuth.ID, ManageWorkspaceAuth.DEFAULT_LABEL,
					ManageWorkspaceAuth.DEFAULT_ICON_PATH, isDistribElem,
					params);
		}
	}

	private class DistributionsContentProvider implements ITreeContentProvider {
		Session nodeSession;
		List<RepoElem> repositories = new ArrayList<RepoElem>();

		public Object[] getElements(Object input) {
			Repository nodeRepository = (Repository) input;
			try {
				if (nodeSession != null)
					dispose();
				nodeSession = nodeRepository.login();

				String reposPath = UserJcrUtils.getUserHome(nodeSession)
						.getPath() + RepoConstants.REPOSITORIES_BASE_PATH;
				NodeIterator repos = nodeSession.getNode(reposPath).getNodes();
				while (repos.hasNext()) {
					Node repoNode = repos.nextNode();
					if (repoNode.isNodeType(ArgeoTypes.ARGEO_REMOTE_REPOSITORY)) {
						repositories.add(new RepoElem(repoNode));
					}
				}
			} catch (RepositoryException e) {
				throw new SlcException("Cannot get base elements", e);
			}
			return repositories.toArray();
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof RepoElem) {
				return ((RepoElem) parentElement).getChildren();
			} else if (parentElement instanceof DistributionElem) {
				return ((DistributionElem) parentElement).getChildren();
			}
			return null;
		}

		public Object getParent(Object element) {
			// TODO register repo elem in distirbution elem?
			return null;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof RepoElem) {
				return true;
			} else if (element instanceof DistributionElem) {
				return false;
			}
			return false;
		}

		public void dispose() {
			for (RepoElem repoElem : repositories)
				repoElem.dispose();
			repositories = new ArrayList<RepoElem>();
			JcrUtils.logoutQuietly(nodeSession);
		}

	}

	// private class DistributionsContentProvider extends
	// AbstractTreeContentProvider {
	//
	// public Object[] getElements(Object arg0) {
	// return repositories.toArray();
	// }
	//
	// }

	private class RepoElem {
		private Node repoNode;

		private Repository repository;
		private Credentials credentials;
		private Session defaultSession = null;

		public RepoElem(Node repoNode) {
			this.repoNode = repoNode;
		}

		/** Lazily connects to repository */
		protected void connect() {
			if (defaultSession != null)
				return;

			try {
				if (repoNode.isNodeType(ArgeoTypes.ARGEO_REMOTE_REPOSITORY)) {
					String uri = repoNode.getProperty(ARGEO_URI).getString();
					if (uri.startsWith("http")) {// http, https
						if (repoNode.hasProperty(ARGEO_USER_ID)) {
							String userId = repoNode.getProperty(ARGEO_USER_ID)
									.getString();
							char[] password = keyring.getAsChars(repoNode
									.getPath() + '/' + ARGEO_PASSWORD);
							credentials = new SimpleCredentials(userId,
									password);
						}
						repository = ArgeoJcrUtils.getRepositoryByUri(
								repositoryFactory, uri);
					} else {// alias
						String alias = uri;
						repository = ArgeoJcrUtils.getRepositoryByAlias(
								repositoryFactory, alias);
						credentials = null;
					}
					defaultSession = repository.login(credentials);
				}
			} catch (RepositoryException e) {
				throw new SlcException("Cannot connect to repository "
						+ repoNode, e);
			}
		}

		public String getLabel() {
			try {
				if (repoNode.isNodeType(NodeType.MIX_TITLE)) {
					return repoNode.getProperty(Property.JCR_TITLE).getString();
				} else {
					return repoNode.getName();
				}
			} catch (RepositoryException e) {
				throw new SlcException("Cannot read label of " + repoNode, e);
			}
		}

		public String toString() {
			return repoNode.toString();
		}

		public Object[] getChildren() {
			connect();
			try {
				String[] workspaceNames = defaultSession.getWorkspace()
						.getAccessibleWorkspaceNames();
				List<DistributionElem> distributionElems = new ArrayList<DistributionElem>();
				for (String workspace : workspaceNames)
					distributionElems.add(new DistributionElem(repository,
							workspace, credentials));
				return distributionElems.toArray();
			} catch (RepositoryException e) {
				throw new SlcException(
						"Cannot list workspaces for " + repoNode, e);
			}
		}

		public void dispose() {
			JcrUtils.logoutQuietly(defaultSession);
		}
	}

	/** Wraps a repository **/
	// private static class RepositoryElem extends TreeParent {
	// // private final Repository repository;
	// private Session defaultSession;
	//
	// public RepositoryElem(String name, Repository repository,
	// Credentials credentials) {
	// super(name);
	// try {
	// defaultSession = repository.login(credentials);
	// String[] workspaceNames = defaultSession.getWorkspace()
	// .getAccessibleWorkspaceNames();
	// for (String workspace : workspaceNames)
	// addChild(new DistributionElem(repository, workspace,
	// credentials));
	// } catch (RepositoryException e) {
	// ErrorFeedback.show("Cannot log to repository", e);
	// }
	// }
	//
	// @Override
	// public synchronized void dispose() {
	// if (log.isTraceEnabled())
	// log.trace("Disposing RepositoryElement");
	// JcrUtils.logoutQuietly(defaultSession);
	// super.dispose();
	// }
	// }

	private static class DistributionElem extends TreeParent {
		private final String workspaceName;
		private final Repository repository;
		private final Credentials credentials;

		public DistributionElem(Repository repository, String workspaceName,
				Credentials credentials) {
			super(workspaceName);
			this.workspaceName = workspaceName;
			this.repository = repository;
			this.credentials = credentials;
		}

		public String getWorkspaceName() {
			return workspaceName;
		}

		public Repository getRepository() {
			return repository;
		}

		public Credentials getCredentials() {
			return credentials;
		}
	}

	@Override
	public void dispose() {
		// for (RepositoryElem re : repositories)
		// re.dispose();
		super.dispose();
	}

	private class DistributionsDCL implements IDoubleClickListener {

		public void doubleClick(DoubleClickEvent event) {
			if (event.getSelection() == null || event.getSelection().isEmpty())
				return;
			Object obj = ((IStructuredSelection) event.getSelection())
					.getFirstElement();
			if (obj instanceof DistributionElem) {
				DistributionElem distributionElem = (DistributionElem) obj;
				DistributionEditorInput dei = new DistributionEditorInput(
						distributionElem.getName(),
						distributionElem.getRepository(),
						distributionElem.getWorkspaceName(),
						distributionElem.getCredentials());
				try {
					DistPlugin.getDefault().getWorkbench()
							.getActiveWorkbenchWindow().getActivePage()
							.openEditor(dei, DistributionEditor.ID);
				} catch (PartInitException e) {
					ErrorFeedback.show("Cannot open editor for "
							+ distributionElem.getWorkspaceName(), e);
				}
			}
		}
	}
}