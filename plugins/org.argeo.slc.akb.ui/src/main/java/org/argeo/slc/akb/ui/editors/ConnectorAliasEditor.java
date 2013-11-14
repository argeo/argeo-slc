package org.argeo.slc.akb.ui.editors;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.argeo.eclipse.ui.ErrorFeedback;
import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbNames;
import org.argeo.slc.akb.ui.AkbUiPlugin;
import org.argeo.slc.akb.ui.AkbUiUtils;
import org.argeo.slc.akb.utils.AkbJcrUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;

/**
 * Display and edit a connector Alias
 */
public class ConnectorAliasEditor extends AbstractAkbNodeEditor {
	// private final static Log log = LogFactory
	// .getLog(AkbConnectorAliasEditor.class);

	public final static String ID = AkbUiPlugin.PLUGIN_ID
			+ ".connectorAliasEditor";

	private IManagedForm managedForm;
	private Node activeConnector;

	/* CONTENT CREATION */
	@Override
	public void populateMainPage(Composite parent, IManagedForm managedForm) {
		parent.setLayout(AkbUiUtils.gridLayoutNoBorder());

		// TODO clean this
		// Initialization
		this.managedForm = managedForm;
		// enable dynamic change of the active connector
		try {
			activeConnector = getAkbNode().getNode(
					AkbNames.AKB_DEFAULT_TEST_CONNECTOR);
		} catch (RepositoryException e) {
			throw new AkbException("unable to retrieve active connector node",
					e);
		}

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
		parent.setLayout(new GridLayout(2, false));

		// Name
		final Text titleTxt = getToolkit().createText(parent, "", SWT.BORDER);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, false, false);
		gd.minimumWidth = 200;
		gd.widthHint = 200;
		titleTxt.setLayoutData(gd);

		// Description
		final Text descTxt = getToolkit().createText(parent, "", SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		descTxt.setLayoutData(gd);

		// Part Management
		final AbstractFormPart part = new AbstractFormPart() {
			public void refresh() {
				super.refresh();
				// update display value
				AkbUiUtils.refreshFormTextWidget(titleTxt, getAkbNode(),
						Property.JCR_TITLE, "Name");
				AkbUiUtils.refreshFormTextWidget(descTxt, getAkbNode(),
						Property.JCR_DESCRIPTION, "Short description");
			}
		};
		// Listeners
		AkbUiUtils.addTextModifyListener(titleTxt, getAkbNode(),
				Property.JCR_TITLE, part);
		AkbUiUtils.addTextModifyListener(descTxt, getAkbNode(),
				Property.JCR_DESCRIPTION, part);

		managedForm.addPart(part);
	}

	protected void updatePartNameAndToolTip() {
		super.updatePartNameAndToolTip();
		// TODO update editor image
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
		firstLine.setLayout(new GridLayout(2, false));

		getToolkit().createLabel(firstLine, "URL");
		final Text urlTxt = getToolkit().createText(firstLine, "", SWT.BORDER);
		urlTxt.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		getToolkit().createLabel(firstLine, "User");
		final Text userTxt = getToolkit().createText(firstLine, "", SWT.BORDER);
		userTxt.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		final Button testBtn = getToolkit().createButton(firstLine,
				"Test connection", SWT.PUSH);
		// testBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false,
		// false));

		// Part Management
		final AbstractFormPart part = new AbstractFormPart() {
			public void refresh() {
				super.refresh();
				// update display value
				AkbUiUtils.refreshFormTextWidget(urlTxt, activeConnector,
						AkbNames.AKB_CONNECTOR_URL);
				AkbUiUtils.refreshFormTextWidget(userTxt, activeConnector,
						AkbNames.AKB_CONNECTOR_USER);
			}
		};
		// Listeners
		AkbUiUtils.addTextModifyListener(urlTxt, activeConnector,
				AkbNames.AKB_CONNECTOR_URL, part);
		AkbUiUtils.addTextModifyListener(userTxt, activeConnector,
				AkbNames.AKB_CONNECTOR_USER, part);

		testBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean testSuccesfull;

				try {
					testSuccesfull = getAkbService().testConnector(
							activeConnector);
				} catch (Exception e1) {
					testSuccesfull = false;
					ErrorFeedback.show("Cannot test connection", e1);
				}

				String name = AkbJcrUtils.get(activeConnector,
						Property.JCR_TITLE);
				String url = AkbJcrUtils.get(activeConnector,
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