package org.argeo.slc.client.ui.providers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.client.ui.views.ProcessBuilderView;
import org.argeo.slc.client.ui.views.ProcessParametersView;
import org.argeo.slc.core.execution.PrimitiveAccessor;
import org.argeo.slc.core.execution.PrimitiveUtils;
import org.argeo.slc.core.execution.RefSpecAttribute;
import org.argeo.slc.core.execution.RefValue;
import org.argeo.slc.core.execution.RefValueChoice;
import org.argeo.slc.execution.ExecutionSpec;
import org.argeo.slc.execution.ExecutionSpecAttribute;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

/**
 * 
 * 
 * Implements the ability to edit and save various type of parameter of a given
 * process. Parameter values are directly saved as soon as the focus on a given
 * field is lost.
 * 
 * 
 * Note that EditingSupport is tightly coupled with both ProcessParametersView
 * and ProcessBuilderView; it cannot serve as a generic EditingSupport as is.
 * Note also that it assumes that processes in the ProcessBuilderView are stored
 * as an ordered list.
 * 
 * @author bsinou
 * 
 */

public class ProcessParametersEditingSupport extends EditingSupport {

	private final static Log log = LogFactory
			.getLog(ProcessParametersEditingSupport.class);

	private CellEditor strEditor;
	// private ComboBoxCellEditor comboEditor;
	// private int column;

	// So that we can update corresponding process
	private int curProcessIndex;
	private ProcessBuilderView pbView;

	// To populate drop down lists
	private ExecutionSpec executionSpec;

	// To persist combo box elements indexes
	Map<String, List<String>> comboBoxes = new HashMap<String, List<String>>();

	public ProcessParametersEditingSupport(ColumnViewer viewer, int column) {
		super(viewer);
		strEditor = new TextCellEditor(((TableViewer) viewer).getTable());
		// TODO : add cell validators.
	}

	@Override
	protected CellEditor getCellEditor(Object element) {

		// TODO : check if all parameter always have a linked attribute.
		if (element instanceof ProcessParametersView.ObjectWithName) {
			ProcessParametersView.ObjectWithName own = (ProcessParametersView.ObjectWithName) element;
			ExecutionSpecAttribute esa = executionSpec.getAttributes().get(
					own.name);
			if (esa != null) {
				// Paramater is a primitive with no list of choices linked by
				// the specs
				if (own.obj instanceof PrimitiveAccessor
						&& !(esa instanceof RefSpecAttribute))
					return strEditor;
				else if (esa instanceof RefSpecAttribute) {
					RefSpecAttribute rsa = (RefSpecAttribute) esa;

					Iterator<RefValueChoice> choices = rsa.getChoices()
							.iterator();
					List<String> items = new ArrayList<String>();
					while (choices.hasNext()) {
						items.add(choices.next().getName());
					}

					// persists order of the elements in the current combo box
					comboBoxes.put(own.name, items);

					String[] itemsStr = new String[items.size()];
					ComboBoxCellEditor comboEditor = new ComboBoxCellEditor(
							((TableViewer) getViewer()).getTable(),
							items.toArray(itemsStr));
					return comboEditor;
				}
			}
		}
		if (log.isTraceEnabled()) {
			log.warn(" No cell Editor fits for element : " + element.toString()
					+ " of class : " + element.getClass().getName());
		}
		return null;
	}

	@Override
	protected boolean canEdit(Object element) {
		if (element instanceof ProcessParametersView.ObjectWithName) {
			ProcessParametersView.ObjectWithName own = (ProcessParametersView.ObjectWithName) element;
			ExecutionSpecAttribute esa = executionSpec.getAttributes().get(
					own.name);
			if (esa != null && !esa.getIsFrozen())
				return true;
		}
		return false;
	}

	@Override
	protected Object getValue(Object element) {
		ProcessParametersView.ObjectWithName own = (ProcessParametersView.ObjectWithName) element;

		if (own.obj instanceof PrimitiveAccessor) {
			PrimitiveAccessor pv = (PrimitiveAccessor) own.obj;
			return pv.getValue().toString();
		} else if (own.obj instanceof RefValue) {
			RefValue rv = (RefValue) own.obj;
			List<String> values = comboBoxes.get(own.name);
			log.debug("Get Value : " + rv.getRef() + " & index : "
					+ values.indexOf(rv.getRef()));
			return values.indexOf(rv.getRef());
		} else
			return "unsupported param type";
	}

	@Override
	protected void setValue(Object element, Object value) {
		ProcessParametersView.ObjectWithName own = (ProcessParametersView.ObjectWithName) element;
		if (own.obj instanceof PrimitiveAccessor) {
			PrimitiveAccessor pv = (PrimitiveAccessor) own.obj;
			if (PrimitiveUtils.typeAsClass(pv.getType()) != null)
				pv.setValue(value);
			pbView.updateParameter(curProcessIndex, own.name, own.obj);
			getViewer().update(element, null);
		} else if (own.obj instanceof RefValue) {
			RefValue rv = (RefValue) own.obj;
			List<String> values = comboBoxes.get(own.name);
			rv.setRef(values.get(((Integer) value).intValue()));
			getViewer().update(element, null);
		}

	}

	// Store the index of the process which parameters are being edited
	public void setCurrentProcessIndex(int index) {
		this.curProcessIndex = index;
	}

	public void setCurrentExecutionSpec(ExecutionSpec executionSpec) {
		this.executionSpec = executionSpec;
	}

	public void setCurrentProcessBuilderView(
			ProcessBuilderView processbuilderView) {
		this.pbView = processbuilderView;
	}

}
