package org.argeo.slc.client.ui.dist.wizards;

import javax.jcr.security.Privilege;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ChooseRightsPage extends WizardPage implements ModifyListener {
	private static final long serialVersionUID = 3016024222014878781L;

	// This page widget
	private Text groupNameTxt;
	private Combo authorizationCmb;

	// Define acceptable chars for the technical name
	// private static Pattern p = Pattern.compile("^[A-Za-z0-9]+$");

	// USABLE SHORTCUTS
	protected final static String[] validAuthType = { Privilege.JCR_READ,
			Privilege.JCR_WRITE, Privilege.JCR_ALL };

	public ChooseRightsPage() {
		super("Main");
		setTitle("Manage authorizations on the current workspace");
	}

	public void createControl(Composite parent) {
		// specify subject
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		Label lbl = new Label(composite, SWT.LEAD);
		lbl.setText("Group or user name (no blank, no special chars)");
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		groupNameTxt = new Text(composite, SWT.LEAD | SWT.BORDER);
		groupNameTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		if (groupNameTxt != null)
			groupNameTxt.addModifyListener(this);

		// Choose rigths
		new Label(composite, SWT.NONE).setText("Choose corresponding rights");
		authorizationCmb = new Combo(composite, SWT.BORDER | SWT.V_SCROLL);
		authorizationCmb.setItems(validAuthType);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		authorizationCmb.setLayoutData(gd);

		authorizationCmb.select(0);

		// Compulsory
		setControl(composite);
	}

	protected String getGroupName() {
		return groupNameTxt.getText();
	}

	protected String getAuthTypeStr() {
		return authorizationCmb.getItem(authorizationCmb.getSelectionIndex());
	}

	// private static boolean match(String s) {
	// return p.matcher(s).matches();
	// }

	public void modifyText(ModifyEvent event) {
		String message = checkComplete();
		if (message != null)
			setMessage(message, WizardPage.ERROR);
		else {
			setMessage("Complete", WizardPage.INFORMATION);
			setPageComplete(true);
		}
	}

	/** @return error message or null if complete */
	protected String checkComplete() {
		String groupStr = groupNameTxt.getText();
		if (groupStr == null || "".equals(groupStr))
			return "Please enter the name of the corresponding group.";
		// Remove regexp check for the time being.
		// else if (!match(groupStr))
		// return
		// "Please use only alphanumerical chars for the short technical name.";
		return null;
	}
}
