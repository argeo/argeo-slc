package org.argeo.slc.client.ui.views;

import java.util.ArrayList;

import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.process.SlcExecution;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

/**
 * This class display the list of all processes that have run in the
 * corresponding agent. Currently, the local agent.
 * 
 * @author bsinou
 * 
 */
public class ProcessListView extends ViewPart {
	// private final static Log log = LogFactory.getLog(ProcessListView.class);

	public static final String ID = "org.argeo.slc.client.ui.processListView";

	private TableViewer viewer;

	// IoC
	private SlcExecutionDao slcExecutionDao;
	private ITableLabelProvider tableLabelProvider;
	private IStructuredContentProvider structuredContentProvider;

	public void createPartControl(Composite parent) {
		Table table = createTable(parent);
		viewer = new TableViewer(table);
		viewer.setLabelProvider(tableLabelProvider);
		viewer.setContentProvider(structuredContentProvider);
		viewer.setInput(getViewSite());
		viewer.addDoubleClickListener(new ViewDoubleClickListener());

	}

	protected Table createTable(Composite parent) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		Table table = new Table(parent, style);

//		GridData gridData = new GridData(GridData.FILL_BOTH);
//		gridData.grabExcessVerticalSpace = true;
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.horizontalSpan = 3;
//		table.setLayoutData(gridData);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("Date");
		column.setWidth(200);

		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Host");
		column.setWidth(100);

		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText("Id");
		column.setWidth(300);

		column = new TableColumn(table, SWT.LEFT, 3);
		column.setText("Status");
		column.setWidth(100);

		return table;
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void retrieveResults() {
		viewer.setInput(slcExecutionDao.listSlcExecutions());
	}

	// Handle Events
	class ViewDoubleClickListener implements IDoubleClickListener {
		public void doubleClick(DoubleClickEvent evt) {
			Object obj = ((IStructuredSelection) evt.getSelection())
					.getFirstElement();

			if (obj instanceof SlcExecution) {
				SlcExecution se = (SlcExecution) obj;

				IWorkbench iw = ClientUiPlugin.getDefault().getWorkbench();
				IHandlerService handlerService = (IHandlerService) iw
						.getService(IHandlerService.class);
				try {
					// get the command from plugin.xml
					IWorkbenchWindow window = iw.getActiveWorkbenchWindow();
					ICommandService cmdService = (ICommandService) window
							.getService(ICommandService.class);
					Command cmd = cmdService
							.getCommand("org.argeo.slc.client.ui.displayProcessDetails");

					ArrayList<Parameterization> parameters = new ArrayList<Parameterization>();

					// get the parameter
					IParameter iparam = cmd
							.getParameter("org.argeo.slc.client.commands.processUuid");
					Parameterization params = new Parameterization(iparam, se
							.getUuid()); // "testUUID");//
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

	// IoC
	public void setSlcExecutionDao(SlcExecutionDao slcExecutionDao) {
		this.slcExecutionDao = slcExecutionDao;
	}

	public void setTableLabelProvider(ITableLabelProvider tableLabelProvider) {
		this.tableLabelProvider = tableLabelProvider;
	}

	public void setStructuredContentProvider(
			IStructuredContentProvider structuredContentProvider) {
		this.structuredContentProvider = structuredContentProvider;
	}

}