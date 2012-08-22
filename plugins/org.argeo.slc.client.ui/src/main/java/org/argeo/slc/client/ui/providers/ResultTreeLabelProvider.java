package org.argeo.slc.client.ui.providers;

import org.argeo.eclipse.ui.TreeParent;
import org.argeo.slc.client.ui.SlcImages;
import org.argeo.slc.client.ui.model.ResultParent;
import org.argeo.slc.client.ui.model.SingleResultNode;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/** Basic label provider for a tree of result */
public class ResultTreeLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		return ((TreeParent) element).getName();
	}

	public Image getImage(Object obj) {
		if (obj instanceof SingleResultNode) {
			// FIXME add realtime modification of process icon (SCHEDULED,
			// RUNNING, COMPLETED...)
			// Node resultNode = ((SingleResultNode) obj).getNode();
			// int status = SlcJcrUtils.aggregateTestStatus(resultNode);
			return SlcImages.PROCESS_COMPLETED;
		} else if (obj instanceof ResultParent)
			return SlcImages.FOLDER;
		else
			return null;
	}
}
