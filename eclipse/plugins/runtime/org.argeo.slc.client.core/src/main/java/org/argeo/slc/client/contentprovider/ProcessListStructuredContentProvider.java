package org.argeo.slc.client.contentprovider;

import java.util.List;

import org.argeo.slc.process.SlcExecution;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author bsinou
 * 
 *         Fill ProcessList view. Deported in an external bundle so that main
 *         slc ui bundle does not depend on DB implementation.
 */
public class ProcessListStructuredContentProvider implements
		IStructuredContentProvider {

	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
	}

	public void dispose() {
	}

	@SuppressWarnings("unchecked")
	public Object[] getElements(Object obj) {
		if (obj instanceof List) {
			return ((List<SlcExecution>) obj).toArray();
		} else {
			return new Object[0];
		}
	}

	public String getColumnText(Object obj, int index) {
		return null;
	}

}
