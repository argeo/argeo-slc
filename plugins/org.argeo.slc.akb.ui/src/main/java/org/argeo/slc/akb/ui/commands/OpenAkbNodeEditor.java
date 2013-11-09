package org.argeo.slc.akb.ui.commands;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.eclipse.ui.dialogs.SingleValue;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbService;
import org.argeo.slc.akb.AkbTypes;
import org.argeo.slc.akb.ui.AkbUiPlugin;
import org.argeo.slc.akb.ui.editors.AkbConnectorAliasEditor;
import org.argeo.slc.akb.ui.editors.AkbEnvTemplateEditor;
import org.argeo.slc.akb.ui.editors.AkbNodeEditorInput;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Opens or show an AKB specific node in a single repository / single workspace
 * environment given some parameters, namely :
 * <ul>
 * <li>PARAM_NODE_JCR_ID: the corresponding JCR ID might be null to create a new
 * one</li>
 * <li>PARAM_NODE_TYPE: jcr type of the node to create</li>
 * <li>PARAM_PARENT_NODE_JCR_ID: Only used in the case of the creation of a new
 * node.</li>
 * </ul>
 */
public class OpenAkbNodeEditor extends AbstractHandler {
	public final static String ID = AkbUiPlugin.PLUGIN_ID
			+ ".openAkbNodeEditor";

	/* DEPENDENCY INJECTION */
	private Repository repository;
	private AkbService akbService;

	public final static String PARAM_NODE_JCR_ID = "param.nodeJcrId";
	public final static String PARAM_NODE_TYPE = "param.nodeType";
	public final static String PARAM_PARENT_NODE_JCR_ID = "param.parentNodeJcrId";

	public Object execute(ExecutionEvent event) throws ExecutionException {

		String nodeType = event.getParameter(PARAM_NODE_TYPE);
		String nodeJcrId = event.getParameter(PARAM_NODE_JCR_ID);
		String parentNodeJcrId = event.getParameter(PARAM_PARENT_NODE_JCR_ID);

		Session session = null;
		try {
			// caches current Page
			IWorkbenchPage currentPage = HandlerUtil.getActiveWorkbenchWindow(
					event).getActivePage();

			session = repository.login();
			Node node = null;

			if (nodeJcrId == null)
				if (parentNodeJcrId == null)
					throw new AkbException(
							"Define a parent node to create a new node");
				else
					node = createNewNode(session, nodeType, parentNodeJcrId);
			else
				node = session.getNodeByIdentifier(nodeJcrId);

			// no node has been found or created, return
			if (node == null)
				return null;

			String editorId = getEditorForNode(node);

			// no editor has been found, return silently
			if (editorId == null)
				return null;

			AkbNodeEditorInput eei = new AkbNodeEditorInput(
					node.getIdentifier());

			currentPage.openEditor(eei, editorId);
		} catch (PartInitException pie) {
			throw new AkbException(
					"Unexpected PartInitException while opening akb node editor",
					pie);
		} catch (RepositoryException e) {
			throw new AkbException("unexpected JCR error while opening "
					+ nodeType + " editor", e);
		} finally {
			JcrUtils.logoutQuietly(session);
		}
		return null;
	}

	private Node createNewNode(Session session, String nodeType,
			String parentNodeJcrId) throws RepositoryException {
		Node node = null;
		String name = SingleValue.ask("New name", "Create AKB item");

		if (name == null)
			return null;
		if (AkbTypes.AKB_ENV_TEMPLATE.equals(nodeType)) {
			node = akbService.createAkbTemplate(
					session.getNodeByIdentifier(parentNodeJcrId), name);
		} else {
			Node parentNode = session.getNodeByIdentifier(parentNodeJcrId);
			node = parentNode.addNode(name, nodeType);
			node.setProperty(Property.JCR_TITLE, name);
		}
		// corresponding node is saved but not checked in, in order to ease
		// cancel actions.
		session.save();
		return node;
	}

	private String getEditorForNode(Node node) throws RepositoryException {
		String editorId = null;
		if (node.isNodeType(AkbTypes.AKB_CONNECTOR_ALIAS))
			editorId = AkbConnectorAliasEditor.ID;
		else if (node.isNodeType(AkbTypes.AKB_ENV_TEMPLATE))
			editorId = AkbEnvTemplateEditor.ID;
		// else
		// throw new AkbException("Editor is undefined for node " + node);
		return editorId;
	}

	/* DEPENDENCY INJECTION */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void setAkbService(AkbService akbService) {
		this.akbService = akbService;
	}
}