package org.argeo.slc.akb.ui.editors;

import org.argeo.slc.akb.AkbNames;
import org.argeo.slc.akb.ui.AkbUiPlugin;
import org.argeo.slc.akb.ui.AkbUiUtils;
import org.argeo.slc.akb.ui.composites.ActiveItemHeaderComposite;
import org.argeo.slc.akb.ui.utils.Refreshable;
import org.argeo.slc.akb.utils.AkbJcrUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;

/**
 * Display and edit a connection to a file on a distant accessible by SSH server
 */
public class SshFileTemplateEditor extends AkbItemTemplateEditor implements
		Refreshable {

	public final static String ID = AkbUiPlugin.PLUGIN_ID
			+ ".sshFileTemplateEditor";

	private Text outputDisplay;

	@Override
	protected String getEditorId() {
		return ID;
	}

	@Override
	protected void populateTestPage(Composite parent, IManagedForm managedForm) {
		parent.setLayout(AkbUiUtils.gridLayoutNoBorder());

		ActiveItemHeaderComposite header = new ActiveItemHeaderComposite(
				parent, SWT.NONE, getToolkit(), managedForm, getEnvNode(),
				getAkbNode(), getAkbService());
		header.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		outputDisplay = getToolkit().createText(parent, "", SWT.MULTI);
		outputDisplay.setFont(new Font(parent.getDisplay(), "Monospaced", 10,
				SWT.NONE));
		outputDisplay.setEditable(false);
		outputDisplay
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		forceRefresh(null);
	}

	public void forceRefresh(Object object) {
		String output = getAkbService()
				.retrieveFile(getEnvNode(), getAkbNode());
		if (AkbJcrUtils.checkNotEmptyString(output))
			outputDisplay.setText(output);
		else 
			outputDisplay.setText("");
	}

	@Override
	protected void populateBottomPart(Composite parent, IManagedForm managedForm) {
		parent.setLayout(AkbUiUtils.gridLayoutNoBorder());
		Group group = new Group(parent, SWT.NO_FOCUS);
		getToolkit().adapt(group, false, false);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		group.setLayout(new GridLayout(1, false));

		// first line: Description
		getToolkit().createLabel(group,
				"Enter below a valid path in the target server");

		// 2nd line: the path
		final Text pathTxt = getToolkit().createText(group, "",
				SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		pathTxt.setLayoutData(gd);

		// Part Management
		final AbstractFormPart part = new AbstractFormPart() {
			public void refresh() {
				super.refresh();
				// update display value
				AkbUiUtils.refreshFormTextWidget(pathTxt, getAkbNode(),
						AkbNames.AKB_FILE_PATH);
			}
		};
		// Listeners
		AkbUiUtils.addTextModifyListener(pathTxt, getAkbNode(),
				AkbNames.AKB_FILE_PATH, part);
		managedForm.addPart(part);
	}
}