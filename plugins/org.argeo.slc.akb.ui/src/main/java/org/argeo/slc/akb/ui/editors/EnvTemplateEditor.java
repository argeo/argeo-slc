package org.argeo.slc.akb.ui.editors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.ObservationManager;

import org.argeo.eclipse.ui.jcr.AsyncUiEventListener;
import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbTypes;
import org.argeo.slc.akb.ui.AkbUiPlugin;
import org.argeo.slc.akb.ui.AkbUiUtils;
import org.argeo.slc.akb.ui.commands.OpenAkbNodeEditor;
import org.argeo.slc.akb.ui.composites.ConnectorAliasSmallComposite;
import org.argeo.slc.akb.ui.composites.MixTitleComposite;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;

/**
 * Display and edit an environment template
 */
public class EnvTemplateEditor extends AbstractAkbNodeEditor {

	public final static String ID = AkbUiPlugin.PLUGIN_ID
			+ ".envTemplateEditor";

	// Observer
	private final static String[] observedNodes = { AkbTypes.AKB_CONNECTOR_FOLDER };
	private ConnectorObserver connectorObserver;

	/* CONTENT CREATION */
	@Override
	public void populateMainPage(Composite parent, IManagedForm managedForm) {
		parent.setLayout(new GridLayout());
		// First line main info
		MixTitleComposite mixTitleCmp = new MixTitleComposite(parent,
				SWT.NO_FOCUS, getToolkit(), managedForm, getAkbNode());
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd.heightHint = 200;
		mixTitleCmp.setLayoutData(gd);

		// Second line : the defined editor
		Group group = new Group(parent, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		getToolkit().adapt(group, false, false);
		String groupTitle = "Connector Aliases";
		group.setText(groupTitle);
		populateDisplayConnectorPanel(managedForm, group, getAkbNode());

		// add context menu
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(group);
		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				aboutToShow(manager);
			}
		});
		group.setMenu(menu);
		menuManager.setRemoveAllWhenShown(true);
	}

	private void aboutToShow(IMenuManager menu) {
		try {
			// initialization
			String submenuID = "subMenu.addAlias";
			IWorkbenchWindow window = AkbUiPlugin.getDefault().getWorkbench()
					.getActiveWorkbenchWindow();
			Node connectorParent = getAkbNode();
			IContributionItem ici = menu.find(submenuID);
			if (ici != null)
				menu.remove(ici);
			Map<String, String> params = new HashMap<String, String>();
			params.put(OpenAkbNodeEditor.PARAM_PARENT_NODE_JCR_ID,
					connectorParent.getIdentifier());
			params.put(OpenAkbNodeEditor.PARAM_NODE_TYPE,
					AkbTypes.AKB_CONNECTOR_ALIAS);

			MenuManager subMenu = new MenuManager("Add connector alias",
					submenuID);
			// JDBC
			Map<String, String> tmpParams = new HashMap<String, String>();
			tmpParams.putAll(params);
			tmpParams.put(OpenAkbNodeEditor.PARAM_NODE_SUBTYPE,
					AkbTypes.AKB_JDBC_CONNECTOR);
			String currItemId = "cmd.createJDBCAlias";
			IContributionItem currItem = subMenu.find(currItemId);
			if (currItem != null)
				subMenu.remove(currItem);
			subMenu.add(AkbUiUtils.createContributionItem(subMenu, window,
					currItemId, OpenAkbNodeEditor.ID, "JDBC", null, tmpParams));

			// SSH
			tmpParams = new HashMap<String, String>();
			tmpParams.putAll(params);
			tmpParams.put(OpenAkbNodeEditor.PARAM_NODE_SUBTYPE,
					AkbTypes.AKB_SSH_CONNECTOR);
			currItemId = "cmd.createSSHAlias";
			currItem = subMenu.find(currItemId);
			if (currItem != null)
				subMenu.remove(currItem);
			subMenu.add(AkbUiUtils.createContributionItem(subMenu, window,
					currItemId, OpenAkbNodeEditor.ID, "SSH", null, tmpParams));

			menu.add(subMenu);

		} catch (RepositoryException e) {
			throw new AkbException("Unable to refresh context menu", e);
		}
	}

	/** Manage display and update of defined connector aliases */
	public void populateDisplayConnectorPanel(final IManagedForm managedForm,
			final Composite panel, final Node entity) {
		GridLayout gl = AkbUiUtils.gridLayoutNoBorder();
		gl.marginTop = 10;
		panel.setLayout(gl);

		final Map<String, Composite> connectorsCmps = new HashMap<String, Composite>();
		AbstractFormPart formPart = new AbstractFormPart() {
			public void refresh() {
				try {
					super.refresh();
					// first: initialise composite for new connectors
					Node connectorPar = getAkbNode().getNode(
							AkbTypes.AKB_CONNECTOR_FOLDER);
					NodeIterator ni = connectorPar.getNodes();
					while (ni.hasNext()) {
						Node currNode = ni.nextNode();
						String currJcrId = currNode.getIdentifier();
						if (!connectorsCmps.containsKey(currJcrId)) {
							Composite currCmp = new ConnectorAliasSmallComposite(
									panel, SWT.NO_FOCUS, getToolkit(),
									managedForm, currNode, getAkbService());
							currCmp.setLayoutData(new GridData(SWT.FILL,
									SWT.TOP, true, false));
							connectorsCmps.put(currJcrId, currCmp);
						}
					}

					// then remove necessary composites
					Session session = connectorPar.getSession();
					for (String jcrId : connectorsCmps.keySet()) {
						// TODO: enhance this
						Composite currCmp = connectorsCmps.get(jcrId);
						try {
							session.getNodeByIdentifier(jcrId);
						} catch (ItemNotFoundException infe) {
							currCmp.dispose();
						}
					}
					panel.layout();
				} catch (RepositoryException e) {
					throw new AkbException("Cannot refresh connectors group", e);
				}
			}
		};
		formPart.refresh();
		managedForm.addPart(formPart);

		// Initialize observer
		try {
			ObservationManager observationManager = getSession().getWorkspace()
					.getObservationManager();
			connectorObserver = new ConnectorObserver(panel.getDisplay(),
					formPart);
			// observe tree changes under All results
			observationManager.addEventListener(connectorObserver,
					Event.NODE_ADDED | Event.NODE_REMOVED, getAkbNode()
							.getNode(AkbTypes.AKB_CONNECTOR_FOLDER).getPath(),
					true, null, observedNodes, false);
		} catch (RepositoryException e) {
			throw new AkbException("Cannot register listeners", e);
		}

	}

	class ConnectorObserver extends AsyncUiEventListener {

		private AbstractFormPart formPart;

		public ConnectorObserver(Display display, AbstractFormPart formPart) {
			super(display);
			this.formPart = formPart;
		}

		@Override
		protected Boolean willProcessInUiThread(List<Event> events)
				throws RepositoryException {
			return true;
		}

		protected void onEventInUiThread(List<Event> events)
				throws RepositoryException {
			try {
				formPart.refresh();
			} catch (Exception e) {
				// silently fail
				e.printStackTrace();
			}
		}
	}

	@Override
	protected String getEditorId() {
		return ID;
	}
}