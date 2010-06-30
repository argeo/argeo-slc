/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
