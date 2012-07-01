package org.argeo.slc.client.ui.dist.commands;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.ArgeoException;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.utils.CommandHelpers;
import org.argeo.slc.jcr.SlcTypes;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Create a copy of the chosen workspace in the current repository.
 */

public class CopyWorkspace extends AbstractHandler {
	private static final Log log = LogFactory.getLog(CopyWorkspace.class);
	public final static String ID = DistPlugin.ID + ".copyWorkspace";
	public final static String PARAM_WORKSPACE_NAME = DistPlugin.ID
			+ ".workspaceName";
	public final static String DEFAULT_LABEL = "Duplicate";
	public final static String DEFAULT_ICON_PATH = "icons/addItem.gif";

	/* DEPENDENCY INJECTION */
	private Repository repository;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String srcWorkspaceName = event.getParameter(PARAM_WORKSPACE_NAME);

		if (log.isTraceEnabled())
			log.debug("WORKSPACE " + srcWorkspaceName + " About to be copied");

		// MessageDialog.openWarning(DistPlugin.getDefault()
		// .getWorkbench().getDisplay().getActiveShell(),
		// "WARNING", "Not yet implemented");
		// return null;

		IWorkbenchWindow iww = DistPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow();
		InputDialog inputDialog = new InputDialog(iww.getShell(),
				"New copy of the current workspace",
				"Choose a name for the workspace to create", "", null);
		inputDialog.open();
		String newWorkspaceName = inputDialog.getValue();
		Session srcSession = null;
		Session newSession = null;
		try {
			srcSession = repository.login(srcWorkspaceName);

			// Create the workspace
			srcSession.getWorkspace().createWorkspace(newWorkspaceName);
			Node srcRootNode = srcSession.getRootNode();
			// log in the newly created workspace
			newSession = repository.login(newWorkspaceName);
			newSession.save();
			Node newRootNode = newSession.getRootNode();
			copy(srcRootNode, newRootNode);
			newSession.save();

			CommandHelpers.callCommand(RefreshDistributionsView.ID);
		} catch (RepositoryException re) {
			throw new ArgeoException(
					"Unexpected error while creating the new workspace.", re);
		} finally {
			if (srcSession != null)
				srcSession.logout();
			if (newSession != null)
				newSession.logout();
		}
		return null;
	}

	// FIXME : commons is frozen, cannot fix the problem directly there.
	// test and report corresponding patch
	private void copy(Node fromNode, Node toNode) {
		try {
			if (log.isDebugEnabled())
				log.debug("copy node :" + fromNode.getPath());

			// FIXME : small hack to enable specific workspace copy
			if (fromNode.isNodeType("rep:ACL")
					|| fromNode.isNodeType("rep:system")) {
				if (log.isTraceEnabled())
					log.trace("node skipped");
				return;
			}

			// add mixins
			for (NodeType mixinType : fromNode.getMixinNodeTypes()) {
				toNode.addMixin(mixinType.getName());
			}

			// Double check
			for (NodeType mixinType : toNode.getMixinNodeTypes()) {
				if (log.isDebugEnabled())
					log.debug(mixinType.getName());
			}

			// process properties
			PropertyIterator pit = fromNode.getProperties();
			properties: while (pit.hasNext()) {
				Property fromProperty = pit.nextProperty();
				String propName = fromProperty.getName();
				try {
					String propertyName = fromProperty.getName();
					if (toNode.hasProperty(propertyName)
							&& toNode.getProperty(propertyName).getDefinition()
									.isProtected())
						continue properties;

					if (fromProperty.getDefinition().isProtected())
						continue properties;

					if (propertyName.equals("jcr:created")
							|| propertyName.equals("jcr:createdBy")
							|| propertyName.equals("jcr:lastModified")
							|| propertyName.equals("jcr:lastModifiedBy"))
						continue properties;

					if (fromProperty.isMultiple()) {
						toNode.setProperty(propertyName,
								fromProperty.getValues());
					} else {
						toNode.setProperty(propertyName,
								fromProperty.getValue());
					}
				} catch (RepositoryException e) {
					throw new ArgeoException("Cannot property " + propName, e);
				}
			}

			// recursively process children nodes
			NodeIterator nit = fromNode.getNodes();
			while (nit.hasNext()) {
				Node fromChild = nit.nextNode();
				Integer index = fromChild.getIndex();
				String nodeRelPath = fromChild.getName() + "[" + index + "]";
				Node toChild;
				if (toNode.hasNode(nodeRelPath))
					toChild = toNode.getNode(nodeRelPath);
				else
					toChild = toNode.addNode(fromChild.getName(), fromChild
							.getPrimaryNodeType().getName());
				copy(fromChild, toChild);
			}

			// update jcr:lastModified and jcr:lastModifiedBy in toNode in case
			// they existed
			if (!toNode.getDefinition().isProtected()
					&& toNode.isNodeType(NodeType.MIX_LAST_MODIFIED))
				JcrUtils.updateLastModified(toNode);

			// Workaround to reduce session size: artifact is a saveable unity
			if (toNode.isNodeType(SlcTypes.SLC_ARTIFACT))
				toNode.getSession().save();

		} catch (RepositoryException e) {
			throw new ArgeoException("Cannot copy " + fromNode + " to "
					+ toNode, e);
		}
	}

	/* DEPENDENCY INJECTION */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
}
