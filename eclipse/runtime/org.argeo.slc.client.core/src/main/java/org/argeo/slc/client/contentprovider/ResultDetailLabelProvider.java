package org.argeo.slc.client.contentprovider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.eclipse.ui.TreeParent;
import org.argeo.slc.client.contentprovider.ResultDetailContentProvider.ResultPartNode;
import org.argeo.slc.client.contentprovider.ResultDetailContentProvider.StatusAware;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author bsinou
 * 
 *         Fill ResultDetail view. Deported in an external bundle so that main
 *         slc ui bundle does not depend on DB implementation.
 */
public class ResultDetailLabelProvider extends LabelProvider implements
		ITableLabelProvider {
	private static final Log log = LogFactory
			.getLog(ResultDetailLabelProvider.class);

	// TODO : find a solution to get the icons here.
	// Images
	// public final static Image FAILED = ClientUiPlugin.getImageDescriptor(
	// "icons/failed.gif").createImage();
	// public final static Image PASSED = ClientUiPlugin.getImageDescriptor(
	// "icons/passed.gif").createImage();

	public String getColumnText(Object obj, int index) {

		if (obj instanceof ResultPartNode) {
			ResultPartNode rpn = (ResultPartNode) obj;
			switch (index) {
			// case 0:
			// if (log.isDebugEnabled())
			// log.debug("Get col text, index = 0 & rpn.toString="
			// + rpn.toString());
			// return rpn.toString();
			case 0:
				return rpn.getStatus().toString();
			case 1:
				return rpn.getMessage();
			case 2:
				return rpn.getExceptionMessage();
			}
			return getText(obj);
		}

		if (obj instanceof TreeParent) {
			if (index == 0) {
				if (log.isDebugEnabled())
					log.debug("In GetTreeParent text, index = 0 & label ="
							+ ((TreeParent) obj).getName());

				return ((TreeParent) obj).getName();
			} else
				return null;
		}

		return null;
	}

	public Image getImage(Object element) {
		if (element instanceof StatusAware) {
			// Package use conflict problem when getting the icons, uncomment
			// Images definition above and update Manifest to get it.

			// if (((StatusAware) element).isPassed())
			// return PASSED;
			// else
			// return FAILED;
		}
		return null;
	}

	public Image getColumnImage(Object obj, int index) {
		if (index == 0)
			return getImage(obj);
		else
			return null;
	}
}
