package org.argeo.slc.akb.ui.providers;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;

import org.argeo.eclipse.ui.TreeParent;
import org.argeo.slc.akb.AkbException;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/** Basic label provider for an AKB tree */
public class AkbTreeLabelProvider extends LabelProvider {
	// private final static Log log = LogFactory
	// .getLog(ResultTreeLabelProvider.class);

	AkbImageProvider imageProvider = new AkbImageProvider();

	@Override
	public String getText(Object element) {
		try {
			if (element instanceof ActiveTreeItem)
				element = ((ActiveTreeItem) element).getNode();

			if (element instanceof Node) {
				Node node = (Node) element;
				if (node.isNodeType(NodeType.MIX_TITLE))
					return node.getProperty(Property.JCR_TITLE).getString();
				else
					return node.getName();
			}
			if (element instanceof String)
				return (String) element;

		} catch (RepositoryException e) {
			throw new AkbException("Unexpected error while getting "
					+ "Custom node label", e);
		}
		return ((TreeParent) element).getName();
	}

	public Image getImage(Object element) {
		return imageProvider.getImage(element);
	}
}