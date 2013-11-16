package org.argeo.slc.akb.ui.composites;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.eclipse.ui.ErrorFeedback;
import org.argeo.eclipse.ui.utils.CommandUtils;
import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbNames;
import org.argeo.slc.akb.AkbService;
import org.argeo.slc.akb.AkbTypes;
import org.argeo.slc.akb.ui.AkbImages;
import org.argeo.slc.akb.ui.AkbUiUtils;
import org.argeo.slc.akb.ui.commands.DeleteAkbNodes;
import org.argeo.slc.akb.ui.wizards.ActiveConnectorWizard;
import org.argeo.slc.akb.utils.AkbJcrUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
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
public class AliasListItemComposite extends Composite {

	private final AkbService akbService;
	private final Node connectorAlias;
	private final Node activeEnv;
	private final boolean isActive;
	private final Node activeConnector;
	private final FormToolkit toolkit;
	private final IManagedForm form;

	// Don't forget to unregister on dispose
	private AbstractFormPart formPart;

	// To enable set focus
	private Text titleTxt;

	public AliasListItemComposite(Composite parent, int style,
			FormToolkit toolkit, IManagedForm form, Node envNode,
			String aliasPath, AkbService akbService) {
		super(parent, style);
		this.activeEnv = envNode;
		try {
			isActive = activeEnv.isNodeType(AkbTypes.AKB_ENV);
			this.activeConnector = akbService.getActiveConnectorByAlias(
					envNode, aliasPath);
			this.connectorAlias = activeEnv.getSession().getNode(aliasPath);
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
		gl.marginBottom = 5;

		parent.setLayout(gl);
		Composite firstLine = toolkit.createComposite(parent, SWT.NO_FOCUS);
		firstLine.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		firstLine.setLayout(new GridLayout(6, false));

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
		final Text titleTxt = toolkit.createText(firstLine, "", SWT.NONE);
		gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gd.widthHint = 100;
		titleTxt.setLayoutData(gd);

		// Description
		final Text descTxt = toolkit.createText(firstLine, "", SWT.NONE);
		descTxt.setLayoutData(gd);

		final Link testBtn = new Link(firstLine, SWT.NONE);
		toolkit.adapt(testBtn, false, false);
		testBtn.setText("<a>Test</a>");
		testBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				true, false));
		
		final Link editActiveConnLk = new Link(firstLine, SWT.NONE);
		toolkit.adapt(editActiveConnLk, false, false);
		// editActiveConnLk.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
		// true, false));
		
		if (!isActive) {
			final Link removeBtn = new Link(firstLine, SWT.NONE);
			toolkit.adapt(removeBtn, false, false);
			removeBtn.setText("<a>Delete</a>");
			removeBtn.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					try {
						// Manually check if corresponding node was really
						// removed
						Session session = connectorAlias.getSession();
						String absPath = connectorAlias.getPath();
						CommandUtils.CallCommandWithOneParameter(
								DeleteAkbNodes.ID,
								DeleteAkbNodes.PARAM_NODE_JCR_ID, AkbJcrUtils
										.getIdentifierQuietly(connectorAlias));

						if (!session.nodeExists(absPath))
							form.removePart(formPart);
					} catch (RepositoryException re) {
						throw new AkbException(
								"Error while removing connector Alias ", re);
					}
				}
			});
		}

		// Part Management
		formPart = new AbstractFormPart() {
			public void refresh() {
				super.refresh();
				// update display value
				AkbUiUtils.refreshFormTextWidget(titleTxt, connectorAlias,
						Property.JCR_TITLE, "Name");
				AkbUiUtils.refreshFormTextWidget(descTxt, connectorAlias,
						Property.JCR_DESCRIPTION, "Short description");

				if (isActive) {
					titleTxt.setEditable(false);
					descTxt.setEditable(false);
					editActiveConnLk.setText("<a>Edit connector instance</a>");
				} else {
					titleTxt.setEditable(true);
					descTxt.setEditable(true);
					editActiveConnLk.setText("<a>Edit default connector</a>");
				}
			}
		};
		// Listeners
		AkbUiUtils.addTextModifyListener(titleTxt, connectorAlias,
				Property.JCR_TITLE, formPart);
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

		editActiveConnLk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				ActiveConnectorWizard wizard = new ActiveConnectorWizard(
						akbService, activeConnector);
				WizardDialog dialog = new WizardDialog(getShell(), wizard);
				dialog.open();
			}
		});

		// force refresh to initialize various fields on creation
		formPart.refresh();
		form.addPart(formPart);
	}

	@Override
	public boolean setFocus() {
		if (titleTxt != null)
			return titleTxt.setFocus();
		return false;
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