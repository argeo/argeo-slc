package org.argeo.slc.akb.ui.editors;

import org.argeo.slc.akb.ui.AkbUiPlugin;
import org.argeo.slc.akb.ui.AkbUiUtils;
import org.argeo.slc.akb.ui.composites.MixTitleComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

/**
 * Display and edit a connector Alias
 */
public class AkbEnvTemplateEditor extends AbstractAkbNodeEditor {

	public final static String ID = AkbUiPlugin.PLUGIN_ID
			+ ".akbEnvTemplateEditor";

	/* CONTENT CREATION */
	@Override
	public void populateMainPage(Composite parent, IManagedForm managedForm) {
		parent.setLayout(AkbUiUtils.gridLayoutNoBorder());
		// First line main info
		MixTitleComposite mixTitleCmp = new MixTitleComposite(parent,
				SWT.NO_FOCUS, getToolkit(), managedForm, getAkbNode());
		mixTitleCmp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	@Override
	protected String getEditorId() {
		return ID;
	}
}