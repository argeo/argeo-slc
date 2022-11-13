package org.argeo.cms.e4.users.handlers;

import java.util.Dictionary;
import java.util.Map;

import javax.inject.Inject;

import org.argeo.api.acr.ldap.LdapAttr;
import org.argeo.cms.e4.users.UserAdminWrapper;
import org.argeo.cms.runtime.DirectoryConf;
import org.argeo.cms.swt.CmsException;
import org.argeo.eclipse.ui.EclipseUiUtils;
import org.argeo.eclipse.ui.dialogs.ErrorFeedback;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.useradmin.Group;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.UserAdminEvent;

/** Create a new group */
public class NewGroup {
	// public final static String ID = WorkbenchUiPlugin.PLUGIN_ID + ".newGroup";

	/* DEPENDENCY INJECTION */
	@Inject
	private UserAdminWrapper userAdminWrapper;

	@Execute
	public Object execute() {
		NewGroupWizard newGroupWizard = new NewGroupWizard();
		newGroupWizard.setWindowTitle("Group creation");
		WizardDialog dialog = new WizardDialog(Display.getCurrent().getActiveShell(), newGroupWizard);
		dialog.open();
		return null;
	}

	private class NewGroupWizard extends Wizard {

		// Pages
		private MainGroupInfoWizardPage mainGroupInfo;

		// UI fields
		private Text dNameTxt, commonNameTxt, descriptionTxt;
		private Combo baseDnCmb;

		public NewGroupWizard() {
		}

		@Override
		public void addPages() {
			mainGroupInfo = new MainGroupInfoWizardPage();
			addPage(mainGroupInfo);
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public boolean performFinish() {
			if (!canFinish())
				return false;
			String commonName = commonNameTxt.getText();
			try {
				userAdminWrapper.beginTransactionIfNeeded();
				String dn = getDn(commonName);
				Group group = (Group) userAdminWrapper.getUserAdmin().createRole(dn, Role.GROUP);
				Dictionary props = group.getProperties();
				String descStr = descriptionTxt.getText();
				if (EclipseUiUtils.notEmpty(descStr))
					props.put(LdapAttr.description.name(), descStr);
				userAdminWrapper.commitOrNotifyTransactionStateChange();
				userAdminWrapper.notifyListeners(new UserAdminEvent(null, UserAdminEvent.ROLE_CREATED, group));
				return true;
			} catch (Exception e) {
				ErrorFeedback.show("Cannot create new group " + commonName, e);
				return false;
			}
		}

		private class MainGroupInfoWizardPage extends WizardPage implements FocusListener {
			private static final long serialVersionUID = -3150193365151601807L;

			public MainGroupInfoWizardPage() {
				super("Main");
				setTitle("General information");
				setMessage("Please choose a domain, provide a common name " + "and a free description");
			}

			@Override
			public void createControl(Composite parent) {
				Composite bodyCmp = new Composite(parent, SWT.NONE);
				setControl(bodyCmp);
				bodyCmp.setLayout(new GridLayout(2, false));

				dNameTxt = EclipseUiUtils.createGridLT(bodyCmp, "Distinguished name");
				dNameTxt.setEnabled(false);

				baseDnCmb = createGridLC(bodyCmp, "Base DN");
				// Initialise before adding the listener to avoid NPE
				initialiseDnCmb(baseDnCmb);
				baseDnCmb.addFocusListener(this);

				commonNameTxt = EclipseUiUtils.createGridLT(bodyCmp, "Common name");
				commonNameTxt.addFocusListener(this);

				Label descLbl = new Label(bodyCmp, SWT.LEAD);
				descLbl.setText("Description");
				descLbl.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
				descriptionTxt = new Text(bodyCmp, SWT.LEAD | SWT.MULTI | SWT.WRAP | SWT.BORDER);
				descriptionTxt.setLayoutData(EclipseUiUtils.fillAll());
				descriptionTxt.addFocusListener(this);

				// Initialize buttons
				setPageComplete(false);
				getContainer().updateButtons();
			}

			@Override
			public void focusLost(FocusEvent event) {
				String name = commonNameTxt.getText();
				if (EclipseUiUtils.isEmpty(name))
					dNameTxt.setText("");
				else
					dNameTxt.setText(getDn(name));

				String message = checkComplete();
				if (message != null) {
					setMessage(message, WizardPage.ERROR);
					setPageComplete(false);
				} else {
					setMessage("Complete", WizardPage.INFORMATION);
					setPageComplete(true);
				}
				getContainer().updateButtons();
			}

			@Override
			public void focusGained(FocusEvent event) {
			}

			/** @return the error message or null if complete */
			protected String checkComplete() {
				String name = commonNameTxt.getText();

				if (name.trim().equals(""))
					return "Common name must not be empty";
				Role role = userAdminWrapper.getUserAdmin().getRole(getDn(name));
				if (role != null)
					return "Group " + name + " already exists";
				return null;
			}

			@Override
			public void setVisible(boolean visible) {
				super.setVisible(visible);
				if (visible)
					if (baseDnCmb.getSelectionIndex() == -1)
						baseDnCmb.setFocus();
					else
						commonNameTxt.setFocus();
			}
		}

		private Map<String, String> getDns() {
			return userAdminWrapper.getKnownBaseDns(true);
		}

		private String getDn(String cn) {
			Map<String, String> dns = getDns();
			String bdn = baseDnCmb.getText();
			if (EclipseUiUtils.notEmpty(bdn)) {
				Dictionary<String, ?> props = DirectoryConf.uriAsProperties(dns.get(bdn));
				String dn = LdapAttr.cn.name() + "=" + cn + "," + DirectoryConf.groupBase.getValue(props) + "," + bdn;
				return dn;
			}
			return null;
		}

		private void initialiseDnCmb(Combo combo) {
			Map<String, String> dns = userAdminWrapper.getKnownBaseDns(true);
			if (dns.isEmpty())
				throw new CmsException("No writable base dn found. Cannot create group");
			combo.setItems(dns.keySet().toArray(new String[0]));
			if (dns.size() == 1)
				combo.select(0);
		}
	}

	private Combo createGridLC(Composite parent, String label) {
		Label lbl = new Label(parent, SWT.LEAD);
		lbl.setText(label);
		lbl.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		Combo combo = new Combo(parent, SWT.LEAD | SWT.BORDER | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		return combo;
	}

	/* DEPENDENCY INJECTION */
	public void setUserAdminWrapper(UserAdminWrapper userAdminWrapper) {
		this.userAdminWrapper = userAdminWrapper;
	}
}
