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
 * @author bsinou
 * 
 *         This class implements the ability to edit and save various type of
 *         parameter of a given process. Parameter values are directly saved as
 *         soon as the focus on a given field is lost.
 * 
 * 
 *         Note that this EditingSupport is tightly coupled with both
 *         ProcessParametersView and ProcessBuilderView; it cannot serve as a
 *         generic EditingSupport as is. Note also that it assumes that the
 *         processes in ProcessBuilderView as stored as an ordered list.
 */

public class ProcessParametersEditingSupport extends EditingSupport {

	private CellEditor editor;
	private int column;

	// So that we can update corresponding process
	private int curProcessIndex;
	private ProcessBuilderView pbView;

	public ProcessParametersEditingSupport(ColumnViewer viewer, int column) {
		super(viewer);
		editor = new TextCellEditor(((TableViewer) viewer).getTable());
		this.column = column;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
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
			// we only handle string parameter in a first time
			if ("string".equals(pv.getType())) {
				return pv.getValue();
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
			if ("string".equals(pv.getType())) {
				pv.setValue(value);
				pbView.updateParameter(curProcessIndex, objectWithName.name,
						objectWithName.obj);
				getViewer().update(element, null);
			}
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
