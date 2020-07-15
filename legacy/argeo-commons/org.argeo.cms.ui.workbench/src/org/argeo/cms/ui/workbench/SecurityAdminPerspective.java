package org.argeo.cms.ui.workbench;

import org.argeo.cms.ui.workbench.internal.useradmin.parts.GroupsView;
import org.argeo.cms.ui.workbench.internal.useradmin.parts.UsersView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/** Default perspective to manage users and groups */
public class SecurityAdminPerspective implements IPerspectiveFactory {
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);

		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.3f, editorArea);
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.5f, editorArea);
		left.addView(UsersView.ID);
		right.addView(GroupsView.ID);
	}
}
