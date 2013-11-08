package org.argeo.slc.akb.ui;

import org.argeo.slc.akb.ui.views.AkbDefaultView;
import org.argeo.slc.akb.ui.views.AkbTemplatesTreeView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class AkbEnvPerspective implements IPerspectiveFactory {
	public static final String ID = AkbUiPlugin.PLUGIN_ID + ".akbEnvPerspective";
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);

		IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT,
				0.3f, editorArea);
		left.addView(AkbDefaultView.ID);
	}
}
