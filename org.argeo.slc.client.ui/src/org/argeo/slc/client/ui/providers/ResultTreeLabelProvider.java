package org.argeo.slc.client.ui.providers;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;

import org.argeo.eclipse.ui.TreeParent;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.SlcImages;
import org.argeo.slc.client.ui.SlcUiConstants;
import org.argeo.slc.client.ui.model.ParentNodeFolder;
import org.argeo.slc.client.ui.model.ResultParent;
import org.argeo.slc.client.ui.model.SingleResultNode;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/** Basic label provider for a tree of result */
public class ResultTreeLabelProvider extends LabelProvider {
	// private final static Log log = LogFactory
	// .getLog(ResultTreeLabelProvider.class);

	@Override
	public String getText(Object element) {
		try {

			if (element instanceof SingleResultNode) {
				Node node = ((SingleResultNode) element).getNode();
				if (node.isNodeType(NodeType.MIX_TITLE))
					return node.getProperty(Property.JCR_TITLE).getString();

			} else if (element instanceof ParentNodeFolder) {
				Node node = ((ParentNodeFolder) element).getNode();
				if (node.hasProperty(Property.JCR_TITLE))
					return node.getProperty(Property.JCR_TITLE).getString();
			}
		} catch (RepositoryException e) {
			throw new SlcException("Unexpected error while getting "
					+ "custom result label", e);
		}
		return ((TreeParent) element).getName();
	}

	public Image getImage(Object obj) {
		if (obj instanceof SingleResultNode) {
			// FIXME add realtime modification of process icon (SCHEDULED,
			// RUNNING, COMPLETED...)
			// Node resultNode = ((SingleResultNode) obj).getNode();
			// int status = SlcJcrUtils.aggregateTestStatus(resultNode);
			return SlcImages.PROCESS_COMPLETED;
		} else if (obj instanceof ResultParent) {
			ResultParent rParent = (ResultParent) obj;
			if (SlcUiConstants.DEFAULT_MY_RESULTS_FOLDER_LABEL.equals(rParent
					.getName()))
				return SlcImages.MY_RESULTS_FOLDER;
			else
				return SlcImages.FOLDER;
		} else
			return null;
	}
}
