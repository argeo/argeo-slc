package org.argeo.slc.client.ui.dist.controllers;

import org.argeo.eclipse.ui.jcr.JcrImages;
import org.argeo.slc.client.ui.dist.DistImages;
import org.argeo.slc.client.ui.dist.model.DistParentElem;
import org.argeo.slc.client.ui.dist.model.ModularDistVersionBaseElem;
import org.argeo.slc.client.ui.dist.model.ModularDistVersionElem;
import org.argeo.slc.client.ui.dist.model.RepoElem;
import org.argeo.slc.client.ui.dist.model.WkspGroupElem;
import org.argeo.slc.client.ui.dist.model.WorkspaceElem;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Manages icons and labels for the Distributions tree browser
 */
public class DistTreeLabelProvider extends ColumnLabelProvider {
	@Override
	public String getText(Object element) {
		if (element instanceof DistParentElem)
			return ((DistParentElem) element).getName();
		else
			return element.toString();
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof RepoElem) {
			RepoElem re = ((RepoElem) element);
			if (re.inHome())
				return DistImages.IMG_HOME_REPO;
			else if (re.isConnected())
				return JcrImages.REPOSITORY_CONNECTED;
			else
				return JcrImages.REPOSITORY_DISCONNECTED;
		} else if (element instanceof WorkspaceElem) {
			return JcrImages.WORKSPACE_CONNECTED;
		} else if (element instanceof WkspGroupElem)
			return DistImages.IMG_WKSP_GROUP;
		// else if (element instanceof GroupBaseElem)
		// return DistImages.IMG_GROUP_BASE;
		else if (element instanceof ModularDistVersionBaseElem)
			return DistImages.IMG_MODULAR_DIST_BASE;
		else if (element instanceof ModularDistVersionElem)
			return DistImages.IMG_MODULAR_DIST_VERSION;
		return super.getImage(element);
	}
}