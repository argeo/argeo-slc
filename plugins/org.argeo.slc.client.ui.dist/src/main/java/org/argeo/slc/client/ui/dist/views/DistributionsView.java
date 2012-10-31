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
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.ArgeoMonitor;
import org.argeo.eclipse.ui.EclipseArgeoMonitor;
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
import org.argeo.slc.repo.RepoUtils;
import org.argeo.util.security.Keyring;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
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
	private final static Log log = LogFactory.getLog(DistributionsView.class);
	public final static String ID = DistPlugin.ID + ".distributionsView";

	private Repository nodeRepository;
	private RepositoryFactory repositoryFactory;
	private Keyring keyring;

	private TreeViewer viewer;

	@Override
	public void createPartControl(Composite parent) {
		// Define the TableViewer
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.BORDER);

		TreeViewerColumn col = new TreeViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(200);
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

		// Drag'n drop
		Transfer[] tt = new Transfer[] { TextTransfer.getInstance() };
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		viewer.addDragSupport(operations, tt, new ViewDragListener());
		viewer.addDropSupport(operations, tt, new ViewDropListener(viewer));

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
					repoNode.setProperty(ARGEO_URI, "vm:///" + alias);
					repoNode.addMixin(NodeType.MIX_TITLE);
					repoNode.setProperty(Property.JCR_TITLE, "Internal "
							+ alias + " repository");
					nodeSession.save();
				}
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot register repository", e);
		} finally {
			JcrUtils.logoutQuietly(nodeSession);
		}

		viewer.setInput(nodeRepository);

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

	@Override
	public void setFocus() {
		viewer.getTree().setFocus();
	}

	/*
	 * DEPENDENCY INJECTION
	 */
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

	/*
	 * INTERNAL CLASSES
	 */
	/** Content provider */
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

	/** A software repository */
	private class RepoElem {
		private Node repoNode;

		private Repository repository;
		private Credentials credentials;

		public RepoElem(Node repoNode) {
			this.repoNode = repoNode;
		}

		/** Lazily connects to repository */
		protected void connect() {
			if (repository != null)
				return;
			repository = RepoUtils.getRepository(repositoryFactory, keyring,
					repoNode);
			credentials = RepoUtils.getRepositoryCredentials(keyring, repoNode);
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
			Session session = null;
			try {
				session = repository.login(credentials);
				String[] workspaceNames = session.getWorkspace()
						.getAccessibleWorkspaceNames();
				List<DistributionElem> distributionElems = new ArrayList<DistributionElem>();
				for (String workspaceName : workspaceNames) {
					Node workspaceNode = repoNode.hasNode(workspaceName) ? repoNode
							.getNode(workspaceName) : repoNode
							.addNode(workspaceName);
					repoNode.getSession().save();
					distributionElems.add(new DistributionElem(this,
							workspaceNode));
					// FIXME remove deleted workspaces
				}
				return distributionElems.toArray();
			} catch (RepositoryException e) {
				throw new SlcException(
						"Cannot list workspaces for " + repoNode, e);
			} finally {
				JcrUtils.logoutQuietly(session);
			}
		}

		public void dispose() {
		}

		public Node getRepoNode() {
			return repoNode;
		}

		public String getRepoPath() {
			try {
				return repoNode.getPath();
			} catch (RepositoryException e) {
				throw new SlcException("Cannot get path for " + repoNode, e);
			}
		}

		public Repository getRepository() {
			connect();
			return repository;
		}

		public Credentials getCredentials() {
			return credentials;
		}

	}

	/** Abstracts a distribution, that is a workspace */
	private static class DistributionElem extends TreeParent {
		private final RepoElem repoElem;
		private final Node workspaceNode;

		public DistributionElem(RepoElem repoElem, Node workspaceNode) {
			super(JcrUtils.getNameQuietly(workspaceNode));
			this.repoElem = repoElem;
			this.workspaceNode = workspaceNode;
		}

		public Node getWorkspaceNode() {
			return workspaceNode;
		}

		public String getWorkspaceName() {
			return JcrUtils.getNameQuietly(workspaceNode);
		}

		public String getWorkspacePath() {
			try {
				return workspaceNode.getPath();
			} catch (RepositoryException e) {
				throw new SlcException("Cannot get or add workspace path "
						+ getWorkspaceName(), e);
			}
		}

		public String getRepoPath() {
			try {
				return workspaceNode.getParent().getPath();
			} catch (RepositoryException e) {
				throw new SlcException("Cannot get or add workspace path "
						+ getWorkspaceName(), e);
			}
		}

		public RepoElem getRepoElem() {
			return repoElem;
		}

		public Credentials getCredentials() {
			return repoElem.getCredentials();
		}
	}

	/** Listens to drag */
	class ViewDragListener extends DragSourceAdapter {
		public void dragSetData(DragSourceEvent event) {
			IStructuredSelection selection = (IStructuredSelection) viewer
					.getSelection();
			if (selection.getFirstElement() instanceof DistributionElem) {
				DistributionElem de = (DistributionElem) selection
						.getFirstElement();
				if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
					event.data = de.getWorkspacePath();
				}
			}
		}
	}

	/** Listens to drop */
	class ViewDropListener extends ViewerDropAdapter {

		public ViewDropListener(Viewer viewer) {
			super(viewer);
		}

		@Override
		public boolean performDrop(Object data) {
			DistributionElem sourceDist = (DistributionElem) getSelectedObject();
			RepoElem targetRepo = (RepoElem) getCurrentTarget();

			Boolean ok = MessageDialog.openConfirm(getSite().getShell(),
					"Confirm distribution merge", "Do you want to merge "
							+ sourceDist.getWorkspaceName() + " (from repo "
							+ sourceDist.getRepoElem().getLabel()
							+ ") to repo " + targetRepo.getLabel() + "?");
			if (!ok)
				return false;

			try {
				String sourceWorkspace = sourceDist.getWorkspaceName();
				Repository sourceRepository = RepoUtils.getRepository(
						repositoryFactory, keyring, sourceDist
								.getWorkspaceNode().getParent());
				Credentials sourceCredentials = RepoUtils
						.getRepositoryCredentials(keyring, sourceDist
								.getWorkspaceNode().getParent());

				String targetWorkspace = sourceWorkspace;
				Repository targetRepository = RepoUtils.getRepository(
						repositoryFactory, keyring, targetRepo.getRepoNode());
				Credentials targetCredentials = RepoUtils
						.getRepositoryCredentials(keyring,
								targetRepo.getRepoNode());

				// Open sessions here since the background thread
				// won't necessarily be authenticated.
				// Job should close the sessions.
				Session sourceSession = sourceRepository.login(
						sourceCredentials, sourceWorkspace);
				Session targetSession;
				try {
					targetSession = targetRepository.login(targetCredentials,
							targetWorkspace);
				} catch (NoSuchWorkspaceException e) {
					Session defaultSession = targetRepository
							.login(targetCredentials);
					try {
						defaultSession.getWorkspace().createWorkspace(
								targetWorkspace);
					} catch (Exception e1) {
						throw new SlcException("Cannot create new workspace "
								+ targetWorkspace, e);
					} finally {
						JcrUtils.logoutQuietly(defaultSession);
					}
					targetSession = targetRepository.login(targetCredentials,
							targetWorkspace);
				}

				Job workspaceMergeJob = new WorkspaceMergeJob(sourceSession,
						targetSession);
				workspaceMergeJob.setUser(true);
				workspaceMergeJob.schedule();
				return true;
			} catch (RepositoryException e) {
				throw new SlcException("Cannot process drop from " + sourceDist
						+ " to " + targetRepo, e);
			}
		}

		@Override
		public boolean validateDrop(Object target, int operation,
				TransferData transferType) {
			if (target instanceof RepoElem) {
				if (getSelectedObject() instanceof DistributionElem) {
					// check if not same repository
					String srcRepoPath = ((DistributionElem) getSelectedObject())
							.getRepoPath();
					String targetRepoPath = ((RepoElem) target).getRepoPath();
					return !targetRepoPath.equals(srcRepoPath);
				}
			}
			return false;
		}
	}

	private static class WorkspaceMergeJob extends Job {
		private Session sourceSession;
		private Session targetSession;

		public WorkspaceMergeJob(Session sourceSession, Session targetSession) {
			super("Workspace merge");
			this.sourceSession = sourceSession;
			this.targetSession = targetSession;
		}

		@Override
		protected IStatus run(IProgressMonitor eclipseMonitor) {
			long begin = System.currentTimeMillis();
			try {
				// Not implemented in Davex Jackrabbit v2.2
				// Query countQuery = sourceSession.getWorkspace()
				// .getQueryManager()
				// .createQuery("//element(*, nt:file)", Query.XPATH);
				// QueryResult result = countQuery.execute();
				// Long expectedCount = result.getNodes().getSize();

				Long expectedCount = JcrUtils.countFiles(sourceSession
						.getRootNode());
				if (log.isDebugEnabled())
					log.debug("Will copy " + expectedCount + " files...");

				ArgeoMonitor monitor = new EclipseArgeoMonitor(eclipseMonitor);
				eclipseMonitor
						.beginTask("Copy files", expectedCount.intValue());

				Long count = JcrUtils.copyFiles(sourceSession.getRootNode(),
						targetSession.getRootNode(), true, monitor);

				monitor.done();
				long duration = (System.currentTimeMillis() - begin) / 1000;// in
																			// s
				if (log.isDebugEnabled())
					log.debug("Copied " + count + " files in "
							+ (duration / 60) + "min " + (duration % 60) + "s");

				return Status.OK_STATUS;
			} catch (RepositoryException e) {
				return new Status(IStatus.ERROR, DistPlugin.ID, "Cannot merge",
						e);
			} finally {
				JcrUtils.logoutQuietly(sourceSession);
				JcrUtils.logoutQuietly(targetSession);
			}
		}
	}

	/** Listen to double-clicks */
	private class DistributionsDCL implements IDoubleClickListener {

		public void doubleClick(DoubleClickEvent event) {
			if (event.getSelection() == null || event.getSelection().isEmpty())
				return;
			Object obj = ((IStructuredSelection) event.getSelection())
					.getFirstElement();
			if (obj instanceof DistributionElem) {
				DistributionElem distributionElem = (DistributionElem) obj;
				DistributionEditorInput dei = new DistributionEditorInput(
						distributionElem.getName(), distributionElem
								.getRepoElem().getRepository(),
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

}