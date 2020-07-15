package org.argeo.cms.ui.workbench;

import org.argeo.cms.ui.workbench.useradmin.AdminLogView;
import org.argeo.cms.ui.workbench.useradmin.UserProfile;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/** First draft of a maintenance perspective. Not yet used */
public class MaintenancePerspective implements IPerspectiveFactory {
	public final static String ID = WorkbenchUiPlugin.PLUGIN_ID
			+ ".adminMaintenancePerspective";

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);

		IFolderLayout bottom = layout.createFolder("bottom",
				IPageLayout.BOTTOM, 0.50f, editorArea);
		bottom.addView(AdminLogView.ID);

		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT,
				0.30f, editorArea);
		left.addView(UserProfile.ID);
	}
}
