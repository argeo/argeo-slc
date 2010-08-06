package org.argeo.slc.client.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.argeo.slc.client.ui.ClientUiPlugin;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class ExecutionModulesView extends ViewPart {
	public static final String ID = "org.argeo.slc.client.ui.executionModulesView";

	private TreeViewer viewer;

	private IContentProvider contentProvider;

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
				return ClientUiPlugin.getDefault().getImageRegistry().get(
						"agent");
			else if (obj instanceof ExecutionModulesContentProvider.ExecutionModuleNode)
				return ClientUiPlugin.getDefault().getImageRegistry().get(
						"executionModule");
			else if (obj instanceof ExecutionModulesContentProvider.FolderNode)
				return ClientUiPlugin.getDefault().getImageRegistry().get(
						"folder");
			else if (obj instanceof ExecutionModulesContentProvider.FlowNode)
				return ClientUiPlugin.getDefault().getImageRegistry().get(
						"flow");
			else
				return PlatformUI.getWorkbench().getSharedImages().getImage(
						ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setInput(getViewSite());
		viewer.addDoubleClickListener(new IDoubleClickListener() {

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
					slcExecution.setRealizedFlows(realizedFlows);
					fn.getExecutionModuleNode().getAgentNode().getAgent()
							.runSlcExecution(slcExecution);
				}
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	public void setContentProvider(IContentProvider contentProvider) {
		this.contentProvider = contentProvider;
	}

}