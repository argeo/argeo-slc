package org.argeo.slc.client.ui.model;

import javax.jcr.RepositoryException;

import org.argeo.slc.SlcException;
import org.eclipse.jface.viewers.IElementComparer;

/**
 * Override default behaviour to insure that 2 distincts results that have the
 * same name will be correctly and distincly returned by corresponding
 * TreeViewer.getSelection() method.
 * 
 */
public class ResultItemsComparer implements IElementComparer {
	// private final static Log log =
	// LogFactory.getLog(ResultItemsComparer.class);

	public boolean equals(Object a, Object b) {
		if (b == null)
			return a == null ? true : false;

		if (a.hashCode() != b.hashCode() || !a.getClass().equals(b.getClass()))
			return false;
		else if (a instanceof SingleResultNode) {
			try {
				String ida = ((SingleResultNode) a).getNode().getIdentifier();

				String idb = ((SingleResultNode) b).getNode().getIdentifier();

				if (ida.equals(idb))
					return true;
				else
					return false;

			} catch (RepositoryException e) {
				throw new SlcException("Cannot compare single reult nodes", e);
			}
		} else
			return true;
	}

	public int hashCode(Object element) {
		return element.hashCode();
	}

}
