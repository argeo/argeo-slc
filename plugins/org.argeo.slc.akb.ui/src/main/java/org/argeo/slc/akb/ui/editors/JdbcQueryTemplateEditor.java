package org.argeo.slc.akb.ui.editors;

import org.argeo.slc.akb.AkbNames;
import org.argeo.slc.akb.ui.AkbUiPlugin;
import org.argeo.slc.akb.ui.AkbUiUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;

/**
 * Display and edit a jdbc query
 */
public class JdbcQueryTemplateEditor extends AkbItemTemplateEditor {

	public final static String ID = AkbUiPlugin.PLUGIN_ID
			+ ".jdbcQueryTemplateEditor";

	@Override
	protected String getEditorId() {
		return ID;
	}

	@Override
	protected void populateBottomPart(Composite parent, IManagedForm managedForm) {
		parent.setLayout(AkbUiUtils.gridLayoutNoBorder());
		Group group = new Group(parent, SWT.NO_FOCUS);
		getToolkit().adapt(group, false, false);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		group.setLayout(new GridLayout(1, false));

		// first line: Description
		getToolkit().createLabel(group, "Enter below a valid JDBC Query");

		// 2nd line: the query
		final Text queryTxt = getToolkit().createText(group, "",
				SWT.BORDER | SWT.MULTI | SWT.WRAP);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		queryTxt.setLayoutData(gd);

		// Part Management
		final AbstractFormPart part = new AbstractFormPart() {
			public void refresh() {
				super.refresh();
				// update display value
				AkbUiUtils.refreshFormTextWidget(queryTxt, getAkbNode(),
						AkbNames.AKB_QUERY_TEXT);
			}
		};
		// Listeners
		AkbUiUtils.addTextModifyListener(queryTxt, getAkbNode(),
				AkbNames.AKB_QUERY_TEXT, part);
		managedForm.addPart(part);
	}

}
