package org.argeo.slc.client.ui.dist;

import org.argeo.slc.client.ui.dist.views.ArtifactsBrowser;
import org.argeo.slc.client.ui.dist.views.QueryArtifactsForm;
import org.argeo.slc.client.ui.dist.views.QueryArtifactsText;
import org.argeo.slc.client.ui.dist.views.QueryBundlesForm;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class DistributionPerspective implements IPerspectiveFactory {

	public final static String ID = DistPlugin.ID + ".distributionPerspective";

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);

		IFolderLayout main = layout.createFolder("main", IPageLayout.LEFT,
				0.5f, editorArea);
		main.addView(ArtifactsBrowser.ID);
		main.addView(QueryArtifactsForm.ID);
		main.addView(QueryBundlesForm.ID);
		main.addView(QueryArtifactsText.ID);
	}
}
