package org.argeo.slc.client.ui.views;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.controllers.ProcessController;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.runtime.SlcAgent;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
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
import org.eclipse.ui.part.ViewPart;

public class ProcessBuilderView extends ViewPart {
	public static final String ID = "org.argeo.slc.client.ui.processBuilderView";

	// private final static Log log =
	// LogFactory.getLog(ProcessBuilderView.class);

	private TableViewer viewer;

	private List<RealizedFlow> realizedFlows = new ArrayList<RealizedFlow>();

	private String currentAgentUuid = null;
	private ProcessController processController;

	public void createPartControl(Composite parent) {
		Table table = createTable(parent);
		viewer = new TableViewer(table);
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setContentProvider(new ViewContentProvider());
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
		column.setText("Date");
		column.setWidth(200);

		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("UUID");
		column.setWidth(300);

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

	public void setProcessController(ProcessController processController) {
		this.processController = processController;
	}

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

	protected class ViewDropListener extends ViewerDropAdapter {

		public ViewDropListener(Viewer viewer) {
			super(viewer);
		}

		@Override
		public boolean performDrop(Object data) {
			System.out.println(data);
			Properties props = new Properties();
			ByteArrayInputStream in = new ByteArrayInputStream(data.toString()
					.getBytes());
			try {
				props.load(in);
			} catch (IOException e) {
				throw new SlcException("Cannot create read realized flow", e);
			} finally {
				IOUtils.closeQuietly(in);
			}

			String agentId = props.getProperty("agentId");
			if (currentAgentUuid == null)
				currentAgentUuid = agentId;
			else if (currentAgentUuid.equals(agentId))
				return false;

			RealizedFlow rf = realizedFlowFromProperties(props);
			realizedFlows.add(rf);
			getViewer().refresh();
			return true;
		}

		private RealizedFlow realizedFlowFromProperties(Properties props) {
			RealizedFlow rf = new RealizedFlow();
			rf.setModuleName(props.getProperty("moduleName"));
			rf.setModuleVersion(props.getProperty("moduleVersion"));
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

}
