package org.argeo.slc.akb.ui.editors;

import org.argeo.slc.akb.ui.AkbUiUtils;
import org.argeo.slc.akb.ui.composites.ItemTemplateTitleComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;

/**
 * Display and edit a connector Alias
 */
public abstract class AkbItemTemplateEditor extends AbstractAkbNodeEditor {

	/* CONTENT CREATION */
	@Override
	public void populateMainPage(Composite parent, IManagedForm managedForm) {
		parent.setLayout(AkbUiUtils.gridLayoutNoBorder());
		// First line main info
		ItemTemplateTitleComposite ittCmp = new ItemTemplateTitleComposite(
				parent, SWT.NO_FOCUS, getToolkit(), managedForm, getAkbNode());
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd.minimumHeight = 250;
		ittCmp.setLayoutData(gd);

		Composite bottomCmp = getToolkit().createComposite(parent);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		bottomCmp.setLayoutData(gd);
		populateBottomPart(bottomCmp);
	}

	@Override
	protected void addOtherPages() throws PartInitException {
		// TODO implement addition of the test page
	}

	/** Overwrite to add specific bottom part depending on the item type */
	abstract protected void populateBottomPart(Composite parent);
}