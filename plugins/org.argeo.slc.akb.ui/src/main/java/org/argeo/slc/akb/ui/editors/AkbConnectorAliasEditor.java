package org.argeo.slc.akb.ui.editors;

import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbNames;
import org.argeo.slc.akb.AkbTypes;
import org.argeo.slc.akb.ui.AkbUiPlugin;
import org.argeo.slc.akb.ui.AkbUiUtils;
import org.argeo.slc.akb.utils.AkbJcrUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;

/**
 * Display and edit a connector Alias
 */
public class AkbConnectorAliasEditor extends AbstractAkbNodeEditor {
	// private final static Log log = LogFactory
	// .getLog(AkbConnectorAliasEditor.class);

	public final static String ID = AkbUiPlugin.PLUGIN_ID
			+ ".akbConnectorAliasEditor";

	private String[] connectorTypesLbl = new String[] { "JDBC", "SSH", "JCR" };
	private String[] connectorTypes = new String[] {
			AkbTypes.AKB_JDBC_CONNECTOR, AkbTypes.AKB_SSH_CONNECTOR,
			AkbTypes.AKB_JCR_CONNECTOR };

	private IManagedForm managedForm;

	/* CONTENT CREATION */
	@Override
	public void populateMainPage(Composite parent, IManagedForm managedForm) {
		parent.setLayout(AkbUiUtils.gridLayoutNoBorder());
		this.managedForm = managedForm;

		// First line main info
		Composite firstLine = getToolkit()
				.createComposite(parent, SWT.NO_FOCUS);
		firstLine.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		createConnectorAliasInfoCmp(firstLine);

		// Second line define defaut connector and test abilities
		Composite secondLine = getToolkit().createComposite(parent,
				SWT.NO_FOCUS);
		secondLine.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createDefaultTestConnectorCmp(secondLine);

	}

	private void createConnectorAliasInfoCmp(Composite parent) {
		parent.setLayout(new GridLayout(4, false));

		// first line: connector type and name
		getToolkit().createLabel(parent, "Connector Type");
		final Combo typeCmb = new Combo(parent, SWT.READ_ONLY);
		typeCmb.setItems(connectorTypesLbl);

		getToolkit().createLabel(parent, "Name");
		final Text titleTxt = getToolkit().createText(parent, "", SWT.BORDER);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		titleTxt.setLayoutData(gd);

		// 2nd line: description
		getToolkit().createLabel(parent, "Short Description");
		final Text descTxt = getToolkit().createText(parent, "", SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1);
		descTxt.setLayoutData(gd);

		// Part Management
		final AbstractFormPart part = new AbstractFormPart() {
			public void refresh() {
				super.refresh();
				// update display value
				AkbUiUtils.refreshFormTextWidget(titleTxt, getAkbNode(),
						Property.JCR_TITLE);
				AkbUiUtils.refreshFormTextWidget(descTxt, getAkbNode(),
						Property.JCR_DESCRIPTION);
			}
		};
		// Listeners
		AkbUiUtils.addTextModifyListener(titleTxt, getAkbNode(),
				Property.JCR_TITLE, part);
		AkbUiUtils.addTextModifyListener(descTxt, getAkbNode(),
				Property.JCR_DESCRIPTION, part);

		typeCmb.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {

				try { // TODO enhance this

					// retrieve old and new node type
					int oldIndex = -1;
					for (int i = 0; i < connectorTypes.length; i++) {
						if (getAkbNode().isNodeType(connectorTypes[i])) {
							oldIndex = i;
							break;
						}
					}
					int selIndex = typeCmb.getSelectionIndex();

					// insure something has really been modified
					if (selIndex < 0 || oldIndex == selIndex)
						return;

					// remove old mixin, add new and notify form
					if (oldIndex > -1)
						getAkbNode().removeMixin(connectorTypes[oldIndex]);
					getAkbNode().addMixin(connectorTypes[selIndex]);
					part.markDirty();
				} catch (RepositoryException e) {
					throw new AkbException(
							"Error while updating connector type", e);
				}
			}
		});

		managedForm.addPart(part);
	}

	private void createDefaultTestConnectorCmp(Composite parent) {
		String groupTitle = "Default test instance";
		parent.setLayout(new GridLayout());
		Group group = new Group(parent, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		getToolkit().adapt(group, false, false);

		group.setText(groupTitle);
		group.setLayout(AkbUiUtils.gridLayoutNoBorder());
		// 1st line: the URL

		Composite firstLine = getToolkit().createComposite(group);
		firstLine.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		firstLine.setLayout(new GridLayout(3, false));

		getToolkit().createLabel(firstLine, "URL");
		final Text urlTxt = getToolkit().createText(firstLine, "", SWT.BORDER);
		urlTxt.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		final Button testBtn = getToolkit().createButton(firstLine,
				"Test connection", SWT.PUSH);
		// testBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false,
		// false));

		// Part Management
		final AbstractFormPart part = new AbstractFormPart() {
			public void refresh() {
				super.refresh();
				// update display value
				AkbUiUtils.refreshFormTextWidget(urlTxt, getAkbNode(),
						AkbNames.AKB_CONNECTOR_URL);
			}
		};
		// Listeners
		AkbUiUtils.addTextModifyListener(urlTxt, getAkbNode(),
				AkbNames.AKB_CONNECTOR_URL, part);

		testBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean testSuccesfull = getAkbService().testConnector(
						getAkbNode());

				String name = AkbJcrUtils.get(getAkbNode(), Property.JCR_TITLE);
				String url = AkbJcrUtils.get(getAkbNode(),
						AkbNames.AKB_CONNECTOR_URL);

				String msg = "to " + name + " (" + url + ")";
				if (testSuccesfull)
					MessageDialog.openInformation(getSite().getShell(),
							"Test successful", "Successfully connected " + msg);
				else
					MessageDialog.openError(getSite().getShell(),
							"Test failure", "Unable to connect" + msg);
			}
		});

		managedForm.addPart(part);

	}

	@Override
	protected String getEditorId() {
		return ID;
	}
}