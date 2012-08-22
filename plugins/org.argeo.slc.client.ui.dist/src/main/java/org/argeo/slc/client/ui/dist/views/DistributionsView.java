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
import javax.jcr.GuestCredentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.eclipse.ui.AbstractTreeContentProvider;
import org.argeo.eclipse.ui.ErrorFeedback;
import org.argeo.eclipse.ui.TreeParent;
import org.argeo.jcr.ArgeoJcrUtils;
import org.argeo.jcr.JcrUtils;
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
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
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

public class DistributionsView extends ViewPart implements SlcNames {
	private final static Log log = LogFactory.getLog(DistributionsView.class);
	public final static String ID = DistPlugin.ID + ".distributionsView";

	private Repository repository;
	private RepositoryFactory repositoryFactory;

	private TreeViewer viewer;

	private List<RepositoryElem> repositories = new ArrayList<DistributionsView.RepositoryElem>();

	@Override
	public void createPartControl(Composite parent) {
		// Define the TableViewer
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.BORDER);

		TreeViewerColumn col = new TreeViewerColumn(viewer, SWT.NONE);
		col.getColumn().setWidth(200);
		col.getColumn().setText("Workspace");
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return element.toString();
			}
		});

		final Tree table = viewer.getTree();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

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

		// Initializes repositories
		// TODO make it more generic, with remote repositories etc.
		repositories.add(new RepositoryElem("java", repository, null));

		// Remote
		String uri = null;
		Credentials credentials = null;
		Repository remoteRepository = null;

		uri = "http://dev.argeo.org/org.argeo.jcr.webapp/pub/java";
		credentials = new GuestCredentials();
		remoteRepository = ArgeoJcrUtils.getRepositoryByUri(repositoryFactory, uri);
		repositories.add(new RepositoryElem("anonymous@dev.argeo.org//java",
				remoteRepository, credentials));

		uri = "http://localhost:7070/org.argeo.jcr.webapp/pub/java";
		credentials = new GuestCredentials();
		remoteRepository = ArgeoJcrUtils.getRepositoryByUri(repositoryFactory, uri);
		repositories.add(new RepositoryElem("anonymous@localhost//java",
				remoteRepository, credentials));

		uri = "http://localhost:7070/org.argeo.jcr.webapp/remoting/java";
		credentials = new SimpleCredentials(System.getProperty("user.name"),
				"".toCharArray());
		remoteRepository = ArgeoJcrUtils.getRepositoryByUri(repositoryFactory, uri);
		repositories.add(new RepositoryElem("@localhost//java",
				remoteRepository, credentials));

		viewer.setInput(getSite());

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

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
		this.repositoryFactory = repositoryFactory;
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
					CreateWorkspace.DEFAULT_ICON_PATH,
					tp instanceof RepositoryElem);

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

	private class DistributionsContentProvider extends
			AbstractTreeContentProvider {

		public Object[] getElements(Object arg0) {
			return repositories.toArray();
		}

	}

	/** Wraps a repository **/
	private static class RepositoryElem extends TreeParent {
		// private final Repository repository;
		private Session defaultSession;

		public RepositoryElem(String name, Repository repository,
				Credentials credentials) {
			super(name);
			try {
				defaultSession = repository.login(credentials);
				String[] workspaceNames = defaultSession.getWorkspace()
						.getAccessibleWorkspaceNames();
				for (String workspace : workspaceNames)
					addChild(new DistributionElem(repository, workspace,
							credentials));
			} catch (RepositoryException e) {
				ErrorFeedback.show("Cannot log to repository", e);
			}
		}

		@Override
		public synchronized void dispose() {
			if (log.isTraceEnabled())
				log.trace("Disposing RepositoryElement");
			JcrUtils.logoutQuietly(defaultSession);
			super.dispose();
		}
	}

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
		for (RepositoryElem re : repositories)
			re.dispose();
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