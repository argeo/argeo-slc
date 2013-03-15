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
package org.argeo.slc.client.ui.dist;

import org.argeo.slc.client.ui.dist.views.DistributionsView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * User interface to manage a set of distributions split into several
 * repositories
 */
public class DistributionPerspective implements IPerspectiveFactory {

//	private RepositoryFactory repositoryFactory;
//	private Repository nodeRepository;

	public final static String ID = DistPlugin.ID + ".distributionPerspective";

	public void createInitialLayout(IPageLayout layout) {
		//initializeModel();

		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);

		IFolderLayout main = layout.createFolder("main", IPageLayout.LEFT,
				0.3f, editorArea);
		main.addView(DistributionsView.ID);
		main.addView("org.eclipse.ui.views.ProgressView");

	}

//	private void initializeModel() {
//		Session nodeSession = null;
//		try {
//			nodeSession = nodeRepository.login();
//
//			Node homeNode = UserJcrUtils.getUserHome(nodeSession);
//			if (homeNode == null) // anonymous
//				throw new SlcException("User must be authenticated.");
//
//			// make sure base directory is available
//			Node repos = JcrUtils.mkdirs(nodeSession, homeNode.getPath()
//					+ RepoConstants.REPOSITORIES_BASE_PATH);
//			nodeSession.save();
//
//			// register default local java repository
//			String alias = RepoConstants.DEFAULT_JAVA_REPOSITORY_ALIAS;
//			Repository javaRepository = ArgeoJcrUtils.getRepositoryByAlias(
//					repositoryFactory, alias);
//			if (javaRepository != null) {
//				if (!repos.hasNode(alias)) {
//					Node repoNode = repos.addNode(alias,
//							ArgeoTypes.ARGEO_REMOTE_REPOSITORY);
//					repoNode.setProperty(ArgeoNames.ARGEO_URI, "vm:///" + alias);
//					repoNode.addMixin(NodeType.MIX_TITLE);
//					repoNode.setProperty(Property.JCR_TITLE,
//							RepoConstants.DEFAULT_JAVA_REPOSITORY_LABEL);
//					nodeSession.save();
//				}
//			}
//		} catch (RepositoryException e) {
//			throw new SlcException("Cannot register repository", e);
//		} finally {
//			JcrUtils.logoutQuietly(nodeSession);
//		}
//	}

	// public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
	// this.repositoryFactory = repositoryFactory;
	// }
	//
	// public void setRepository(Repository nodeRepository) {
	// this.nodeRepository = nodeRepository;
	// }

}
