package org.argeo.slc.client.ui.dist.editors;

import javax.jcr.Node;

import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * Enable launch of the current distribution in a separate osgi run time.
 * Display also a console to interract with the launched runtime
 */
public class RunInOsgiPage extends FormPage implements SlcNames {

	final static String PAGE_ID = "RunInOsgiPage";

	// Business Objects
	private Node modularDistribution;

	// This page widgets
	private Button launchBtn;
	private Text consoleTxt;

	private FormToolkit tk;

	public RunInOsgiPage(FormEditor formEditor, String title,
			Node modularDistribution) {
		super(formEditor, PAGE_ID, title);
		this.modularDistribution = modularDistribution;
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		tk = managedForm.getToolkit();
		// Main Layout
		Composite body = form.getBody();
		GridLayout layout = new GridLayout();
		layout.marginTop = layout.marginWidth = 0;
		body.setLayout(layout);

		// The header
		Composite header = tk.createComposite(body);
		header.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		createHeaderPart(header);

		// The console
		Composite console = tk.createComposite(body);
		console.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createConsolePart(console);
		body.layout();
	}

	private void createHeaderPart(Composite parent) {
		GridLayout layout = new GridLayout();
		parent.setLayout(layout);

		// Text Area to filter
		launchBtn = tk.createButton(parent, " Launch ", SWT.PUSH);
		launchBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));

		launchBtn.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = -1633658484882130602L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				throw new SlcException("Implement this");
			}
		});
	}

	private void createConsolePart(Composite parent) {
		parent.setLayout(new GridLayout());
		consoleTxt = tk.createText(parent, "OSGi > ", SWT.MULTI | SWT.WRAP
				| SWT.BORDER);
		consoleTxt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	@Override
	public void setFocus() {
		launchBtn.setFocus();
	}
}