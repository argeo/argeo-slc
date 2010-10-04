package org.argeo.slc.client.ui.views;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.client.ui.controllers.ProcessController;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.process.RealizedFlow;
import org.argeo.slc.process.SlcExecution;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class ExecutionModulesView extends ViewPart {
	private final static Log log = LogFactory
			.getLog(ExecutionModulesView.class);

	public static final String ID = "org.argeo.slc.client.ui.executionModulesView";

	private TreeViewer viewer;

	private IContentProvider contentProvider;

	private ProcessController processController;

	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setInput(getViewSite());
		viewer.addDoubleClickListener(new ViewDoubleClickListener());
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] tt = new Transfer[] { TextTransfer.getInstance() };
		viewer.addDragSupport(operations, tt, new ViewDragListener());
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	public void setContentProvider(IContentProvider contentProvider) {
		this.contentProvider = contentProvider;
	}

	public void setProcessController(ProcessController processController) {
		this.processController = processController;
	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			if (obj instanceof ExecutionModulesContentProvider.ExecutionModuleNode) {
				ExecutionModuleDescriptor emd = ((ExecutionModulesContentProvider.ExecutionModuleNode) obj)
						.getDescriptor();
				if (emd.getLabel() != null)
					return emd.getLabel();
				else
					return getText(emd);
			} else
				return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		public Image getImage(Object obj) {
			if (obj instanceof ExecutionModulesContentProvider.AgentNode)
				return ClientUiPlugin.getDefault().getImageRegistry()
						.get("agent");
			else if (obj instanceof ExecutionModulesContentProvider.ExecutionModuleNode)
				return ClientUiPlugin.getDefault().getImageRegistry()
						.get("executionModule");
			else if (obj instanceof ExecutionModulesContentProvider.FolderNode)
				return ClientUiPlugin.getDefault().getImageRegistry()
						.get("folder");
			else if (obj instanceof ExecutionModulesContentProvider.FlowNode)
				return ClientUiPlugin.getDefault().getImageRegistry()
						.get("flow");
			else
				return PlatformUI.getWorkbench().getSharedImages()
						.getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	class ViewDoubleClickListener implements IDoubleClickListener {
		public void doubleClick(DoubleClickEvent evt) {
			Object obj = ((IStructuredSelection) evt.getSelection())
					.getFirstElement();
			if (obj instanceof ExecutionModulesContentProvider.FlowNode) {
				ExecutionModulesContentProvider.FlowNode fn = (ExecutionModulesContentProvider.FlowNode) obj;

				List<RealizedFlow> realizedFlows = new ArrayList<RealizedFlow>();
				RealizedFlow realizedFlow = new RealizedFlow();
				realizedFlow.setModuleName(fn.getExecutionModuleNode()
						.getDescriptor().getName());
				realizedFlow.setModuleVersion(fn.getExecutionModuleNode()
						.getDescriptor().getVersion());
				realizedFlow.setFlowDescriptor(fn.getExecutionModuleNode()
						.getFlowDescriptors().get(fn.getFlowName()));
				realizedFlows.add(realizedFlow);

				SlcExecution slcExecution = new SlcExecution();
				slcExecution.setUuid(UUID.randomUUID().toString());
				slcExecution.setRealizedFlows(realizedFlows);
				slcExecution.setHost(fn.getExecutionModuleNode().getAgentNode()
						.getAgent().toString());
				processController.execute(fn.getExecutionModuleNode()
						.getAgentNode().getAgent(), slcExecution);
			}
		}

	}

	class ViewDragListener implements DragSourceListener {
		public void dragFinished(DragSourceEvent event) {
			System.out.println("Finished Drag");
		}

		public void dragSetData(DragSourceEvent event) {
			System.out.println("dragSetData: " + event);
			IStructuredSelection selection = (IStructuredSelection) viewer
					.getSelection();
			if (selection.getFirstElement() instanceof ExecutionModulesContentProvider.FlowNode) {
				if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
					ExecutionModulesContentProvider.FlowNode flowNode = (ExecutionModulesContentProvider.FlowNode) selection
							.getFirstElement();
					RealizedFlow rf = nodeAsRealizedFlow(flowNode);
					Properties props = new Properties();
					realizedFlowAsProperties(props, rf);
					props.setProperty("agentId", flowNode
							.getExecutionModuleNode().getAgentNode().getAgent()
							.getAgentUuid());

					ByteArrayOutputStream out = new ByteArrayOutputStream();
					try {
						props.store(out, "");
						event.data = new String(out.toByteArray());
					} catch (IOException e) {
						throw new SlcException("Cannot transfor realized flow",
								e);
					} finally {
						IOUtils.closeQuietly(out);
					}
				}
			}
		}

		public void dragStart(DragSourceEvent event) {
			System.out.println("Start Drag");
		}

		private RealizedFlow nodeAsRealizedFlow(
				ExecutionModulesContentProvider.FlowNode flowNode) {
			RealizedFlow rf = new RealizedFlow();
			rf.setModuleName(flowNode.getExecutionModuleNode().getDescriptor()
					.getName());
			rf.setModuleVersion(flowNode.getExecutionModuleNode()
					.getDescriptor().getVersion());
			ExecutionFlowDescriptor efd = new ExecutionFlowDescriptor();
			efd.setName(flowNode.getFlowName());
			rf.setFlowDescriptor(efd);
			return rf;
		}

		private void realizedFlowAsProperties(Properties props, RealizedFlow rf) {
			props.setProperty("moduleName", rf.getModuleName());
			props.setProperty("moduleVersion", rf.getModuleVersion());
			props.setProperty("flowName", rf.getFlowDescriptor().getName());
		}

	}

}