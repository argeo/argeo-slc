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
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcAgent;
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
import org.eclipse.ui.PartInitException;
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
	// private final static Log log =
	// LogFactory.getLog(ProcessBuilderView.class);

	public static final String ID = "org.argeo.slc.client.ui.processBuilderView";

	// private final static Log log =
	// LogFactory.getLog(ProcessBuilderView.class);

	private TableViewer viewer;
	private List<RealizedFlow> realizedFlows = new ArrayList<RealizedFlow>();
	private String currentAgentUuid = null;
	private String host = null;

	// TODO find a better way to get index of the current selected row
	// used in removeSelected
	private int curSelectedRow = -1;

	// IoC
	private OxmInterface oxmBean;
	private ProcessController processController;
	private List<SlcAgent> slcAgents;

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

	// clear the realizedFlow<List>
	public void clearBatch() {
		// we clear the list
		realizedFlows = new ArrayList<RealizedFlow>();
		curSelectedRow = -1;
		refreshParameterview();
		viewer.refresh();
	}

	// Remove the selected process from the batch
	public void removeSelected() {
		if (curSelectedRow == -1)
			return;
		else
			realizedFlows.remove(curSelectedRow);
		curSelectedRow = -1;
		refreshParameterview();
		viewer.refresh();
	}

	// calling this method with index =-1 will cause the reset of the view.
	private void refreshParameterview() {
		// We choose to directly access the view rather than going through
		// commands.
		ProcessParametersView ppView;
		try {
			ppView = (ProcessParametersView) ClientUiPlugin.getDefault()
					.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView(ProcessParametersView.ID);

			if (curSelectedRow == -1)
				ppView.setRealizedFlow(-1, null);
			else
				ppView.setRealizedFlow(curSelectedRow,
						realizedFlows.get(curSelectedRow));
		} catch (PartInitException e) {
			throw new SlcException(
					"Cannot Retrieve ProcessParameterView to edit parameters of selected process",
					e);
		}
	}

	// Return the list of the processes to execute.
	public void launchBatch() {
		SlcExecution slcExecution = new SlcExecution();
		slcExecution.setUuid(UUID.randomUUID().toString());

		slcExecution.setRealizedFlows(realizedFlows);
		slcExecution.setHost(host);

		// TODO : insure that the concept has been well understood & the
		// specification respected
		SlcAgent curAgent;
		for (int i = 0; i < slcAgents.size(); i++) {
			if (currentAgentUuid == null)
				throw new SlcException(
						"Cannot launch a batch if no agent is specified");
			if (currentAgentUuid.equals(slcAgents.get(i).getAgentUuid())) {
				curAgent = slcAgents.get(i);
				processController.execute(curAgent, slcExecution);
				break;
			}
		}
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
				curSelectedRow = realizedFlows.indexOf(rf);
				refreshParameterview();
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
			if (currentAgentUuid == null) {
				currentAgentUuid = agentId;
				host = props.getProperty("host");
			} else if (!currentAgentUuid.equals(agentId)) {
				// TODO: as for now, we can only construct batch on a single
				// Agent, must be upgraded to enable batch on various agent.
				throw new SlcException(
						"Cannot create batch on two (or more) distinct agents",
						null);
				// return false;
			}

			String fdXml = props.getProperty("RealizedFlowAsXml");
			if (fdXml == null)
				return false;
			RealizedFlow rf = (RealizedFlow) oxmBean.unmarshal(fdXml);
			realizedFlows.add(rf);
			curSelectedRow = realizedFlows.indexOf(rf);
			refreshParameterview();
			getViewer().refresh();
			return true;
		}

		@Override
		public boolean validateDrop(Object target, int operation,
				TransferData transferType) {
			return true;
		}
	}

	// IoC
	public void setSlcAgents(List<SlcAgent> slcAgents) {
		this.slcAgents = slcAgents;
	}

	public void setOxmBean(OxmInterface oxmBean) {
		this.oxmBean = oxmBean;
	}

	public void setProcessController(ProcessController processController) {
		this.processController = processController;
	}

}
