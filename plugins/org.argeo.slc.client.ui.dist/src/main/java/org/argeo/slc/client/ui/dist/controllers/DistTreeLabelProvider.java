package org.argeo.slc.client.ui.dist.controllers;

import org.argeo.slc.client.ui.dist.DistImages;
import org.argeo.slc.client.ui.dist.model.DistParentElem;
import org.argeo.slc.client.ui.dist.model.WkspGroupElem;
import org.argeo.slc.client.ui.dist.model.RepoElem;
import org.argeo.slc.client.ui.dist.model.WorkspaceElem;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * Manages icons and labels for the distributions browser
 */
public class DistTreeLabelProvider extends ColumnLabelProvider {
	@Override
	public String getText(Object element) {
		if (element instanceof DistParentElem)
			return ((DistParentElem) element).getLabel();
		else
			return element.toString();
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof DistParentElem) {
			DistParentElem bElement = (DistParentElem) element;
			if (bElement instanceof RepoElem)
				if (bElement.inHome())
					return DistImages.IMG_HOME_REPO;
				else if (bElement.isReadOnly())
					return DistImages.IMG_REPO_READONLY;
				else
					return DistImages.IMG_REPO;
			else if (bElement instanceof WkspGroupElem)
				return DistImages.IMG_WKSP;
			else if (element instanceof WorkspaceElem)
				if (((WorkspaceElem) element).isReadOnly())
					return DistImages.IMG_DISTGRP_READONLY;
				else
					return DistImages.IMG_DISTGRP;
		}
		return null;
	}
}