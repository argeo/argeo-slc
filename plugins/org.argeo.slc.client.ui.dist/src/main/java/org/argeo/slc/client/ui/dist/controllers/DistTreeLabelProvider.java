package org.argeo.slc.client.ui.dist.controllers;

import org.argeo.eclipse.ui.jcr.JcrImages;
import org.argeo.slc.client.ui.dist.DistImages;
import org.argeo.slc.client.ui.dist.model.DistParentElem;
import org.argeo.slc.client.ui.dist.model.GroupBaseElem;
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
			if (((WorkspaceElem) element).isConnected())
				return JcrImages.WORKSPACE_CONNECTED;
			else
				return JcrImages.WORKSPACE_DISCONNECTED;
		} else if (element instanceof WkspGroupElem)
			return JcrImages.WORKSPACE_CONNECTED;
		else if (element instanceof GroupBaseElem)
			return DistImages.IMG_GROUP_BASE;
		return super.getImage(element);
	}
}