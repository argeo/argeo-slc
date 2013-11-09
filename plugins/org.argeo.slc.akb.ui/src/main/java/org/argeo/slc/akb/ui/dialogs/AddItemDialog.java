package org.argeo.slc.akb.ui.dialogs;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbTypes;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog to create a new Item Template
 */
public class AddItemDialog extends TrayDialog {

	// this page widgets and UI objects
	private final String title;

	private String[] itemTypesLbl = new String[] { "SSH File", "SSH Command",
			"JDBC Connection" };
	private String[] itemTypes = new String[] { AkbTypes.AKB_SSH_FILE,
			AkbTypes.AKB_SSH_COMMAND, AkbTypes.AKB_JDBC_QUERY };

	// business objects
	private Node parentNode;
	private Node newNode;

	// widget objects
	private Combo typeCmb;
	private Text titleTxt;

	public AddItemDialog(Shell parentShell, String title, Node parentNode) {
		super(parentShell);
		this.title = title;
		this.parentNode = parentNode;
	}

	protected Point getInitialSize() {
		return new Point(400, 300);
	}

	protected Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite) super.createDialogArea(parent);
		dialogArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		dialogArea.setLayout(new GridLayout(2, false));
		// type
		Label lbl = new Label(dialogArea, SWT.NONE);
		lbl.setText("Item type");
		typeCmb = new Combo(dialogArea, SWT.READ_ONLY);
		typeCmb.setItems(itemTypesLbl);
		// name
		titleTxt = createLT(dialogArea, "Item Name");

		parent.pack();
		return dialogArea;
	}

	@Override
	protected void okPressed() {
		try {
			newNode = parentNode.addNode(titleTxt.getText(),
					itemTypes[typeCmb.getSelectionIndex()]);
			newNode.setProperty(Property.JCR_TITLE, titleTxt.getText());
		} catch (RepositoryException e) {
			throw new AkbException("unable to create Item", e);
		}
		super.okPressed();
	}

	public Node getNewNode() {
		return newNode;
	}

	/** Creates label and text. */
	protected Text createLT(Composite parent, String label) {
		new Label(parent, SWT.NONE).setText(label);
		Text text = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		return text;
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(title);
	}
}