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
import org.argeo.slc.client.oxm.OxmInterface;
import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.client.ui.controllers.ProcessController;
import org.argeo.slc.client.ui.providers.ExecutionModulesContentProvider;
import org.argeo.slc.client.ui.providers.ExecutionModulesContentProvider.FlowNode;
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

public class JcrExecutionModulesView extends ViewPart {
	private final static Log log = LogFactory
			.getLog(JcrExecutionModulesView.class);

	public static final String ID = "org.argeo.slc.client.ui.executionModulesView";

	private TreeViewer viewer;

	// Ioc
	private IContentProvider contentProvider;
	private OxmInterface oxmBean;
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

	public void refreshView() {
		viewer.setInput(getViewSite());
	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			if (obj instanceof ExecutionModulesContentProvider.ExecutionModuleNode) {
				ExecutionModuleDescriptor emd = ((ExecutionModulesContentProvider.ExecutionModuleNode) obj)
						.getDescriptor();
				if (emd.getLabel() != null) {
					return emd.getLabel();
				} else {
					return getText(emd);
				}
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

		public void dragStart(DragSourceEvent event) {
			System.out.println("Start Drag");
		}

		public void dragSetData(DragSourceEvent event) {

			// System.out.println("dragSetData: " + event);

			IStructuredSelection selection = (IStructuredSelection) viewer
					.getSelection();
			if (selection.getFirstElement() instanceof ExecutionModulesContentProvider.FlowNode) {

				if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
					ExecutionModulesContentProvider.FlowNode flowNode = (ExecutionModulesContentProvider.FlowNode) selection
							.getFirstElement();

					Properties props = new Properties();
					flowNodeAsProperties(props, flowNode);
					props.setProperty("agentId", flowNode
							.getExecutionModuleNode().getAgentNode().getAgent()
							.getAgentUuid());
					props.setProperty("host", flowNode.getExecutionModuleNode()
							.getAgentNode().getAgent().toString());

					ByteArrayOutputStream out = new ByteArrayOutputStream();
					try {
						props.store(out, "");
						event.data = new String(out.toByteArray());
					} catch (IOException e) {
						throw new SlcException(
								"Cannot transform realized flow", e);
					} finally {
						IOUtils.closeQuietly(out);
					}
				}
			}
		}

		public void dragFinished(DragSourceEvent event) {
			System.out.println("Finished Drag");
		}

		protected void flowNodeAsProperties(Properties props, FlowNode fn) {

			RealizedFlow realizedFlow = new RealizedFlow();
			realizedFlow.setModuleName(fn.getExecutionModuleNode()
					.getDescriptor().getName());
			realizedFlow.setModuleVersion(fn.getExecutionModuleNode()
					.getDescriptor().getVersion());
			realizedFlow.setFlowDescriptor(fn.getExecutionFlowDescriptor());

			// As we want to have the effective ExecutionSpec and not a
			// reference; we store it at the RealizeFlow level : thus the
			// marshaller will store the object and not only a reference.
			realizedFlow.setExecutionSpec(fn.getExecutionFlowDescriptor()
					.getExecutionSpec());

			props.setProperty("RealizedFlowAsXml",
					oxmBean.marshal(realizedFlow));
			System.out
					.println(oxmBean.marshal(fn.getExecutionFlowDescriptor()));

		}

	}

	// IoC
	public void setContentProvider(IContentProvider contentProvider) {
		this.contentProvider = contentProvider;
	}

	public void setProcessController(ProcessController processController) {
		this.processController = processController;
	}

	public void setOxmBean(OxmInterface oxmBean) {
		this.oxmBean = oxmBean;
	}

}