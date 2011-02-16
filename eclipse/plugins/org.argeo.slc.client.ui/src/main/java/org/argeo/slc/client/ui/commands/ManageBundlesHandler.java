package org.argeo.slc.client.ui.commands;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.client.ui.ClientUiPlugin;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Command handler to manage existing bundles; add or remove specific ones.
 * 
 * @author bsinou
 * 
 */

public class ManageBundlesHandler extends AbstractHandler {
	private static final Log log = LogFactory
			.getLog(ManageBundlesHandler.class);

	// private static final String DEFAULT_BUNDLE_DIR = "tmp";

	public Object execute(ExecutionEvent event) throws ExecutionException {

		Shell shell = ClientUiPlugin.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getShell();
		// LaunchConfigurationsDialog lcd;
		// see http://kickjava.com/src/org/eclipse/debug/internal/ui/launchConfigurations/LaunchConfigurationsDialog.java.htm
		// to have more ideas about what to do.
		
		TitleAreaDialog tad = new TitleAreaDialog(shell);
		tad.setTitle("Manage Bundles");
		tad.open();

		
		// RCP Specific, commented for now.
		// DirectoryDialog dialog2 = new DirectoryDialog(shell);
		// String path = dialog2.open();
		//
		// if (path == null)
		// // action canceled by user
		// return null;
		// log.debug("Path chosen by user : " + path);
		
		// the following does not work : it doesn't display anything.
		// Label label = new Label(shell, SWT.WRAP);
		// label.setText("This is a long text string that will wrap when the dialog is resized.");
		// List list = new List(shell, SWT.BORDER | SWT.H_SCROLL |
		// SWT.V_SCROLL);
		// list.setItems(new String[] { "Item 1", "Item 2" });
		// Button button1 = new Button(shell, SWT.PUSH);
		// button1.setText("OK");
		// Button button2 = new Button(shell, SWT.PUSH);
		// button2.setText("Cancel");
		//
		// final int insetX = 4, insetY = 4;
		// FormLayout formLayout = new FormLayout();
		// formLayout.marginWidth = insetX;
		// formLayout.marginHeight = insetY;
		// shell.setLayout(formLayout);
		//
		// Point size = label.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		// final FormData labelData = new FormData(size.x, SWT.DEFAULT);
		// labelData.left = new FormAttachment(0, 0);
		// labelData.right = new FormAttachment(100, 0);
		// label.setLayoutData(labelData);
		//
		// FormData button2Data = new FormData();
		// button2Data.right = new FormAttachment(100, -insetX);
		// button2Data.bottom = new FormAttachment(100, 0);
		// button2.setLayoutData(button2Data);
		//
		// FormData button1Data = new FormData();
		// button1Data.right = new FormAttachment(button2, -insetX);
		// button1Data.bottom = new FormAttachment(100, 0);
		// button1.setLayoutData(button1Data);
		//
		// FormData listData = new FormData();
		// listData.left = new FormAttachment(0, 0);
		// listData.right = new FormAttachment(100, 0);
		// listData.top = new FormAttachment(label, insetY);
		// listData.bottom = new FormAttachment(button2, -insetY);
		// list.setLayoutData(listData);
		//
		// shell.pack();
		// shell.open();
		//
		// Display display = shell.getDisplay();
		// while (!shell.isDisposed()) {
		// if (!display.readAndDispatch())
		// display.sleep();
		// }
		// display.dispose();

		return null;
	}
	
}
