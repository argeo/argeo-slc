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
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

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
import org.argeo.slc.client.ui.dist.DistImages;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.commands.CopyWorkspace;
import org.argeo.slc.client.ui.dist.commands.CreateWorkspace;
import org.argeo.slc.client.ui.dist.commands.DeleteWorkspace;
import org.argeo.slc.client.ui.dist.commands.DisplayRepoInformation;
import org.argeo.slc.client.ui.dist.commands.Fetch;
import org.argeo.slc.client.ui.dist.commands.NormalizeDistribution;
import org.argeo.slc.client.ui.dist.commands.PublishWorkspace;
import org.argeo.slc.client.ui.dist.commands.RegisterRepository;
import org.argeo.slc.client.ui.dist.commands.UnregisterRemoteRepo;
import org.argeo.slc.client.ui.dist.editors.DistributionEditor;
import org.argeo.slc.client.ui.dist.editors.DistributionEditorInput;
import org.argeo.slc.client.ui.dist.utils.ArtifactNamesComparator;
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
import org.eclipse.swt.graphics.Image;
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
		col.getColumn().setWidth(400);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof BrowserElem)
					return ((BrowserElem) element).getLabel();
				else
					return element.toString();
			}

			@Override
			public Image getImage(Object element) {
				if (element instanceof BrowserElem) {
					BrowserElem bElement = (BrowserElem) element;
					if (bElement instanceof RepoElem) {
						if (bElement.isHomeRepo())
							return DistImages.IMG_HOME_REPO;
						else if (bElement.isReadOnly)
							return DistImages.IMG_REPO_READONLY;
						else
							return DistImages.IMG_REPO;

					} else if (bElement instanceof DistribGroupElem) {
						if (bElement.isReadOnly)
							return DistImages.IMG_DISTGRP_READONLY;
						else
							return DistImages.IMG_DISTGRP;
					}
				} else if (element instanceof DistributionElem)
					return DistImages.IMG_WKSP;
				return null;
			}
		});

		final Tree tree = viewer.getTree();
		tree.setHeaderVisible(false);
		tree.setLinesVisible(false);

		viewer.setContentProvider(new DistributionsContentProvider());
		viewer.addDoubleClickListener(new DistributionsDCL());
		viewer.setComparator(new BrowserElementComparator());

		// Enable selection retrieving from outside the view
		getSite().setSelectionProvider(viewer);

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
		// Most of the implemented commands support only one selected
		// element
		boolean singleElement = ((IStructuredSelection) viewer.getSelection())
				.size() == 1;
		// Get Current selected item :
		Object firstElement = ((IStructuredSelection) viewer.getSelection())
				.getFirstElement();

		if (firstElement instanceof TreeParent
				|| firstElement instanceof BrowserElem) {
			String targetRepoPath = null;

			// Build conditions depending on element type
			boolean isDistribElem = false, isRepoElem = false, isDistribGroupElem = false;
			boolean isHomeRepo = false, isReadOnly = true;

			if (firstElement instanceof DistributionElem) {
				DistributionElem de = (DistributionElem) firstElement;
				isDistribElem = true;
				isReadOnly = de.isReadOnly();
			} else if (firstElement instanceof RepoElem) {
				RepoElem re = (RepoElem) firstElement;
				isRepoElem = true;
				targetRepoPath = re.getRepoPath();
				isHomeRepo = re.isHomeRepo();
				isReadOnly = re.isReadOnly();
			} else if (firstElement instanceof DistribGroupElem) {
				DistribGroupElem dge = (DistribGroupElem) firstElement;
				isReadOnly = dge.isReadOnly();
				isDistribGroupElem = true;
			}

			// Display repo info
			CommandHelpers.refreshCommand(menuManager, window,
					DisplayRepoInformation.ID,
					DisplayRepoInformation.DEFAULT_LABEL,
					DisplayRepoInformation.DEFAULT_ICON_PATH, isRepoElem
							&& singleElement);

			// create workspace
			CommandHelpers.refreshCommand(menuManager, window,
					CreateWorkspace.ID, CreateWorkspace.DEFAULT_LABEL,
					CreateWorkspace.DEFAULT_ICON_PATH,
					(isRepoElem || isDistribGroupElem) && singleElement
							&& !isReadOnly);
			// publish workspace
			CommandHelpers.refreshCommand(menuManager, window,
					PublishWorkspace.ID, PublishWorkspace.DEFAULT_LABEL,
					PublishWorkspace.DEFAULT_ICON_PATH, isDistribElem
							&& singleElement && !isReadOnly);

			// Register a remote repository
			CommandHelpers.refreshCommand(menuManager, window,
					RegisterRepository.ID, RegisterRepository.DEFAULT_LABEL,
					RegisterRepository.DEFAULT_ICON_PATH, isRepoElem
							&& singleElement);

			// Unregister a remote repository
			Map<String, String> params = new HashMap<String, String>();
			params.put(UnregisterRemoteRepo.PARAM_REPO_PATH, targetRepoPath);
			CommandHelpers.refreshParameterizedCommand(menuManager, window,
					UnregisterRemoteRepo.ID,
					UnregisterRemoteRepo.DEFAULT_LABEL,
					UnregisterRemoteRepo.DEFAULT_ICON_PATH, isRepoElem
							&& !isHomeRepo && singleElement, params);

			// Fetch repository
			params = new HashMap<String, String>();
			params.put(Fetch.PARAM_TARGET_REPO, targetRepoPath);
			CommandHelpers.refreshParameterizedCommand(menuManager, window,
					Fetch.ID, Fetch.DEFAULT_LABEL, Fetch.DEFAULT_ICON_PATH,
					isRepoElem && singleElement && !isReadOnly, params);

			// Normalize workspace
			CommandHelpers.refreshCommand(menuManager, window,
					NormalizeDistribution.ID,
					NormalizeDistribution.DEFAULT_LABEL,
					NormalizeDistribution.DEFAULT_ICON_PATH, isDistribElem
							&& singleElement && !isReadOnly);

			// Copy workspace
			CommandHelpers.refreshCommand(menuManager, window,
					CopyWorkspace.ID, CopyWorkspace.DEFAULT_LABEL,
					CopyWorkspace.DEFAULT_ICON_PATH, isDistribElem
							&& singleElement);

			// Clear Workspace
			CommandHelpers.refreshCommand(menuManager, window,
					DeleteWorkspace.ID, DeleteWorkspace.DEFAULT_LABEL,
					DeleteWorkspace.DEFAULT_ICON_PATH, isDistribElem
							&& singleElement && !isReadOnly);

			// // Manage workspace authorizations
			// params = new HashMap<String, String>();
			// params.put(ManageWorkspaceAuth.PARAM_WORKSPACE_NAME, wsName);
			// CommandHelpers.refreshParameterizedCommand(menuManager, window,
			// ManageWorkspaceAuth.ID, ManageWorkspaceAuth.DEFAULT_LABEL,
			// ManageWorkspaceAuth.DEFAULT_ICON_PATH, isDistribElem
			// && singleElement && !isReadOnly, params);
		}
		// } catch (RepositoryException e) {
		// throw new SlcException("unexpected errror while "
		// + "building context menu", e);
		// }
	}

	/**
	 * Exposes some Repository and workspace information about the selected
	 * element without exposing the UI model
	 */
	public class DistributionViewSelectedElement {
		public boolean isRepository = false;
		public boolean isWorkspaceGroup = false;
		public boolean isWorkspace = false;
		public boolean isReadOnly = false;
		public String repositoryDescription;
		public Node repoNode;
		public String wkspName;
		public String wkspPrefix;
		public Repository repository;
		public Credentials credentials;
	}

	/**
	 * Returns a {@see DistributionViewSelectedElement} if one and only one
	 * valid element is currently selected.
	 * 
	 */
	public DistributionViewSelectedElement getSelectedElement() {

		IStructuredSelection iss = (IStructuredSelection) viewer.getSelection();
		if (iss.isEmpty() || iss.size() > 1)
			return null;

		DistributionViewSelectedElement dvse = new DistributionViewSelectedElement();
		Object obj = iss.getFirstElement();
		if (obj instanceof RepoElem) {
			RepoElem re = (RepoElem) obj;
			dvse.isRepository = true;
			dvse.isReadOnly = re.isReadOnly();
			dvse.repository = re.getRepository();
			dvse.repoNode = re.getRepoNode();
			dvse.credentials = re.getCredentials();
			dvse.repositoryDescription = getRepositoryDescription(re);
		} else if (obj instanceof DistribGroupElem) {
			DistribGroupElem dge = (DistribGroupElem) obj;
			dvse.isWorkspaceGroup = true;
			dvse.isReadOnly = dge.isReadOnly();
			dvse.repository = dge.getRepoElem().getRepository();
			dvse.repoNode = dge.getRepoElem().getRepoNode();
			dvse.credentials = dge.getRepoElem().getCredentials();
			dvse.wkspPrefix = dge.getLabel();
			dvse.repositoryDescription = getRepositoryDescription(dge
					.getRepoElem());
		} else if (obj instanceof DistributionElem) {
			DistributionElem de = (DistributionElem) obj;
			dvse.isWorkspace = true;
			dvse.isReadOnly = de.isReadOnly();
			dvse.repository = de.getRepoElem().getRepository();
			dvse.repoNode = de.getRepoElem().getRepoNode();
			dvse.credentials = de.getRepoElem().getCredentials();
			dvse.wkspName = de.getWorkspaceName();
			dvse.repositoryDescription = getRepositoryDescription(de
					.getRepoElem());
		}
		return dvse;
	}

	private String getRepositoryDescription(RepoElem repo) {
		StringBuffer repoDesc = new StringBuffer();
		repoDesc.append(repo.getLabel());
		repoDesc.append(" (");
		repoDesc.append(JcrUtils.get(repo.getRepoNode(), ARGEO_URI));
		repoDesc.append(")");
		return repoDesc.toString();
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
						if (RepoConstants.DEFAULT_JAVA_REPOSITORY_ALIAS
								.equals(repoNode.getName()))
							repositories
									.add(new RepoElem(repoNode, true, false));
						else if (repoNode.hasNode(ARGEO_PASSWORD))
							repositories.add(new RepoElem(repoNode));
						else
							repositories
									.add(new RepoElem(repoNode, false, true));
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
			if (parentElement instanceof BrowserElem) {
				return ((BrowserElem) parentElement).getChildren();
			} else if (parentElement instanceof DistributionElem) {
				return ((DistributionElem) parentElement).getChildren();
			}
			return null;
		}

		public Object getParent(Object element) {
			// TODO register repo elem in distribution elem?
			return null;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof BrowserElem) {
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

	/** Add some view specific behaviours to the comparator */
	private class BrowserElementComparator extends ArtifactNamesComparator {
		@Override
		public int category(Object element) {
			// Home repository always first
			if (element instanceof RepoElem
					&& ((RepoElem) element).isHomeRepo())
				return 2;
			else
				return super.category(element);
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			// reverse order for versions
			if (e1 instanceof DistributionElem)
				return -super.compare(viewer, e1, e2);
			else
				return super.compare(viewer, e1, e2);
		}
	}

	/** Abstract class to simplify UI conditions build */
	abstract class BrowserElem {
		private boolean isHomeRepo = false;
		private boolean isReadOnly = false;

		public BrowserElem(boolean isHomeRepo, boolean isReadOnly) {
			this.isHomeRepo = isHomeRepo;
			this.isReadOnly = isReadOnly;
		}

		public BrowserElem() {
		}

		public abstract String getLabel();

		public abstract Object[] getChildren();

		public void dispose() {
		}

		public boolean isHomeRepo() {
			return isHomeRepo;
		}

		public boolean isReadOnly() {
			return isReadOnly;
		}
	}

	/** A software repository */
	private class RepoElem extends BrowserElem {
		private Node repoNode;
		private Repository repository;
		private Credentials credentials;

		public RepoElem(Node repoNode, boolean isHomeRepo, boolean isReadOnly) {
			super(isHomeRepo, isReadOnly);
			this.repoNode = repoNode;
		}

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
				// List<DistributionElem> distributionElems = new
				// ArrayList<DistributionElem>();
				Map<String, DistribGroupElem> children = new HashMap<String, DistributionsView.DistribGroupElem>();
				for (String workspaceName : workspaceNames) {
					// filter technical workspaces
					// FIXME: rely on a more robust rule than just wksp name
					if (workspaceName.lastIndexOf('-') > 0) {
						String prefix = workspaceName.substring(0,
								workspaceName.lastIndexOf('-'));
						if (!repoNode.hasNode(workspaceName))
							repoNode.addNode(workspaceName);
						repoNode.getSession().save();
						if (!children.containsKey(prefix)) {
							children.put(prefix, new DistribGroupElem(
									RepoElem.this, prefix));
						}
						// FIXME remove deleted workspaces
					}
				}
				return children.values().toArray();
			} catch (RepositoryException e) {
				throw new SlcException(
						"Cannot list workspaces for " + repoNode, e);
			} finally {
				JcrUtils.logoutQuietly(session);
			}
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

		public Node getRepoNode() {
			return repoNode;
		}

	}

	/**
	 * Abstracts a group of distribution, that is a bunch of workspaces with
	 * same prefix.
	 */
	private class DistribGroupElem extends BrowserElem {
		private RepoElem repoElem;
		private String name;

		public DistribGroupElem(RepoElem repoElem, String prefix) {
			super(repoElem.isHomeRepo(), repoElem.isReadOnly());
			this.repoElem = repoElem;
			this.name = prefix;
		}

		public Object[] getChildren() {
			repoElem.connect();
			Session session = null;
			try {
				Repository repository = repoElem.getRepository();
				Node repoNode = repoElem.getRepoNode();
				session = repository.login(repoElem.getCredentials());

				String[] workspaceNames = session.getWorkspace()
						.getAccessibleWorkspaceNames();
				List<DistributionElem> distributionElems = new ArrayList<DistributionElem>();
				for (String workspaceName : workspaceNames) {
					// filter technical workspaces
					if (workspaceName.startsWith(name)) {
						Node workspaceNode = repoNode.hasNode(workspaceName) ? repoNode
								.getNode(workspaceName) : repoNode
								.addNode(workspaceName);
						distributionElems.add(new DistributionElem(repoElem,
								workspaceNode));
						// FIXME remove deleted workspaces
					}
				}
				return distributionElems.toArray();
			} catch (RepositoryException e) {
				throw new SlcException("Cannot list workspaces for prefix "
						+ name, e);
			} finally {
				JcrUtils.logoutQuietly(session);
			}
		}

		public String getLabel() {
			return name;
		}

		public void dispose() {
		}

		public RepoElem getRepoElem() {
			return repoElem;
		}

	}

	/** Abstracts a distribution, that is a workspace */
	private static class DistributionElem extends TreeParent {
		private final RepoElem repoElem;
		private final Node workspaceNode;

		/**
		 * Helper to display only version when the workspace name is well
		 * formatted
		 */
		private static String formatName(Node workspaceNode) {
			String name = JcrUtils.getNameQuietly(workspaceNode);
			if (name != null && name.lastIndexOf('-') > 0)
				return name.substring(name.lastIndexOf('-') + 1);
			else
				return name;
		}

		public DistributionElem(RepoElem repoElem, Node workspaceNode) {
			super(formatName(workspaceNode));
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

		public boolean isReadOnly() {
			return repoElem.isReadOnly();
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
				Query countQuery = sourceSession
						.getWorkspace()
						.getQueryManager()
						.createQuery("select file from [nt:file] as file",
								Query.JCR_SQL2);
				QueryResult result = countQuery.execute();
				Long expectedCount = result.getNodes().getSize();
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
						distributionElem.getName(),
						getRepositoryDescription(distributionElem.getRepoElem()),
						distributionElem.getRepoElem().getRepository(),
						distributionElem.getWorkspaceName(), distributionElem
								.getCredentials());
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

	/*
	 * DEPENDENCY INJECTION
	 */
	public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
		this.repositoryFactory = repositoryFactory;
	}

	public void setKeyring(Keyring keyring) {
		this.keyring = keyring;
	}

	public void setNodeRepository(Repository repository) {
		this.nodeRepository = repository;
	}
}