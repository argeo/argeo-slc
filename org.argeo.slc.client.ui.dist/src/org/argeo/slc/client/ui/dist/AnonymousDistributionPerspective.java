package org.argeo.slc.client.ui.dist;

import org.argeo.slc.client.ui.dist.views.AnonymousDistributionsView;
import org.argeo.slc.client.ui.dist.views.HelpView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/** Simple default perspective that presents public repositories */
public class AnonymousDistributionPerspective implements IPerspectiveFactory {

	public final static String ID = DistPlugin.PLUGIN_ID
			+ ".anonymousDistributionPerspective";

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);

		IFolderLayout main = layout.createFolder("main", IPageLayout.LEFT,
				0.3f, editorArea);
		main.addView(AnonymousDistributionsView.ID);
		main.addView(HelpView.ID);
	}
}
