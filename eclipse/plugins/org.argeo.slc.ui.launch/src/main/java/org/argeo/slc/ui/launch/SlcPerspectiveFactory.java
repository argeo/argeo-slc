package org.argeo.slc.ui.launch;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

public class SlcPerspectiveFactory implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		// Get the editor area.
		String editorArea = layout.getEditorArea();

		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f,
			editorArea);
		topLeft.addView(IPageLayout.ID_RES_NAV);

		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.66f,
				editorArea);
		bottom.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		bottom.addView(IPageLayout.ID_TASK_LIST);
		
		layout.addActionSet("org.eclipse.debug.ui.launchActionSet");
	}

}
