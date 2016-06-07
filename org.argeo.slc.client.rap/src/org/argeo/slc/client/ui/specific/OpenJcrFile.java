/*
 * Copyright (C) 2007-2012 Argeo GmbH
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
package org.argeo.slc.client.ui.specific;

import java.net.URL;
import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.Session;

import org.argeo.slc.SlcException;
import org.argeo.slc.client.rap.OpenJcrFileService;
import org.argeo.slc.client.rap.SlcRapPlugin;
import org.argeo.slc.repo.RepoService;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.rap.rwt.RWT;
//import org.eclipse.rap.rwt.service.IServiceHandler;
//import org.eclipse.rap.rwt.service.IServiceManager;
import org.eclipse.ui.PlatformUI;

/**
 * Rap specific command handler to open a file retrieved from a distant JCR
 * Repository. It creates and register a service instantiated with the
 * corresponding JCR node, forwards the request, and un register the service on
 * dispose
 * 
 * This command and the corresponding service are specific for RAP version [1.3,
 * 2)
 */
public class OpenJcrFile extends AbstractHandler {

	// Use (new OpenJcrFileCmdId()).getCmdId() instead.
	// public final String ID = SlcRapPlugin.PLUGIN_ID + ".openJcrFile";
	
	public final static String PARAM_REPO_NODE_PATH = "param.repoNodePath";
	public final static String PARAM_REPO_URI = "param.repoUri";
	public final static String PARAM_WORKSPACE_NAME = "param.workspaceName";
	public final static String PARAM_FILE_PATH = "param.filePath";

	private RepoService repoService;
	private String currentServiceId;

	public Object execute(ExecutionEvent event) throws ExecutionException {

		String repoNodePath = event.getParameter(PARAM_REPO_NODE_PATH);
		String repoUri = event.getParameter(PARAM_REPO_URI);
		String wkspName = event.getParameter(PARAM_WORKSPACE_NAME);
		String filePath = event.getParameter(PARAM_FILE_PATH);

		// TODO sanity check
		if (filePath == null || "".equals(filePath.trim()))
			return null;
		Session businessSession = null;
		try {
			businessSession = repoService.getRemoteSession(repoNodePath,
					repoUri, wkspName);
			Node result = businessSession.getNode(filePath);

			// Create a temporary service. No better solution has been found
			// yet.
			currentServiceId = UUID.randomUUID().toString();
			OpenJcrFileService ojfs = new OpenJcrFileService(result);
			// FIXME replace it
//			IServiceManager manager = RWT.getServiceManager();
//			manager.registerServiceHandler(currentServiceId, ojfs);
			String urlStr = createFullDownloadUrl(currentServiceId);
			URL url = new URL(urlStr);
			PlatformUI.getWorkbench().getBrowserSupport()
					.createBrowser("DownloadDialog").openURL(url);
		} catch (Exception e) {
			throw new SlcException("Unable to open Jcr File for path "
					+ filePath, e);
		}

		return null;
	}

	@Override
	public void dispose() {
//		IServiceManager manager = RWT.getServiceManager();
//		manager.unregisterServiceHandler(currentServiceId);
		super.dispose();
	}

	private String createFullDownloadUrl(String serviceId) {
		StringBuilder url = new StringBuilder();
		url.append(RWT.getRequest().getRequestURL());

		StringBuilder params = new StringBuilder();
		params.append("?");
		// FIXME commented out so that it builds
		//params.append(IServiceHandler.REQUEST_PARAM).append("=");
		params.append(serviceId);
		String encodedURL = RWT.getResponse().encodeURL(params.toString());
		url.append(encodedURL);
		return url.toString();
	}

	/* Dependency Injection */
	public void setRepoService(RepoService repoService) {
		this.repoService = repoService;
	}
}