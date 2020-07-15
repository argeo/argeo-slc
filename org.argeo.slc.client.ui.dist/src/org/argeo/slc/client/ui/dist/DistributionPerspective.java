package org.argeo.slc.client.ui.dist;

import org.argeo.slc.client.ui.dist.views.DistributionsView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/** Manage a set of software repositories */
public class DistributionPerspective implements IPerspectiveFactory {

	public final static String ID = DistPlugin.PLUGIN_ID
			+ ".distributionPerspective";

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);

		IFolderLayout main = layout.createFolder("main", IPageLayout.LEFT,
				0.3f, editorArea);
		main.addView(DistributionsView.ID);
		IFolderLayout bottom = layout.createFolder("bottom",
				IPageLayout.BOTTOM, 0.75f, editorArea);
		bottom.addView("org.eclipse.ui.views.ProgressView");
	}
}
