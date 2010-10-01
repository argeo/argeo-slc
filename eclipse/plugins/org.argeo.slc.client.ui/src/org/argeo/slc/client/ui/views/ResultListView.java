package org.argeo.slc.client.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.core.test.tree.ResultAttributes;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

public class ResultListView extends ViewPart {
	private final static Log log = LogFactory.getLog(ResultListView.class);

	public static final String ID = "org.argeo.slc.client.ui.resultListView";

	private TableViewer viewer;

	private TreeTestResultCollectionDao testResultCollectionDao;

	public void createPartControl(Composite parent) {
		Table table = createTable(parent);
		viewer = new TableViewer(table);
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setInput(getViewSite());
		viewer.addDoubleClickListener(new ViewDoubleClickListener());
	}

	protected Table createTable(Composite parent) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		Table table = new Table(parent, style);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 3;
		table.setLayoutData(gridData);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("Date");
		column.setWidth(200);

		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("UUID");
		column.setWidth(300);

		return table;
	}

	// View Specific inner class
	protected static class ViewContentProvider implements
			IStructuredContentProvider {

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
	}

	protected class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			ResultAttributes ra = (ResultAttributes) obj;
			switch (index) {
			case 0:
				return getText(ra.getCloseDate());
			case 1:
				return ra.getUuid();
			}
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			return null;
		}

	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void retrieveResults() {
		try {
			List<ResultAttributes> lst = testResultCollectionDao
					.listResultAttributes(null);
			if (log.isTraceEnabled())
				log.trace("Result attributes count: " + lst.size());
			viewer.setInput(lst);
			// viewer.refresh();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Handle Events
	/**
	 * The ResultAttributes expose a part of the information contained in the
	 * TreeTestResult, It has the same UUID as the corresponding treeTestResult.
	 */
	class ViewDoubleClickListener implements IDoubleClickListener {
		public void doubleClick(DoubleClickEvent evt) {
			Object obj = ((IStructuredSelection) evt.getSelection())
					.getFirstElement();

			if (obj instanceof ResultAttributes) {
				ResultAttributes ra = (ResultAttributes) obj;
				log.debug("Double-clic on result with UUID" + ra.getUuid());

				IWorkbench iw = ClientUiPlugin.getDefault().getWorkbench();
				IHandlerService handlerService = (IHandlerService) iw
						.getService(IHandlerService.class);
				try {
					// get the command from plugin.xml
					IWorkbenchWindow window = iw.getActiveWorkbenchWindow();
					ICommandService cmdService = (ICommandService) window
							.getService(ICommandService.class);
					Command cmd = cmdService
							.getCommand("org.argeo.slc.client.ui.displayResultDetails");

					// log.debug("cmd : " + cmd);
					ArrayList<Parameterization> parameters = new ArrayList<Parameterization>();

					// get the parameter
					IParameter iparam = cmd
							.getParameter("org.argeo.slc.client.commands.resultUuid");

					Parameterization params = new Parameterization(iparam,
							ra.getUuid());
					parameters.add(params);

					// build the parameterized command
					ParameterizedCommand pc = new ParameterizedCommand(cmd,
							parameters.toArray(new Parameterization[parameters
									.size()]));

					// execute the command
					handlerService = (IHandlerService) window
							.getService(IHandlerService.class);
					handlerService.executeCommand(pc, null);

				} catch (Exception e) {
					e.printStackTrace();
					throw new SlcException("Problem while rendering result. "
							+ e.getMessage());
				}
			}
		}
	}

	// Ioc
	public void setTestResultCollectionDao(
			TreeTestResultCollectionDao testResultCollectionDao) {
		this.testResultCollectionDao = testResultCollectionDao;
	}

}
