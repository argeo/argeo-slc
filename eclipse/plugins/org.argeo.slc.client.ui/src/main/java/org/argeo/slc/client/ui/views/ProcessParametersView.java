package org.argeo.slc.client.ui.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.client.ui.providers.ProcessParametersEditingSupport;
import org.argeo.slc.core.execution.PrimitiveAccessor;
import org.argeo.slc.core.execution.RefValue;
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
 * This view, directly linked with the <code> ProcessBuilderView </code> enables
 * the display and editing the parameters of a given process.
 * 
 * 
 * Note that for a given RealizedFlow :
 * 
 * + parameters value are set using
 * <code>RealizedFlow.ExecutionFlowDescriptor.values</code>, that might have
 * default values
 * 
 * + possible "values" for a given parameters are stored in
 * <code>RealizedFlow.ExecutionSpec.</code>
 * 
 */
public class ProcessParametersView extends ViewPart {
	private static final Log log = LogFactory
			.getLog(ProcessParametersView.class);

	public static final String ID = "org.argeo.slc.client.ui.processParametersView";

	// This map stores actual values :
	// * default values (if defined) at instantiation time
	// * values filled-in or modified by the end-user
	private Map<String, Object> values;

	// This map stores the spec of the attributes used to offer the end user
	// some choices.
	// private Map<String, ExecutionSpecAttribute> specAttributes;

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

	// set class attributes, refresh the lists of process paramaters to edit.
	public void setRealizedFlow(int index, RealizedFlow rf) {
		// force the cleaning of the view
		if (index == -1) {
			viewer.setInput(null);
			return;
		}
		// we store the index of the edited Process in the editor so that it can
		// save entries modified by the end user.
		ppEditingSupport.setCurrentProcessIndex(index);

		// We also store corresponding ExecutionSpec to be able to retrieve
		// possible values for dropdown lists
		ppEditingSupport.setCurrentExecutionSpec(rf.getExecutionSpec());
		// ExecutionSpec es = rf.getExecutionSpec();
		// if (es != null && es.getAttributes() != null)
		// parameters = es.getAttributes();
		// if (parameters != null)
		// viewer.setInput(parameters);

		values = rf.getFlowDescriptor().getValues();
		// specAttributes = rf.getFlowDescriptor().getExecutionSpec()
		// .getAttributes();

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

		@SuppressWarnings({ "unchecked", "rawtypes" })
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
						return pa.getValue().toString();
					} else if (own.obj instanceof RefValue) {
						RefValue refValue = (RefValue) own.obj;
						return refValue.getRef();
					} else {
						if (log.isTraceEnabled()) {
							log.warn("Not a Primitive accessor neither a ref Value : "
									+ own.obj.toString()
									+ " and class : "
									+ own.obj.getClass().toString());
						}
						return own.obj.toString();
					}
				default:
					return getText(obj);
				}
			} else {
				return getText(obj);
			}
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
}