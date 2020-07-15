package org.argeo.cms.ui.workbench.jcr;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;

import org.argeo.cms.ui.workbench.WorkbenchUiPlugin;
import org.argeo.cms.ui.workbench.internal.jcr.parts.ChildNodesPage;
import org.argeo.cms.ui.workbench.internal.jcr.parts.GenericNodeEditorInput;
import org.argeo.cms.ui.workbench.internal.jcr.parts.GenericNodePage;
import org.argeo.cms.ui.workbench.internal.jcr.parts.GenericPropertyPage;
import org.argeo.cms.ui.workbench.internal.jcr.parts.NodePrivilegesPage;
import org.argeo.cms.ui.workbench.internal.jcr.parts.NodeVersionHistoryPage;
import org.argeo.eclipse.ui.EclipseUiException;
import org.argeo.jcr.JcrUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

/** Default form editor for a Jcr {@link Node} */
public class DefaultNodeEditor extends FormEditor {
	private static final long serialVersionUID = 8322127770921612239L;

	// private final static Log log =
	// LogFactory.getLog(GenericNodeEditor.class);
	public final static String ID = WorkbenchUiPlugin.PLUGIN_ID
			+ ".defaultNodeEditor";

	private Node currentNode;

	private GenericNodePage genericNodePage;
	private GenericPropertyPage genericPropertyPage;
	private ChildNodesPage childNodesPage;
	private NodePrivilegesPage nodeRightsManagementPage;
	private NodeVersionHistoryPage nodeVersionHistoryPage;

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init(site, input);
		GenericNodeEditorInput nei = (GenericNodeEditorInput) getEditorInput();
		currentNode = nei.getCurrentNode();
		this.setPartName(JcrUtils.lastPathElement(nei.getPath()));
	}

	@Override
	protected void addPages() {
		try {
			genericPropertyPage = new GenericPropertyPage(this,
					WorkbenchUiPlugin.getMessage("genericNodePageTitle"),
					currentNode);
			addPage(genericPropertyPage);

			childNodesPage = new ChildNodesPage(this,
					WorkbenchUiPlugin.getMessage("childNodesPageTitle"),
					currentNode);
			addPage(childNodesPage);

			AccessControlManager accessControlManager = currentNode
					.getSession().getAccessControlManager();
			List<Privilege> privileges = new ArrayList<Privilege>();
			privileges.add(accessControlManager
					.privilegeFromName(Privilege.JCR_READ_ACCESS_CONTROL));
			if (accessControlManager.hasPrivileges(currentNode.getPath(),
					privileges.toArray(new Privilege[0]))) {
				nodeRightsManagementPage = new NodePrivilegesPage(this,
						WorkbenchUiPlugin
								.getMessage("nodeRightsManagementPageTitle"),
						currentNode);
				addPage(nodeRightsManagementPage);
			}
			if (currentNode.isNodeType(NodeType.MIX_VERSIONABLE)) {
				nodeVersionHistoryPage = new NodeVersionHistoryPage(this,
						WorkbenchUiPlugin
								.getMessage("nodeVersionHistoryPageTitle"),
						currentNode);
				addPage(nodeVersionHistoryPage);
			}

			privileges = new ArrayList<Privilege>();
			privileges.add(accessControlManager
					.privilegeFromName(Privilege.JCR_ALL));
			if (accessControlManager.hasPrivileges(currentNode.getPath(),
					privileges.toArray(new Privilege[0]))) {
				genericNodePage = new GenericNodePage(
						this,
						WorkbenchUiPlugin.getMessage("propertyEditorPageTitle"),
						currentNode);
				addPage(genericNodePage);
			}

		} catch (RepositoryException e) {
			throw new EclipseUiException("Cannot get node info for "
					+ currentNode, e);
		} catch (PartInitException e) {
			throw new EclipseUiException("Cannot add page "
					+ "on node editor for " + currentNode, e);
		}
	}

	@Override
	public void doSaveAs() {
		// unused compulsory method
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			// Automatically commit all pages of the editor
			commitPages(true);
			firePropertyChange(PROP_DIRTY);
		} catch (Exception e) {
			throw new EclipseUiException("Error while saving node", e);
		}

	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	Node getCurrentNode() {
		return currentNode;
	}
}
