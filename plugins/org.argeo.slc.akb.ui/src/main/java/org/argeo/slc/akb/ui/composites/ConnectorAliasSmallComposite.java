package org.argeo.slc.akb.ui.composites;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.argeo.eclipse.ui.ErrorFeedback;
import org.argeo.eclipse.ui.utils.CommandUtils;
import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbNames;
import org.argeo.slc.akb.AkbService;
import org.argeo.slc.akb.ui.AkbImages;
import org.argeo.slc.akb.ui.AkbUiUtils;
import org.argeo.slc.akb.ui.commands.DeleteAkbNodes;
import org.argeo.slc.akb.utils.AkbJcrUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

/** Default composite to display a connector alias. */
public class ConnectorAliasSmallComposite extends Composite {

	private final AkbService akbService;
	private final Node connectorAlias;
	private final Node activeConnector;
	private final FormToolkit toolkit;
	private final IManagedForm form;

	// Don't forget to unregister on dispose
	private AbstractFormPart formPart;

	// To enable set focus
	private Text titleTxt;

	public ConnectorAliasSmallComposite(Composite parent, int style,
			FormToolkit toolkit, IManagedForm form, Node akbNode,
			AkbService akbService) {
		super(parent, style);
		this.connectorAlias = akbNode;
		try {
			this.activeConnector = akbNode
					.getNode(AkbNames.AKB_DEFAULT_TEST_CONNECTOR);
		} catch (RepositoryException e) {
			throw new AkbException("Unable to get activeConnector for node", e);
		}
		this.toolkit = toolkit;
		this.form = form;
		this.akbService = akbService;
		populate();
		toolkit.adapt(this);
	}

	private void populate() {
		// Initialization
		Composite parent = this;
		createConnectorAliasInfoCmp(parent);
	}

	private void createConnectorAliasInfoCmp(Composite parent) {
		GridLayout gl = AkbUiUtils.gridLayoutNoBorder();
		gl.marginBottom = 15;

		parent.setLayout(gl);
		Composite firstLine = toolkit.createComposite(parent, SWT.NO_FOCUS);
		firstLine.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		firstLine.setLayout(new GridLayout(9, false));

		// Image
		final Label image = toolkit.createLabel(firstLine, "", SWT.NONE);
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		try {
			image.setImage(AkbImages.getImageForAkbNodeType(activeConnector
					.getPrimaryNodeType().getName()));
		} catch (RepositoryException e2) {
			// silent
		}
		image.setLayoutData(gd);

		// Name
		final Text titleTxt = toolkit.createText(firstLine, "", SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.TOP, false, false);
		gd.widthHint = 150;
		titleTxt.setLayoutData(gd);

		toolkit.createLabel(firstLine, "URL");
		final Text urlTxt = toolkit.createText(firstLine, "", SWT.BORDER);
		urlTxt.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		toolkit.createLabel(firstLine, "User");
		final Text userTxt = toolkit.createText(firstLine, "", SWT.BORDER);
		gd = new GridData(SWT.LEFT, SWT.TOP, false, false);
		gd.widthHint = 150;
		userTxt.setLayoutData(gd);

		final Link testBtn = new Link(firstLine, SWT.NONE);
		toolkit.adapt(testBtn, false, false);
		testBtn.setText("<a>Test</a>");

		final Link removeBtn = new Link(firstLine, SWT.NONE);
		toolkit.adapt(removeBtn, false, false);
		removeBtn.setText("<a>Delete</a>");

		// createDefaultTestConnectorCmp(secondLine);

		// Description
		final Text descTxt = toolkit.createText(parent, "", SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		descTxt.setLayoutData(gd);

		// Part Management
		formPart = new AbstractFormPart() {
			public void refresh() {
				super.refresh();
				// update display value
				AkbUiUtils.refreshFormTextWidget(titleTxt, connectorAlias,
						Property.JCR_TITLE, "Name");
				AkbUiUtils.refreshFormTextWidget(descTxt, connectorAlias,
						Property.JCR_DESCRIPTION, "Short description");
				AkbUiUtils.refreshFormTextWidget(urlTxt, activeConnector,
						AkbNames.AKB_CONNECTOR_URL);
				AkbUiUtils.refreshFormTextWidget(userTxt, activeConnector,
						AkbNames.AKB_CONNECTOR_USER);
			}
		};
		// Listeners
		AkbUiUtils.addTextModifyListener(titleTxt, connectorAlias,
				Property.JCR_TITLE, formPart);
		AkbUiUtils.addTextModifyListener(urlTxt, activeConnector,
				AkbNames.AKB_CONNECTOR_URL, formPart);
		AkbUiUtils.addTextModifyListener(userTxt, activeConnector,
				AkbNames.AKB_CONNECTOR_USER, formPart);
		AkbUiUtils.addTextModifyListener(descTxt, connectorAlias,
				Property.JCR_DESCRIPTION, formPart);

		testBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean testSuccesfull;
				try {
					testSuccesfull = akbService.testConnector(activeConnector);
				} catch (Exception e1) {
					testSuccesfull = false;
					ErrorFeedback.show("Cannot test connection", e1);
				}
				String name = AkbJcrUtils.get(activeConnector,
						Property.JCR_TITLE);
				String url = AkbJcrUtils.get(activeConnector,
						AkbNames.AKB_CONNECTOR_URL);
				String msg = " to " + name + " (" + url + ")";
				if (testSuccesfull)
					MessageDialog.openInformation(
							getDisplay().getActiveShell(), "Test successful",
							"Successfully connected " + msg);
				else
					MessageDialog.openError(getDisplay().getActiveShell(),
							"Test failure", "Unable to connect" + msg);
			}
		});

		removeBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CommandUtils.CallCommandWithOneParameter(DeleteAkbNodes.ID,
						DeleteAkbNodes.PARAM_NODE_JCR_ID,
						AkbJcrUtils.getIdentifierQuietly(connectorAlias));
				// for (IFormPart part : form.getParts())
				// if (!formPart.equals(part))
				// part.refresh();
			}
		});
		form.addPart(formPart);
	}

	@Override
	public boolean setFocus() {
		return titleTxt.setFocus();
	}

	protected void disposePart(AbstractFormPart part) {
		if (part != null) {
			form.removePart(part);
			part.dispose();
		}
	}

	@Override
	public void dispose() {
		disposePart(formPart);
		super.dispose();
	}
}
