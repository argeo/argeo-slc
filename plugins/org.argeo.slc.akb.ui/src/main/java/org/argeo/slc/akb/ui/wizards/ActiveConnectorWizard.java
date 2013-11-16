package org.argeo.slc.akb.ui.wizards;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;

import org.argeo.eclipse.ui.ErrorFeedback;
import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbNames;
import org.argeo.slc.akb.AkbService;
import org.argeo.slc.akb.ui.AkbUiUtils;
import org.argeo.slc.akb.utils.AkbJcrUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Creates or edit a connector instance */
public class ActiveConnectorWizard extends Wizard {

	// private Session session;
	private AkbService akbService;
	private Node activeConnector;
	// private Node createdNode;

	// pages
	private EditConnectorPage editConnectorPage;

	public ActiveConnectorWizard(AkbService akbService, Node activeConnector) {
		this.akbService = akbService;
		this.activeConnector = activeConnector;
	}

	@Override
	public void addPages() {
		editConnectorPage = new EditConnectorPage();
		addPage(editConnectorPage);
	}

	@Override
	public boolean performFinish() {
		if (!canFinish())
			return false;
		doUpdate();
		return true;
	}

	private void doUpdate() {
		try {
			boolean changed = false;
			changed |= AkbUiUtils.setJcrProperty(activeConnector,
					AkbNames.AKB_CONNECTOR_URL, PropertyType.STRING,
					editConnectorPage.getUrl());
			changed |= AkbUiUtils.setJcrProperty(activeConnector,
					AkbNames.AKB_CONNECTOR_USER, PropertyType.STRING,
					editConnectorPage.getUser());
			if (changed)
				activeConnector.getSession().save();
			// return changed;
		} catch (RepositoryException re) {
			throw new AkbException("Unable to update active connector", re);
		}
	}

	private boolean doTest() {
		boolean testSuccesfull;
		try {
			testSuccesfull = akbService.testConnector(activeConnector);
		} catch (Exception e1) {
			testSuccesfull = false;
			ErrorFeedback.show("Cannot test connection", e1);
		}
		String name = AkbJcrUtils.get(activeConnector, Property.JCR_TITLE);
		String url = AkbJcrUtils.get(activeConnector,
				AkbNames.AKB_CONNECTOR_URL);
		String msg = " to " + name + " (" + url + ")";
		Shell shell = ActiveConnectorWizard.this.getShell();
		if (testSuccesfull) {
			MessageDialog.openInformation(shell, "Test successful",
					"Successfully connected " + msg);
		} else
			MessageDialog.openError(shell, "Test failure", "Unable to connect"
					+ msg);
		return testSuccesfull;
	}

	public boolean canFinish() {
		if (AkbJcrUtils.isEmptyString(editConnectorPage.getUrl())
				|| AkbJcrUtils.isEmptyString(editConnectorPage.getUser()))
			return false;
		else
			return true;
	}

	// //////////////////////
	// Pages definition
	/**
	 * Displays a combo box that enables user to choose which action to perform
	 */
	private class EditConnectorPage extends WizardPage {
		private Text urlTxt;
		private Text userTxt;

		public EditConnectorPage() {
			super("Edit connector");
			setTitle("Edit connector");
			setDescription("Edit or create an active connector");
		}

		@Override
		public void createControl(Composite parent) {
			Composite container = new Composite(parent, SWT.NO_FOCUS);
			GridLayout gl = new GridLayout(2, false);
			container.setLayout(gl);

			new Label(container, NONE).setText("URL");
			urlTxt = new Text(container, SWT.NONE);
			urlTxt.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			AkbUiUtils.refreshFormTextWidget(urlTxt, activeConnector,
					AkbNames.AKB_CONNECTOR_URL);
			urlTxt.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					// TODO implement here name validation.
					getWizard().getContainer().updateButtons();
				}
			});

			new Label(container, NONE).setText("User");
			userTxt = new Text(container, SWT.NONE);
			userTxt.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			AkbUiUtils.refreshFormTextWidget(userTxt, activeConnector,
					AkbNames.AKB_CONNECTOR_USER);
			userTxt.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					// TODO implement here name validation.
					getWizard().getContainer().updateButtons();
				}
			});

			new Label(container, SWT.SEPARATOR | SWT.SHADOW_OUT
					| SWT.HORIZONTAL).setLayoutData(new GridData(SWT.FILL,
					SWT.FILL, false, false, 2, 1));

			Link testBtn = new Link(container, SWT.NONE);
			testBtn.setText("<a>Save and test</a>");

			testBtn.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					doUpdate();
					doTest();
				}
			});

			setControl(container);
		}

		protected String getUrl() {
			return
			// String url =
			urlTxt.getText();
			// if (AkbJcrUtils.isEmptyString(url))
			// return null;
			// else
			// return url;
		}

		protected String getUser() {
			return userTxt.getText();
		}
	}
}