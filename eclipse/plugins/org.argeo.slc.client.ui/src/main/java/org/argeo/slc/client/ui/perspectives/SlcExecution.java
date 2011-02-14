package org.argeo.slc.client.ui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class SlcExecution implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(false);

		// Create the main ui layout

		// For a vertical split, the part on top gets the specified ratio of the
		// current space and the part on bottom gets the rest. Likewise, for a
		// horizontal split, the part at left gets the specified ratio of the
		// current space.
		IFolderLayout main = layout.createFolder("main", IPageLayout.RIGHT,
				0.3f, editorArea);
		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT,
				0.3f, editorArea);

		IFolderLayout bottom = layout.createFolder("bottom",
				IPageLayout.BOTTOM, 0.65f, "main");

		IFolderLayout topRight = layout.createFolder("topRight",
				IPageLayout.RIGHT, 0.6f, "main");

		// add the views to the corresponding place holder
		left.addView("org.argeo.slc.client.ui.executionModulesView");
		left.addView("org.argeo.slc.client.ui.resultListView");

		main.addView("org.argeo.slc.client.ui.processBuilderView");
		main.addPlaceholder("org.argeo.slc.client.ui.resultDetailView:UUID-*");
		// main.addView("org.argeo.slc.client.ui.resultExcelView");
		// main.addPlaceholder("org.argeo.slc.client.ui.resultExcelView:UUID-*");
		main.addPlaceholder("org.argeo.slc.client.ui.processDetailView:UUID-*");

		bottom.addView("org.argeo.slc.client.ui.processListView");

		topRight.addView("org.argeo.slc.client.ui.processParametersView");
	}

}
