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
import org.argeo.slc.akb.ui.AkbMessages;
import org.argeo.slc.akb.ui.AkbUiPlugin;
import org.argeo.slc.akb.ui.dialogs.AddItemDialog;
import org.argeo.slc.akb.ui.editors.AkbNodeEditorInput;
import org.argeo.slc.akb.ui.editors.ConnectorAliasEditor;
import org.argeo.slc.akb.ui.editors.EnvTemplateEditor;
import org.argeo.slc.akb.ui.editors.JdbcQueryTemplateEditor;
import org.argeo.slc.akb.ui.editors.SshCommandTemplateEditor;
import org.argeo.slc.akb.ui.editors.SshFileTemplateEditor;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;
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
	public final static String PARAM_NODE_SUBTYPE = "param.nodeSubtype";
	public final static String PARAM_CURR_ENV_JCR_ID = "param.currEnvJcrId";
	public final static String PARAM_PARENT_NODE_JCR_ID = "param.parentNodeJcrId";

	public Object execute(ExecutionEvent event) throws ExecutionException {

		String nodeType = event.getParameter(PARAM_NODE_TYPE);
		String nodeSubtype = event.getParameter(PARAM_NODE_SUBTYPE);
		String currEnvJcrId = event.getParameter(PARAM_CURR_ENV_JCR_ID);
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
					node = createNewNode(session, nodeType, nodeSubtype,
							parentNodeJcrId);
			else
				node = session.getNodeByIdentifier(nodeJcrId);

			// no node has been found or created, return
			if (node == null)
				return null;

			String editorId = getEditorForNode(node);

			// no editor has been found, return silently
			if (editorId == null)
				return null;

			AkbNodeEditorInput eei = new AkbNodeEditorInput(currEnvJcrId,
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
			String nodeSubtype, String parentNodeJcrId)
			throws RepositoryException {
		Node node = null;

		if (AkbTypes.AKB_ITEM.equals(nodeType)) {
			Node parNode = session.getNodeByIdentifier(parentNodeJcrId);
			AddItemDialog dialog = new AddItemDialog(Display.getDefault()
					.getActiveShell(), "Add new item", parNode);
			dialog.open();
			node = dialog.getNewNode();
		} else {
			String name = SingleValue
					.ask("Create "
							+ AkbMessages
									.getLabelForType(nodeSubtype == null ? nodeType
											: nodeSubtype),
							"Please enter a name for the corresponding "
									+ AkbMessages
											.getLabelForType(nodeSubtype == null ? nodeType
													: nodeSubtype));
			if (name == null)
				return null;
			if (AkbTypes.AKB_ENV_TEMPLATE.equals(nodeType)) {
				node = akbService.createAkbTemplate(
						session.getNodeByIdentifier(parentNodeJcrId), name);
			} else if (AkbTypes.AKB_CONNECTOR_ALIAS.equals(nodeType)) {
				// the Jcr ID of the corresponding template must be passed to
				// create a new alias
				node = session.getNodeByIdentifier(parentNodeJcrId);
				akbService.createConnectorAlias(node, name, nodeSubtype);
			} else {
				Node parentNode = session.getNodeByIdentifier(parentNodeJcrId);
				node = parentNode.addNode(name, nodeType);
				node.setProperty(Property.JCR_TITLE, name);
			}
		}
		// corresponding node is saved but not checked in, in order to ease
		// cancel actions.
		session.save();
		return node;
	}

	private String getEditorForNode(Node node) throws RepositoryException {
		String editorId = null;
		if (node.isNodeType(AkbTypes.AKB_CONNECTOR_ALIAS))
			editorId = ConnectorAliasEditor.ID;
		else if (node.isNodeType(AkbTypes.AKB_ENV_TEMPLATE))
			editorId = EnvTemplateEditor.ID;
		else if (node.isNodeType(AkbTypes.AKB_SSH_FILE))
			editorId = SshFileTemplateEditor.ID;
		else if (node.isNodeType(AkbTypes.AKB_SSH_COMMAND))
			editorId = SshCommandTemplateEditor.ID;
		else if (node.isNodeType(AkbTypes.AKB_JDBC_QUERY))
			editorId = JdbcQueryTemplateEditor.ID;
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