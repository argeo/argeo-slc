package org.argeo.slc.client.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.client.ui.providers.ProcessParametersEditingSupport;
import org.argeo.slc.core.execution.PrimitiveAccessor;
import org.argeo.slc.execution.ExecutionSpecAttribute;
import org.argeo.slc.process.RealizedFlow;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;

/**
 * 
 * @author bsinou
 * 
 *         This view, directly linked with the <code> ProcessBuilderView </code>
 *         enables the display and editing the parameters of a given process.
 * 
 *         Note that for now we use <code>ExecutionFlowDescriptor.values</code>
 *         attribute to recall (and update ??) the various parameters.
 */
public class ProcessParametersView extends ViewPart {
	public static final String ID = "org.argeo.slc.client.ui.processParametersView";

	// class attribute
	private Map<String, ExecutionSpecAttribute> parameters;
	private RealizedFlow curRealizedFlow;

	// we should be using executionspecAttribute but for now we uses values.
	private Map<String, Object> values;

	// We must keep a reference to the current EditingSupport so that we can
	// update the index of the process being updated
	ProcessParametersEditingSupport ppEditingSupport;

	// view attributes
	private TableViewer viewer;

	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION);
		createColumns(viewer);

		// WARNING
		// for the moment being, we support only one process builder at a time 
		// we set the corresponding view in the editor here.
		ProcessBuilderView pbView = (ProcessBuilderView) ClientUiPlugin
				.getDefault().getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().findView(ProcessBuilderView.ID);
		ppEditingSupport.setCurrentProcessBuilderView(pbView);
		
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setInput(getViewSite());
		
		
	}

	// This will create the columns for the table
	private void createColumns(TableViewer viewer) {

		String[] titles = { "Attribute name", "value" };
		int[] bounds = { 200, 200 };

		for (int i = 0; i < titles.length; i++) {
			TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			column.getColumn().setText(titles[i]);
			column.getColumn().setWidth(bounds[i]);
			column.getColumn().setResizable(true);
			column.getColumn().setMoveable(true);
			if (i == 1) {
				// we create the used EditingSupport and enable editing support
				// for value Column
				ppEditingSupport = new ProcessParametersEditingSupport(viewer,
						i);
				column.setEditingSupport(ppEditingSupport);
			}
		}
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	// save and update a field when it looses the focus
	// TODO implement this method.

	// set class attributes, refresh the lists of process paramaters to edit.
	public void setRealizedFlow(int index, RealizedFlow rf) {

		// this.processIndex = index;
		ppEditingSupport.setCurrentProcessIndex(index);
		curRealizedFlow = rf;

		// TODO :
		// We should handle ExecutionSpec here. need to be improved.
		// ExecutionSpec es = rf.getExecutionSpec();
		// if (es != null && es.getAttributes() != null)
		// parameters = es.getAttributes();
		// if (parameters != null)
		// viewer.setInput(parameters);

		values = rf.getFlowDescriptor().getValues();
		if (values != null)
			viewer.setInput(values);
		else
			// No parameters to edit, we reset the view.
			viewer.setInput(null);

	}

	// Inner Classes we should use ExecutionSpecAttribute instead of values
	// see below
	protected class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		}

		public void dispose() {
		}

		@SuppressWarnings("unchecked")
		// we cast the Map<String, Object> to List<Object>
		public Object[] getElements(Object obj) {

			if (obj instanceof Map && ((Map) obj).size() != 0) {
				List<ObjectWithName> list = new ArrayList<ObjectWithName>();
				Map<String, Object> map = (Map<String, Object>) obj;
				for (String key : map.keySet()) {
					list.add(new ObjectWithName(key, map.get(key)));
				}
				return list.toArray();
			} else {
				return new Object[0];
			}
		}
	}

	protected class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		public String getColumnText(Object obj, int index) {
			// NOTE : the passed object is a line of the table !!!

			if (obj instanceof ObjectWithName) {
				ObjectWithName own = (ObjectWithName) obj;
				switch (index) {
				case 0:
					return own.name;
				case 1:
					if (own.obj instanceof PrimitiveAccessor) {
						PrimitiveAccessor pa = (PrimitiveAccessor) own.obj;
						if ("string".equals(pa.getType()))
							return (String) pa.getValue();
						else
							return "Type " + pa.getType()
									+ " not yet supported";
					} else
						return own.obj.toString();
				default:
					return getText(obj);
				}
			} else
				return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			return null;
		}

	}

	// We add an inner class to enrich the ExecutionSpecAttribute with a name
	// so that we can display it.
	public class ObjectWithName {
		public Object obj;
		public String name;

		public ObjectWithName(String name, Object obj) {
			this.name = name;
			this.obj = obj;
		}

	}

	// protected class ViewContentProvider implements IStructuredContentProvider
	// {
	// public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
	// }
	//
	// public void dispose() {
	// }
	//
	// // we cast the Map<String, ExecutionSpecAttribute> to List<Object>
	// public Object[] getElements(Object obj) {
	// if (obj instanceof Map && ((Map) obj).size() != 0) {
	// List<ExecutionSpecAttributeWithName> list = new
	// ArrayList<ExecutionSpecAttributeWithName>();
	// Map<String, ExecutionSpecAttribute> map = (Map<String,
	// ExecutionSpecAttribute>) obj;
	// for (String key : map.keySet()) {
	// list.add(new ExecutionSpecAttributeWithName(key, map
	// .get(key)));
	// }
	// return list.toArray();
	// } else {
	// return new Object[0];
	// }
	// }
	// }
	//
	// protected class ViewLabelProvider extends LabelProvider implements
	// ITableLabelProvider {
	//
	// public String getColumnText(Object obj, int index) {
	// // NOTE : the passed object is a line of the table !!!
	//
	// if (obj instanceof ExecutionSpecAttributeWithName) {
	// ExecutionSpecAttributeWithName esaw = (ExecutionSpecAttributeWithName)
	// obj;
	// switch (index) {
	// case 0:
	// return esaw.name;
	// case 1:
	// return esaw.esa.getValue().toString();
	// default:
	// return getText(obj);
	// }
	// } else
	// return getText(obj);
	// }
	//
	// public Image getColumnImage(Object obj, int index) {
	// return null;
	// }
	//
	// }
	//
	// // We add an inner class to enrich the ExecutionSpecAttribute with a name
	// // so that we can display it.
	// private class ExecutionSpecAttributeWithName {
	// public ExecutionSpecAttribute esa;
	// public String name;
	//
	// public ExecutionSpecAttributeWithName(String name,
	// ExecutionSpecAttribute esa) {
	// this.name = name;
	// this.esa = esa;
	// }

	// }
}
