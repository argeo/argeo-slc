package org.argeo.slc.web.mvc.management;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.deploy.DynamicRuntime;
import org.argeo.slc.msg.ExecutionAnswer;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

/** . */
public class ShutdownRuntime extends AbstractServiceController {
	private DynamicRuntime<?> dynamicRuntime;

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {
		new Thread() {
			public void run() {
				// wait in order to let call return
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// silent
				}
				dynamicRuntime.shutdown();
			}
		}.start();
		ExecutionAnswer answer = ExecutionAnswer.ok("Server shutting down...");
		modelAndView.addObject(answer);
	}

	public void setDynamicRuntime(DynamicRuntime<?> dynamicRuntime) {
		this.dynamicRuntime = dynamicRuntime;
	}

}
