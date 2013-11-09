package org.argeo.slc.akb.ui.editors;

import org.argeo.slc.akb.ui.AkbUiPlugin;
import org.argeo.slc.akb.ui.AkbUiUtils;
import org.eclipse.swt.widgets.Composite;

/**
 * Display and edit a connector Alias
 */
public class SshFileTemplateEditor extends AkbItemTemplateEditor {

	public final static String ID = AkbUiPlugin.PLUGIN_ID
			+ ".sshFileTemplateEditor";

	@Override
	protected String getEditorId() {
		return ID;
	}

	@Override
	protected void populateBottomPart(Composite parent) {
		parent.setLayout(AkbUiUtils.gridLayoutNoBorder());
		getToolkit().createLabel(parent, "Implement this");
	}
}