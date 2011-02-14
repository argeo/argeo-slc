package org.argeo.slc.client.ui.providers;

import org.argeo.slc.client.ui.views.ProcessBuilderView;
import org.argeo.slc.client.ui.views.ProcessParametersView;
import org.argeo.slc.core.execution.PrimitiveAccessor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

/**
 * 
 * 
 *         Implements the ability to edit and save various type of
 *         parameter of a given process. Parameter values are directly saved as
 *         soon as the focus on a given field is lost.
 * 
 * 
 *         Note that EditingSupport is tightly coupled with both
 *         ProcessParametersView and ProcessBuilderView; it cannot serve as a
 *         generic EditingSupport as is. Note also that it assumes that the
 *         processes in ProcessBuilderView as stored as an ordered list.
 
 @author bsinou
 * 
 */

public class ProcessParametersEditingSupport extends EditingSupport {

	// private final static Log log = LogFactory
	// .getLog(ProcessParametersEditingSupport.class);

	private CellEditor strEditor;
	//private CellEditor nbEditor;
	// private int column;

	private final static String strType = "string", intType = "integer";

	// different type of primitive
	// private static enum primitiveType {
	// strType, intType
	// };

	// So that we can update corresponding process
	private int curProcessIndex;
	private ProcessBuilderView pbView;

	public ProcessParametersEditingSupport(ColumnViewer viewer, int column) {
		super(viewer);
		strEditor = new TextCellEditor(((TableViewer) viewer).getTable());
		// nbEditor = new NumberCellEditor(((TableViewer) viewer).getTable());
		// this.column = column;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		// TODO return specific editor depending on the parameter type.
		return strEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		ProcessParametersView.ObjectWithName objectWithName = (ProcessParametersView.ObjectWithName) element;

		if (objectWithName.obj instanceof PrimitiveAccessor) {
			PrimitiveAccessor pv = (PrimitiveAccessor) objectWithName.obj;
			// we only handle string & integer parameter in a first time
			if (strType.equals(pv.getType())) {
				return pv.getValue();
			}
			if (intType.equals(pv.getType())) {
				return ((Integer) pv.getValue()).toString();
			}
		}
		return "unsupported param type";

	}

	@Override
	protected void setValue(Object element, Object value) {
		ProcessParametersView.ObjectWithName objectWithName = (ProcessParametersView.ObjectWithName) element;
		if (objectWithName.obj instanceof PrimitiveAccessor) {
			PrimitiveAccessor pv = (PrimitiveAccessor) objectWithName.obj;
			// we only handle string parameter in a first time
			if (strType.equals(pv.getType())) {
				pv.setValue(value);
				pbView.updateParameter(curProcessIndex, objectWithName.name,
						objectWithName.obj);
			} else if (intType.equals(pv.getType())) {

				String stVal = (String) value;
				Integer val = ("".equals(stVal)) ? new Integer(0)
						: new Integer(stVal);
				pv.setValue(val);
				pbView.updateParameter(curProcessIndex, objectWithName.name, pv);
			}
			getViewer().update(element, null);
		}

	}

	// Store the index of the process which parameters are being edited
	public void setCurrentProcessIndex(int index) {
		this.curProcessIndex = index;
	}

	public void setCurrentProcessBuilderView(
			ProcessBuilderView processbuilderView) {
		this.pbView = processbuilderView;
	}

}
