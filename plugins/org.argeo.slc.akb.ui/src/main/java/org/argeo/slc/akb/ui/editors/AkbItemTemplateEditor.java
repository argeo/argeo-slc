package org.argeo.slc.akb.ui.editors;

import org.argeo.slc.akb.ui.AkbUiUtils;
import org.argeo.slc.akb.ui.composites.ItemTemplateTitleComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * Parent class for most akb items editor. Factorizes UI parts that are common
 * in various item types.
 */
public abstract class AkbItemTemplateEditor extends AbstractAkbNodeEditor {

	/* CONTENT CREATION */
	@Override
	public void populateMainPage(Composite parent, IManagedForm managedForm) {
		parent.setLayout(AkbUiUtils.gridLayoutNoBorder());
		// First line main info
		ItemTemplateTitleComposite ittCmp = new ItemTemplateTitleComposite(
				parent, SWT.NO_FOCUS, getToolkit(), managedForm, getEnvNode(),
				getAkbNode(), getAkbService());
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd.minimumHeight = 250;
		ittCmp.setLayoutData(gd);

		Composite bottomCmp = getToolkit().createComposite(parent);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		bottomCmp.setLayoutData(gd);
		populateBottomPart(bottomCmp, managedForm);
	}

	@Override
	protected void addOtherPages() throws PartInitException {
		addPage(new TestPage(this, "testPage", "Test"));
	}

	/** Overwrite to add specific bottom part depending on the item type */
	abstract protected void populateBottomPart(Composite parent,
			IManagedForm managedForm);

	/**
	 * Display history
	 */
	private class TestPage extends FormPage {

		public TestPage(FormEditor editor, String id, String title) {
			super(editor, id, title);
		}

		protected void createFormContent(IManagedForm managedForm) {
			super.createFormContent(managedForm);
			ScrolledForm form = managedForm.getForm();
			form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			Composite parent = form.getBody();
			populateTestPage(parent);
		}
	}

	protected void populateTestPage(Composite parent) {
		parent.setLayout(AkbUiUtils.gridLayoutNoBorder());
		getToolkit().createLabel(
				parent,
				"This page will display a test page "
						+ "using default connection for the chosen alias");
	}

}