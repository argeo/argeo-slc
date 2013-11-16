package org.argeo.slc.akb.ui.commands;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbService;
import org.argeo.slc.akb.AkbTypes;
import org.argeo.slc.akb.ui.AkbUiPlugin;
import org.argeo.slc.akb.ui.dialogs.AddItemDialog;
import org.argeo.slc.akb.ui.wizards.CreateEnvInstanceWizard;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Opens corresponding wizard to create a new AKB Node
 */
public class CreateAkbNode extends AbstractHandler {
	public final static String ID = AkbUiPlugin.PLUGIN_ID + ".createAkbNode";

	/* DEPENDENCY INJECTION */
	private AkbService akbService;

	public final static String PARAM_PARENT_NODE_JCR_ID = "param.parentNodeJcrId";
	public final static String PARAM_NODE_TYPE = "param.nodeType";

	public Object execute(ExecutionEvent event) throws ExecutionException {

		String parentNodeJcrId = event.getParameter(PARAM_PARENT_NODE_JCR_ID);
		String nodeType = event.getParameter(PARAM_NODE_TYPE);
		Session session = null;
		try {
			session = akbService.getRepository().login();
			Node node = createNewNode(HandlerUtil.getActiveShell(event),
					session, nodeType, parentNodeJcrId);
			// no node has been created, return
			if (node == null)
				return null;
		} catch (RepositoryException e) {
			throw new AkbException("unexpected JCR error while opening "
					+ nodeType + " editor", e);
		} finally {
			JcrUtils.logoutQuietly(session);
		}
		return null;
	}

	private Node createNewNode(Shell shell, Session session, String nodeType,
			String parentNodeJcrId) throws RepositoryException {
		Node node = null;
		if (AkbTypes.AKB_ITEM.equals(nodeType)) {
			Node parNode = session.getNodeByIdentifier(parentNodeJcrId);
			AddItemDialog dialog = new AddItemDialog(shell, "Add new item",
					parNode);
			dialog.open();
			node = dialog.getNewNode();
		} else if (AkbTypes.AKB_ENV.equals(nodeType)) {
			CreateEnvInstanceWizard wizard = new CreateEnvInstanceWizard(
					akbService, session);
			WizardDialog dialog = new WizardDialog(shell, wizard);
			dialog.open();
			node = wizard.getCreatedNode();
		} else
			return null;
		// {
		// String name = SingleValue
		// .ask("Create "
		// + AkbMessages
		// .getLabelForType(nodeSubtype == null ? nodeType
		// : nodeSubtype),
		// "Please enter a name for the corresponding "
		// + AkbMessages
		// .getLabelForType(nodeSubtype == null ? nodeType
		// : nodeSubtype));
		// if (name == null)
		// return null;
		// if (AkbTypes.AKB_ENV_TEMPLATE.equals(nodeType)) {
		// node = akbService.createAkbTemplate(
		// session.getNodeByIdentifier(parentNodeJcrId), name);
		// } else if (AkbTypes.AKB_CONNECTOR_ALIAS.equals(nodeType)) {
		// // the Jcr ID of the corresponding template must be passed to
		// // create a new alias
		// node = session.getNodeByIdentifier(parentNodeJcrId);
		// akbService.createConnectorAlias(node, name, nodeSubtype);
		// } else {
		// Node parentNode = session.getNodeByIdentifier(parentNodeJcrId);
		// node = parentNode.addNode(name, nodeType);
		// node.setProperty(Property.JCR_TITLE, name);
		// }
		// }
		// corresponding node is saved but not checked in, in order to ease
		// cancel actions.
		session.save();
		return node;
	}

	public void setAkbService(AkbService akbService) {
		this.akbService = akbService;
	}
}