package org.argeo.slc.client.rcp;

import org.argeo.slc.client.ui.views.ExecutionModulesView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);

		layout.addStandaloneView(ExecutionModulesView.ID, false,
				IPageLayout.LEFT, 1.0f, editorArea);
	}

}