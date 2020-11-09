package org.argeo.slc.client.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/** Perspective to manage SLC execution flows. */
public class SlcExecutionPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);

		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT,
				0.35f, editorArea);
		left.addView(ClientUiPlugin.ID + ".jcrExecutionModulesView");
		// left.addView(ClientUiPlugin.ID + ".jcrResultListView");
		left.addView(ClientUiPlugin.ID + ".jcrResultTreeView");
		// Sleak view for SWT resource debugging
		// left.addView("org.eclipse.swt.tools.views.SleakView");

		IFolderLayout bottom = layout.createFolder("bottom",
				IPageLayout.BOTTOM, 0.65f, editorArea);
//		BOTTOM.ADDVIEW(SECURITYUIPLUGIN.PLUGIN_ID + ".LOGVIEW");
		bottom.addView(ClientUiPlugin.ID + ".jcrProcessListView");
	}
}
