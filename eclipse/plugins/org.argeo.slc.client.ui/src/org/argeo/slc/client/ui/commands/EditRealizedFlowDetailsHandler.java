package org.argeo.slc.client.ui.commands;

import org.argeo.slc.client.oxm.OxmInterface;
import org.argeo.slc.client.ui.views.ProcessParametersView;
import org.argeo.slc.process.RealizedFlow;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Command handler to display and edit the attributes of a given Realizedflow.
 * The corresponding RealizedFlow is passed via command parameters and
 * unmarshalled with the oxmBean which is injected by Spring.
 * 
 * @author bsinou
 * 
 */

public class EditRealizedFlowDetailsHandler extends AbstractHandler {

	// IoC
	private OxmInterface oxmBean;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		// We pass Realized flow through command parameters as XML
		String rfAsXml = event
				.getParameter("org.argeo.slc.client.commands.realizedFlowAsXml");
		int index = new Integer(
				event.getParameter("org.argeo.slc.client.commands.realizedFlowIndex"))
				.intValue();
		RealizedFlow rf = (RealizedFlow) oxmBean.unmarshal(rfAsXml);

		try {
			ProcessParametersView ppView = (ProcessParametersView) HandlerUtil
					.getActiveWorkbenchWindow(event).getActivePage()
					.showView(ProcessParametersView.ID);
			ppView.setRealizedFlow(index, rf);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// IoC
	public void setOxmBean(OxmInterface oxmBean) {
		this.oxmBean = oxmBean;
	}

}
