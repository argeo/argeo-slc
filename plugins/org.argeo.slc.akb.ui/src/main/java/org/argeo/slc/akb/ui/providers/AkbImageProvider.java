package org.argeo.slc.akb.ui.providers;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbTypes;
import org.argeo.slc.akb.ui.AkbImages;
import org.eclipse.swt.graphics.Image;

public class AkbImageProvider {
	public Image getImage(Object element) {
		try {
			if (element instanceof ActiveTreeItem)
				element = ((ActiveTreeItem) element).getNode();

			if (element instanceof Node) {
				Node node = (Node) element;
				if (node.isNodeType(AkbTypes.AKB_ITEM_FOLDER))
					return AkbImages.ITEM_FOLDER;
				else if (node.isNodeType(AkbTypes.AKB_SSH_CONNECTOR))
					return AkbImages.SSH_CONNECTOR;
				else if (node.isNodeType(AkbTypes.AKB_SSH_COMMAND))
					return AkbImages.SSH_COMMAND;
				else if (node.isNodeType(AkbTypes.AKB_SSH_FILE))
					return AkbImages.SSH_FILE;
				else if (node.isNodeType(AkbTypes.AKB_JDBC_CONNECTOR))
					return AkbImages.JDBC_CONNECTOR;
				else if (node.isNodeType(AkbTypes.AKB_JDBC_QUERY))
					return AkbImages.JDBC_QUERY;
				else if (node.isNodeType(AkbTypes.AKB_ENV_TEMPLATE))
					return AkbImages.TEMPLATE;
				else if (node.isNodeType(AkbTypes.AKB_ENV))
					return AkbImages.ACTIVE_ENV;
				else if (node.isNodeType(AkbTypes.AKB_CONNECTOR_FOLDER))
					return AkbImages.CONNECTOR_FOLDER;
				else if (node.isNodeType(AkbTypes.AKB_CONNECTOR_ALIAS))
					return AkbImages.CONNECTOR_ALIAS;
				else if (node.isNodeType(AkbTypes.AKB_CONNECTOR))
					return AkbImages.DEFAULT_CONNECTOR;
			}
		} catch (RepositoryException e) {
			throw new AkbException("Unexpected error while getting "
					+ "Custom node label", e);
		}
		return null;
	}
}
