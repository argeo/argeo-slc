package org.argeo.slc.client.ui.wizards;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.client.ui.SlcUiConstants;
import org.argeo.slc.jcr.SlcJcrResultUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;

public class ConfirmOverwriteWizard extends Wizard {

	// Define widget here to simplify getters
	private Button overwriteBtn, renameBtn;
	private Text newNameTxt;
	private Label newNameLbl;

	// business object
	private String sourceNodeName;
	private Node targetParentNode;

	private String newName;
	private String parentRelPath;
	private boolean overwrite;

	public ConfirmOverwriteWizard(String sourceNodeName, Node targetParentNode) {
		setWindowTitle("Confirm overwrite or define a new name");
		this.sourceNodeName = sourceNodeName;
		this.targetParentNode = targetParentNode;
	}

	@Override
	public void addPages() {
		try {
			addPage(new MyPage());
		} catch (Exception e) {
			throw new SlcException("Cannot add page to wizard ", e);
		}
		getShell().setImage(
				ClientUiPlugin.getDefault().getWorkbench().getSharedImages()
						.getImageDescriptor(ISharedImages.IMG_LCL_LINKTO_HELP)
						.createImage());
	}

	// Expose info to the calling view
	public boolean overwrite() {
		return overwrite;
	}

	public String newName() {
		return newName;
	}

	@Override
	public boolean performFinish() {
		boolean doFinish = false;

		if (canFinish()) {
			if (overwriteBtn.getSelection())
				doFinish = MessageDialog.openConfirm(Display.getDefault()
						.getActiveShell(), "CAUTION", "All data contained in ["
						+ (parentRelPath !=null?parentRelPath:"")
						+ sourceNodeName
						+ "] are about to be definitively destroyed. \n "
						+ "Are you sure you want to proceed ?");
			else
				doFinish = true;
			// cache values
		}
		if (doFinish) {
			overwrite = overwriteBtn.getSelection();
			newName = newNameTxt.getText();
		}
		return doFinish;
	}

	class MyPage extends WizardPage implements ModifyListener {

		public MyPage() {
			super("");
			String msg = "An object with same name (" + sourceNodeName
					+ ") already exists at chosen target path";

			// Add target rel path to the message
			Session session;
			String relPath;
			try {
				session = targetParentNode.getSession();
				relPath = targetParentNode.getPath();
				String basePath = SlcJcrResultUtils
						.getMyResultsBasePath(session);
				if (relPath.startsWith(basePath))
					relPath = relPath.substring(basePath.length());
				// FIXME currently add the default base label
				parentRelPath = SlcUiConstants.DEFAULT_MY_RESULTS_FOLDER_LABEL + "/"
						+ relPath;
			} catch (RepositoryException e) {
				throw new SlcException("Unexpected error while defining "
						+ "target parent node rel path", e);
			}
			msg = msg + (parentRelPath == null ? "." : ": \n" + parentRelPath);

			// Set Title
			setTitle(msg);
		}

		public void createControl(Composite parent) {
			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new GridLayout(2, false));

			// choose between overwrite and rename
			overwriteBtn = new Button(composite, SWT.RADIO);
			overwriteBtn.setText("Overwrite");
			GridData gd = new GridData();
			gd.horizontalIndent = 30;
			gd.horizontalSpan = 2;
			overwriteBtn.setLayoutData(gd);
			overwriteBtn.setSelection(true);

			renameBtn = new Button(composite, SWT.RADIO);
			renameBtn.setText("Rename");
			renameBtn.setSelection(false);
			renameBtn.setText("Rename");
			gd = new GridData();
			gd.horizontalIndent = 30;
			gd.horizontalSpan = 2;
			renameBtn.setLayoutData(gd);

			newNameLbl = new Label(composite, SWT.LEAD);
			newNameLbl.setText("New name");
			newNameLbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
					false));
			newNameLbl.setEnabled(false);

			newNameTxt = new Text(composite, SWT.LEAD | SWT.BORDER);
			newNameTxt.setText(sourceNodeName);
			newNameTxt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
					false));
			if (newNameTxt != null)
				newNameTxt.addModifyListener(this);
			newNameTxt.setEnabled(false);

			SelectionAdapter sa = new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					updateSelection(overwriteBtn.getSelection());
				}
			};
			overwriteBtn.addSelectionListener(sa);
			renameBtn.addSelectionListener(sa);

			// Compulsory
			setControl(composite);
		}

		private void updateSelection(boolean overwrite) {
			newNameLbl.setEnabled(!overwrite);
			newNameTxt.setEnabled(!overwrite);
			if (overwrite)
				setPageComplete(true);
			else
				checkComplete();
		}

		protected String getTechName() {
			return newNameTxt.getText();
		}

		public void modifyText(ModifyEvent event) {
			checkComplete();
		}

		private void checkComplete() {
			try {

				String newName = newNameTxt.getText();
				if (newName == null || "".equals(newName.trim())) {
					setMessage("Name cannot be blank or empty",
							WizardPage.ERROR);
					setPageComplete(false);
				} else if (targetParentNode.hasNode(newName)) {
					setMessage("An object with the same name already exists.",
							WizardPage.ERROR);
					setPageComplete(false);
				} else {
					setMessage("Complete", WizardPage.INFORMATION);
					setPageComplete(true);
				}
			} catch (RepositoryException e) {
				throw new SlcException("Unexpected error while checking "
						+ "children node with same name", e);
			}
		}
	}
}
