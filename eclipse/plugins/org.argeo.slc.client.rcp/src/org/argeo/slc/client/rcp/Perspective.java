package org.argeo.slc.client.rcp;

import org.argeo.slc.client.ui.views.ExecutionModulesView;
import org.argeo.slc.client.ui.views.ProcessBuilderView;
import org.argeo.slc.client.ui.views.ResultListView;
import org.argeo.slc.client.ui.views.ProcessListView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);

		IFolderLayout topLeft = layout.createFolder("topLeft",
				IPageLayout.LEFT, 0.5f, editorArea);
		topLeft.addView(ExecutionModulesView.ID);

		IFolderLayout topRight = layout.createFolder("topRight",
				IPageLayout.RIGHT, 0.5f, editorArea);
		topRight.addView(ResultListView.ID);

		IFolderLayout bottomRight = layout.createFolder("bottomRight",
				IPageLayout.BOTTOM, 0.6f, "topRight");
		bottomRight.addView(ProcessListView.ID);

		IFolderLayout bottomLeft = layout.createFolder("bottomLeft",
				IPageLayout.BOTTOM, 0.6f, "topLeft");
		bottomLeft.addView(ProcessBuilderView.ID);
		// layout.addStandaloneView(SlcExecutionListView.ID, false,
		// IPageLayout.BOTTOM, 0.5f, editorArea);
	}

}
