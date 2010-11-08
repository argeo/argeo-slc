package org.argeo.slc.client.contentprovider;

import org.argeo.slc.process.SlcExecution;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author bsinou
 * 
 *         Fill ProcessList view. Deported in an external bundle so that main
 *         slc ui bundle does not depend on DB implementation.
 */
public class ProcessListTableLabelProvider extends LabelProvider implements
		ITableLabelProvider {
	public String getColumnText(Object obj, int index) {
		// log.debug(sessionFactory.getClass().toString());

		SlcExecution se = (SlcExecution) obj;
		switch (index) {

		case 0:
			return getText(se.getStartDate());
		case 1:
			return se.getHost();
		case 2:
			return se.getUuid();
		case 3:
			return se.currentStep().getType();
		}
		return getText(obj);
	}

	public Image getColumnImage(Object obj, int index) {
		return null;
	}

}
