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

	public Image getImage(Object obj) {
		// if (obj instanceof SingleResultNode) {
		// // FIXME add realtime modification of process icon (SCHEDULED,
		// // RUNNING, COMPLETED...)
		// // Node resultNode = ((SingleResultNode) obj).getNode();
		// // int status = SlcJcrUtils.aggregateTestStatus(resultNode);
		// return SlcImages.PROCESS_COMPLETED;
		// } else if (obj instanceof ResultParent) {
		// ResultParent rParent = (ResultParent) obj;
		// if (SlcUiConstants.DEFAULT_MY_RESULTS_FOLDER_LABEL.equals(rParent
		// .getName()))
		// return SlcImages.MY_RESULTS_FOLDER;
		// else
		// return SlcImages.FOLDER;
		// } else
		return null;
	}
}
