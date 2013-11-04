package org.argeo.slc.akb.ui;

import org.argeo.slc.akb.ui.views.AkbDefaultView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class AkbTemplatesPerspective implements IPerspectiveFactory {
	public static final String ID = AkbUiPlugin.PLUGIN_ID + ".akbTemplatesPerspective";
	
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
