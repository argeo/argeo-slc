package org.argeo.slc.client.contentprovider;

import org.argeo.eclipse.ui.TreeParent;
import org.argeo.slc.client.contentprovider.ResultDetailContentProvider.ResultPartNode;
import org.argeo.slc.client.contentprovider.ResultDetailContentProvider.StatusAware;
import org.argeo.slc.client.ui.ClientUiPlugin;
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
	// private static final Log log = LogFactory
	// .getLog(ResultDetailLabelProvider.class);

	public String getColumnText(Object obj, int index) {

		if (obj instanceof TreeParent) {
			if (index == 0)
				return ((TreeParent) obj).getName();
			else
				return null;
		}

		if (obj instanceof ResultPartNode) {
			ResultPartNode rpn = (ResultPartNode) obj;
			switch (index) {
			case 0:
				return rpn.toString();
			case 1:
				return rpn.getStatus().toString();
			case 2:
				return rpn.getMessage();
			case 3:
				return rpn.getExceptionMessage();
			}
			return getText(obj);
		}
		return null;
	}

	public Image getImage(Object element) {
		if (element instanceof StatusAware) {
			if (((StatusAware) element).isPassed())
				return ClientUiPlugin.getDefault().getImageRegistry()
						.get("passedTest");
			else
				return ClientUiPlugin.getDefault().getImageRegistry()
						.get("failedTest");
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
