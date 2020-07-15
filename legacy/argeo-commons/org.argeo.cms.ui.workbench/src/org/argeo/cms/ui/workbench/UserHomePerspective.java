package org.argeo.cms.ui.workbench;

import org.argeo.cms.ui.workbench.jcr.NodeFsBrowserView;
import org.argeo.cms.ui.workbench.useradmin.UserProfile;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/** Home perspective for the current user */
public class UserHomePerspective implements IPerspectiveFactory {
	public final static String ID = WorkbenchUiPlugin.PLUGIN_ID + ".userHomePerspective";

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);

		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.40f, editorArea);
		left.addView(NodeFsBrowserView.ID);
		left.addView(UserProfile.ID);
		// left.addView(LogView.ID);
	}
}
