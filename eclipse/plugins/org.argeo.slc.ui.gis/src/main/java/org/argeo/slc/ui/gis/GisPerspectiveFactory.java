package org.argeo.slc.ui.gis;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class GisPerspectiveFactory implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(false);

		IFolderLayout topLeft = layout.createFolder("topLeft",
				IPageLayout.LEFT, 0.5f, editorArea);
		topLeft.addView("org.argeo.slc.ui.gis.layersView");

		IFolderLayout topRight = layout.createFolder("topRight",
				IPageLayout.RIGHT, 0.5f, editorArea);
		topRight.addView("org.argeo.slc.ui.gis.mapView");
	}

}
