package org.argeo.slc.client.ui.views;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.oxm.OxmInterface;
import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.client.ui.controllers.ProcessController;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcAgent;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
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

/**
 * Display a list of processes that are to be launched as batch. For the moment
 * being, only one agent by batch is enabled. The batch is contructed by
 * dropping process from the ExecutionModuleView. Wrong type of data dropped in
 * this view might raise errors.
 * 
 * @author bsinou
 * 
 */
public class ProcessBuilderView extends ViewPart {
	public static final String ID = "org.argeo.slc.client.ui.processBuilderView";

	// private final static Log log =
	// LogFactory.getLog(ProcessBuilderView.class);

	private TableViewer viewer;
	private List<RealizedFlow> realizedFlows = new ArrayList<RealizedFlow>();
	private String currentAgentUuid = null;

	// IoC
	private OxmInterface oxmBean;
	private ProcessController processController;

	public void createPartControl(Composite parent) {
		Table table = createTable(parent);
		viewer = new TableViewer(table);
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setContentProvider(new ViewContentProvider());
		viewer.addSelectionChangedListener(new SelectionChangedListener());

		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] tt = new Transfer[] { TextTransfer.getInstance() };
		viewer.addDropSupport(operations, tt, new ViewDropListener(viewer));

		viewer.setInput(getViewSite());
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
		column.setText("Module");
		column.setWidth(200);

		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Flow");
		column.setWidth(200);

		return table;
	}

	protected void execute() {
		// TODO: use agent proxy to retrieve it
		SlcAgent agent = null;
		SlcExecution slcExecution = new SlcExecution();
		slcExecution.setUuid(UUID.randomUUID().toString());
		slcExecution.setRealizedFlows(realizedFlows);
		processController.execute(agent, slcExecution);
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	// update one of the parameter of a given RealizedFlow
	public void updateParameter(int realizedFlowIndex, String paramName,
			Object value) {
		RealizedFlow curRealizedFlow = realizedFlows.get(realizedFlowIndex);
		curRealizedFlow.getFlowDescriptor().getValues().put(paramName, value);
	}

	// Specific Providers for the current view.
	protected class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		}

		public void dispose() {
		}

		public Object[] getElements(Object obj) {
			return realizedFlows.toArray();
		}
	}

	protected class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			RealizedFlow rf = (RealizedFlow) obj;
			switch (index) {
			case 0:
				return rf.getModuleName();
			case 1:
				return rf.getFlowDescriptor().getName();
			}
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			return null;
		}

	}

	// Handle Events
	class SelectionChangedListener implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent evt) {

			IStructuredSelection curSelection = (IStructuredSelection) evt
					.getSelection();
			Object obj = curSelection.getFirstElement();

			if (obj instanceof RealizedFlow) {
				RealizedFlow rf = (RealizedFlow) obj;

				IWorkbench iw = ClientUiPlugin.getDefault().getWorkbench();
				IHandlerService handlerService = (IHandlerService) iw
						.getService(IHandlerService.class);

				// TODO :
				// WARNING :
				// when marshalling an ExecutionFlowDescriptor, the Execution
				// Spec is set correctly,
				// but
				// when marshalling directly a realized flow, paramters are
				// stored under ExecutionFlowDescriptor.values
				String result = oxmBean.marshal(rf);

				// Passing parameters to the command
				try {
					// get the command from plugin.xml
					IWorkbenchWindow window = iw.getActiveWorkbenchWindow();
					ICommandService cmdService = (ICommandService) window
							.getService(ICommandService.class);
					Command cmd = cmdService
							.getCommand("org.argeo.slc.client.ui.editRealizedFlowDetails");

					ArrayList<Parameterization> parameters = new ArrayList<Parameterization>();

					IParameter iparam;
					Parameterization params;

					// The current index to be able to records changes on
					// parameters
					iparam = cmd
							.getParameter("org.argeo.slc.client.commands.realizedFlowIndex");
					params = new Parameterization(iparam, (new Integer(
							realizedFlows.indexOf(rf))).toString());

					parameters.add(params);

					// The current Realized flow marshalled as XML
					// See warning above
					iparam = cmd
							.getParameter("org.argeo.slc.client.commands.realizedFlowAsXml");
					params = new Parameterization(iparam, result);
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

	// Implementation of the Drop Listener
	protected class ViewDropListener extends ViewerDropAdapter {

		public ViewDropListener(Viewer viewer) {
			super(viewer);
		}

		@Override
		public boolean performDrop(Object data) {

			Properties props = new Properties();

			// TODO : Handle wrong type of dropped data
			ByteArrayInputStream in = new ByteArrayInputStream(data.toString()
					.getBytes());
			try {
				props.load(in);
			} catch (IOException e) {
				throw new SlcException("Cannot create read flow node", e);
			} finally {
				IOUtils.closeQuietly(in);
			}

			String agentId = props.getProperty("agentId");
			if (currentAgentUuid == null)
				currentAgentUuid = agentId;
			else if (!currentAgentUuid.equals(agentId)) {
				// TODO: as for now, we can only construct batch on a single
				// Agent,
				// must be upgraded to enable batch on various agent.
				throw new SlcException(
						"Cannot create batch on two (or more) distinct agents",
						null);
				// return false;
			}

			RealizedFlow rf = realizedFlowFromProperties(props);
			realizedFlows.add(rf);

			getViewer().refresh();
			return true;
		}

		private RealizedFlow realizedFlowFromProperties(Properties props) {
			RealizedFlow rf = new RealizedFlow();
			rf.setModuleName(props.getProperty("moduleName"));
			rf.setModuleVersion(props.getProperty("moduleVersion"));
			String fdXml = props.getProperty("FlowDescriptorAsXml");
			if (fdXml != null) {
				Object o = oxmBean.unmarshal(fdXml);
				if (o instanceof ExecutionFlowDescriptor) {
					rf.setFlowDescriptor((ExecutionFlowDescriptor) o);
					System.out.println("instance of EFD !!!"
							+ rf.getFlowDescriptor().toString());
					System.out.println(rf.getFlowDescriptor()
							.getExecutionSpec());
					return rf;
				}
			}
			// Else
			System.out
					.println("***** WARNING : we should not be here; corresponding flow name"
							+ props.getProperty("flowName"));
			ExecutionFlowDescriptor efd = new ExecutionFlowDescriptor();
			efd.setName(props.getProperty("flowName"));
			rf.setFlowDescriptor(efd);
			return rf;
		}

		@Override
		public boolean validateDrop(Object target, int operation,
				TransferData transferType) {
			return true;
		}
	}

	// IoC
	public void setOxmBean(OxmInterface oxmBean) {
		this.oxmBean = oxmBean;
	}

	public void setProcessController(ProcessController processController) {
		this.processController = processController;
	}

}
