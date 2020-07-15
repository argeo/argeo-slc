package org.argeo.cms.ui.workbench.internal.jcr.parts;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Ask end user for a name */
public class ChooseNameDialog extends TitleAreaDialog {
	private static final long serialVersionUID = 280139710002698692L;
	private Text nameTxt;

	public ChooseNameDialog(Shell parentShell) {
		super(parentShell);
		setTitle("Choose name");
	}

	protected Point getInitialSize() {
		return new Point(300, 250);
	}

	protected Control createDialogArea(Composite parent) {
		Composite dialogarea = (Composite) super.createDialogArea(parent);
		dialogarea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Composite composite = new Composite(dialogarea, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		nameTxt = createLT(composite, "Name");
		setMessage("Choose name", IMessageProvider.INFORMATION);
		parent.pack();
		nameTxt.setFocus();
		return composite;
	}

	/** Creates label and text. */
	protected Text createLT(Composite parent, String label) {
		new Label(parent, SWT.NONE).setText(label);
		Text text = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		return text;
	}

	public String getName() {
		return nameTxt.getText();
	}
}
