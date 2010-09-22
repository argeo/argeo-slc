package org.argeo.slc.client.ui.views;

import java.util.List;

import org.argeo.slc.core.test.tree.ResultAttributes;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ProcessListContentProvider implements IStructuredContentProvider {

	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
	}

	public void dispose() {
	}

	@SuppressWarnings("unchecked")
	public Object[] getElements(Object obj) {
		if (obj instanceof List) {
			return ((List<ResultAttributes>) obj).toArray();
		} else {
			return new Object[0];
		}
	}

	public String getColumnText(Object obj, int index) {
		return null;
	}

}
