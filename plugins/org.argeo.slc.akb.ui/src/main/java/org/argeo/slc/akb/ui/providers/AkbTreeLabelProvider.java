package org.argeo.slc.akb.ui.providers;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;

import org.argeo.eclipse.ui.TreeParent;
import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbTypes;
import org.argeo.slc.akb.ui.AkbImages;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/** Basic label provider for an AKB tree */
public class AkbTreeLabelProvider extends LabelProvider {
	// private final static Log log = LogFactory
	// .getLog(ResultTreeLabelProvider.class);

	@Override
	public String getText(Object element) {
		try {
			if (element instanceof Node) {
				Node node = (Node) element;
				if (node.isNodeType(NodeType.MIX_TITLE))
					return node.getProperty(Property.JCR_TITLE).getString();
				else
					return node.getName();
			}
		} catch (RepositoryException e) {
			throw new AkbException("Unexpected error while getting "
					+ "Custom node label", e);
		}
		return ((TreeParent) element).getName();
	}

	public Image getImage(Object element) {
		try {
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
				else if (node.isNodeType(AkbTypes.AKB_CONNECTOR_FOLDER))
					return AkbImages.CONNECTOR_FOLDER;
			}
		} catch (RepositoryException e) {
			throw new AkbException("Unexpected error while getting "
					+ "Custom node label", e);
		}
		return null;
	}
}
