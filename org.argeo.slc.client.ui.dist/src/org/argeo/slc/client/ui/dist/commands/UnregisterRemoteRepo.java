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
package org.argeo.slc.client.ui.dist.commands;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.eclipse.ui.workbench.CommandUtils;
import org.argeo.jcr.ArgeoTypes;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Un-register a remote repository by deleting the corresponding RepoNode from
 * the node Repository. It does not affect the repository instance
 */
public class UnregisterRemoteRepo extends AbstractHandler {
	// private static final Log log = LogFactory
	// .getLog(UnregisterRemoteRepo.class);
	
	public final static String ID = DistPlugin.PLUGIN_ID + ".unregisterRemoteRepo";
	public final static String DEFAULT_LABEL = "Unregister";
	public final static ImageDescriptor DEFAULT_ICON = DistPlugin
			.getImageDescriptor("icons/removeItem.gif");

	public final static String PARAM_REPO_PATH = DistPlugin.PLUGIN_ID
			+ ".repoNodePath";

	// DEPENCY INJECTION
	private Repository nodeRepository;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		Session session = null;
		String repoPath = event.getParameter(PARAM_REPO_PATH);
		if (repoPath == null)
			return null;

		try {
			session = nodeRepository.login();
			Node rNode = session.getNode(repoPath);
			if (rNode.isNodeType(ArgeoTypes.ARGEO_REMOTE_REPOSITORY)) {

				String alias = rNode.getProperty(Property.JCR_TITLE)
						.getString();
				String msg = "Your are about to unregister remote repository: "
						+ alias + "\n" + "Are you sure you want to proceed ?";

				boolean result = MessageDialog.openConfirm(DistPlugin
						.getDefault().getWorkbench().getDisplay()
						.getActiveShell(), "Confirm Delete", msg);

				if (result) {
					rNode.remove();
					session.save();
				}
				CommandUtils.callCommand(RefreshDistributionsView.ID);
			}
		} catch (RepositoryException e) {
			throw new SlcException(
					"Unexpected error while unregistering remote repository.",
					e);
		} finally {
			JcrUtils.logoutQuietly(session);
		}
		return null;
	}

	// DEPENCY INJECTION
	public void setNodeRepository(Repository nodeRepository) {
		this.nodeRepository = nodeRepository;
	}
}