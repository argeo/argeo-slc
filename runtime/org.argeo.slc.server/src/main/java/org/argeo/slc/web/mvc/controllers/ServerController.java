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

package org.argeo.slc.web.mvc.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.argeo.slc.build.BasicNameVersion;
import org.argeo.slc.build.NameVersion;
import org.argeo.slc.core.build.ResourceDistribution;
import org.argeo.slc.deploy.DynamicRuntime;
import org.argeo.slc.deploy.Module;
import org.argeo.slc.msg.ExecutionAnswer;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Sends back the results, rendered or as collection.
 */

@Controller
public class ServerController {

	// IoC
	private DynamicRuntime<?> dynamicRuntime;

	// Create a factory for disk-based file items
	private FileItemFactory factory = new DiskFileItemFactory();
	// Create a new file upload handler
	private ServletFileUpload upload = new ServletFileUpload(factory);

	// SERVER HANDLING

	@RequestMapping("/isServerReady.service")
	protected ExecutionAnswer isServerReady(Model model) {
		// Does nothing for now, it will return an OK answer.
		return ExecutionAnswer.ok("Execution completed properly");
	}

	@RequestMapping("/shutdownRuntime.service")
	protected ExecutionAnswer shutdownRuntime(Model model) {
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
		return ExecutionAnswer.ok("Server shutting down...");
	}

	// MODULE HANDLING

	@SuppressWarnings("unchecked")
	@RequestMapping("/installModule.service")
	public void installModule(HttpServletRequest request) throws Exception {

		// TODO : handle the exception better

		// Parse the request
		List<FileItem> items = upload.parseRequest(request);

		byte[] arr = null;
		for (FileItem item : items) {
			if (!item.isFormField()) {
				arr = item.get();
				break;
			}
		}

		ByteArrayResource res = new ByteArrayResource(arr);
		Module module = dynamicRuntime.installModule(new ResourceDistribution(
				res));
		// TODO: customize whether the module is started or not
		dynamicRuntime.startModule(module);
	}

	@RequestMapping("/uninstallModule.service")
	public void uninstallModule(@RequestParam(value = "name") String name,
			@RequestParam(value = "version") String version) {
		NameVersion nameVersion = new BasicNameVersion(name, version);
		dynamicRuntime.uninstallModule(nameVersion);
	}

	// IoC
	public void setDynamicRuntime(DynamicRuntime<?> dynamicRuntime) {
		this.dynamicRuntime = dynamicRuntime;
	}

}
