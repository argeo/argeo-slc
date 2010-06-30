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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.argeo.slc.core.build.ResourceDistribution;
import org.argeo.slc.deploy.DynamicRuntime;
import org.argeo.slc.deploy.Module;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.servlet.ModelAndView;

public class InstallModule extends AbstractServiceController {// extends
	private DynamicRuntime<?> dynamicRuntime;

	// Create a factory for disk-based file items
	private FileItemFactory factory = new DiskFileItemFactory();

	// Create a new file upload handler
	private ServletFileUpload upload = new ServletFileUpload(factory);

	@Override
	@SuppressWarnings(value = { "unchecked" })
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {
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

	// protected ModelAndView onSubmit(HttpServletRequest request,
	// HttpServletResponse response, Object command, BindException errors)
	// throws Exception {
	// FileUploadBean bean = (FileUploadBean) command;
	//
	// byte[] file = bean.getFile();
	// if (file == null) {
	// throw new SlcException("Upload is empty.");
	// }
	//
	// ByteArrayResource res = new ByteArrayResource(file);
	// dynamicRuntime.installModule(new ResourceDistribution(res));
	//
	// return super.onSubmit(request, response, command, errors);
	// }
	//
	// protected void initBinder(HttpServletRequest request,
	// ServletRequestDataBinder binder) throws ServletException {
	// // to actually be able to convert Multipart instance to byte[]
	// // we have to register a custom editor
	// binder.registerCustomEditor(byte[].class,
	// new ByteArrayMultipartFileEditor());
	// // now Spring knows how to handle multipart object and convert them
	// }

	public void setDynamicRuntime(DynamicRuntime<?> dynamicRuntime) {
		this.dynamicRuntime = dynamicRuntime;
	}

}
