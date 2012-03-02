package org.argeo.slc.client.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class SlcExecutionPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);

		// Create the main ui layout

		// For a vertical split, the part on top gets the specified ratio of the
		// current space and the part on bottom gets the rest. Likewise, for a
		// horizontal split, the part at left gets the specified ratio of the
		// current space.
		// IFolderLayout main = layout.createFolder("main", IPageLayout.RIGHT,
		// 0.3f, editorArea);
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT,
				0.3f, editorArea);

		IFolderLayout bottom = layout.createFolder("bottom",
				IPageLayout.BOTTOM, 0.65f, editorArea);
		bottom.addView("org.argeo.security.ui.logView");
		bottom.addView("org.argeo.slc.client.ui.jcrProcessListView");

		// IFolderLayout topRight = layout.createFolder("topRight",
		// IPageLayout.RIGHT, 0.6f, "main");

		// add the views to the corresponding place holder
		left.addView("org.argeo.slc.client.ui.jcrExecutionModulesView");
		left.addView("org.argeo.slc.client.ui.jcrResultListView");

		// main.addView("org.argeo.slc.client.ui.processBuilderView");
		// main.addPlaceholder("org.argeo.slc.client.ui.resultDetailView:UUID-*");
		// main.addPlaceholder("org.argeo.slc.client.ui.processDetailView:UUID-*");

		// topRight.addView("org.argeo.slc.client.ui.processParametersView");
	}

}
