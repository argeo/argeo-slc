package org.argeo.slc.client.ui.dist;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class DistributionPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);

		IFolderLayout main = layout.createFolder("main", IPageLayout.LEFT,
				0.5f, editorArea);
		//main.addView("org.argeo.slc.client.ui.dist.distributionView");
		main.addView("org.argeo.slc.client.ui.dist.modulesView");

	}

}
