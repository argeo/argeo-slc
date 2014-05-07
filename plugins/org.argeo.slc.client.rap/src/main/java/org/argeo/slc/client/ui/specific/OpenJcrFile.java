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

import javax.jcr.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.client.rap.OpenJcrFileService;
import org.argeo.slc.client.rap.SlcRapPlugin;
import org.argeo.slc.repo.RepoService;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.ui.PlatformUI;

/**
 * Rap specific command handler to open a file retrieved from a distant JCR
 * Repository. It forwards the request to the correct service after encoding
 * file name and path in the request URI.
 * 
 * This command and the corresponding service are specific for RAP version [1.3,
 * 2)
 */
public class OpenJcrFile extends AbstractHandler {
	private final static Log log = LogFactory.getLog(OpenJcrFile.class);

	public final static String ID = SlcRapPlugin.PLUGIN_ID + ".openJcrFile";

	public final static String PARAM_REPO_NODE_PATH = OpenJcrFileService.PARAM_REPO_NODE_PATH;
	public final static String PARAM_REPO_URI = OpenJcrFileService.PARAM_REPO_URI;
	public final static String PARAM_WORKSPACE_NAME = OpenJcrFileService.PARAM_WORKSPACE_NAME;
	public final static String PARAM_FILE_PATH = OpenJcrFileService.PARAM_FILE_PATH;

	private RepoService repoService;

	public Object execute(ExecutionEvent event) throws ExecutionException {

		String repoNodePath = event.getParameter(PARAM_REPO_NODE_PATH);
		String repoUri = event.getParameter(PARAM_REPO_URI);
		String wkspName = event.getParameter(PARAM_WORKSPACE_NAME);
		String filePath = event.getParameter(PARAM_FILE_PATH);

		// TODO remove
		Session session = repoService.getRemoteSession(repoNodePath, repoUri,
				wkspName);
		JcrUtils.logoutQuietly(session);

		// TODO sanity check
		if (filePath == null || "".equals(filePath.trim()))
			return null;

		try {
			if (log.isDebugEnabled())
				log.debug("URL : "
						+ createFullDownloadUrl(repoNodePath, repoUri,
								wkspName, filePath));
			// RWT.getResponse().sendRedirect(createFullDownloadUrl(repoNodePath,
			// repoUri,
			// wkspName, filePath));

			URL url = new URL(createFullDownloadUrl(repoNodePath, repoUri,
					wkspName, filePath));
			PlatformUI.getWorkbench().getBrowserSupport()
					.createBrowser("DownloadDialog").openURL(url);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private String createFullDownloadUrl(String repoNodePath, String repoUri,
			String wkspName, String filePath) {
		StringBuilder url = new StringBuilder();
		url.append(RWT.getRequest().getRequestURL());

		StringBuilder params = new StringBuilder();
		params.append("?");
		params.append(IServiceHandler.REQUEST_PARAM).append("=");
		params.append(OpenJcrFileService.ID);
		if (repoNodePath != null)
			params.append("&").append(OpenJcrFileService.PARAM_REPO_NODE_PATH)
					.append("=").append(repoNodePath);

		if (repoUri != null)
			params.append("&").append(OpenJcrFileService.PARAM_REPO_URI)
					.append("=").append(repoUri);

		if (wkspName != null)
			params.append("&").append(OpenJcrFileService.PARAM_WORKSPACE_NAME)
					.append("=").append(wkspName);

		if (filePath != null)
			params.append("&").append(OpenJcrFileService.PARAM_FILE_PATH)
					.append("=").append(filePath);

		String encodedURL = RWT.getResponse().encodeURL(params.toString());
		url.append(encodedURL);

		return url.toString();
	}

	/* Dependency Injection */
	// only used as a workaround to force the service instantiation
	public void setOpenJcrFileService(OpenJcrFileService openJcrFileService) {
		// do nothing.
	}

	public void setRepoService(RepoService repoService) {
		this.repoService = repoService;
	}

}