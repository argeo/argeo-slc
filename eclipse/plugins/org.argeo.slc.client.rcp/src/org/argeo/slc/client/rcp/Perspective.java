package org.argeo.slc.client.rcp;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(false);

		IFolderLayout topLeft = layout.createFolder("topLeft",
				IPageLayout.LEFT, 0.5f, editorArea);
		topLeft.addView("org.argeo.slc.client.ui.executionModulesView");

		IFolderLayout topRight = layout.createFolder("topRight",
				IPageLayout.RIGHT, 0.5f, editorArea);
		topRight.addView("org.argeo.slc.client.ui.resultListView");

		IFolderLayout bottomRight = layout.createFolder("bottomRight",
				IPageLayout.BOTTOM, 0.6f, "topRight");
		bottomRight.addView("org.argeo.slc.client.ui.processListView");
		// bottomRight.addView("org.argeo.slc.client.ui.processDetailView");
		bottomRight
				.addPlaceholder("org.argeo.slc.client.ui.processDetailView:UUID-*");

		// bottomRight.addView("org.argeo.slc.client.ui.resultListView");

		IFolderLayout bottomLeft = layout.createFolder("bottomLeft",
				IPageLayout.BOTTOM, 0.6f, "topLeft");
		bottomLeft.addView("org.argeo.slc.client.ui.processBuilderView");
	}

}
