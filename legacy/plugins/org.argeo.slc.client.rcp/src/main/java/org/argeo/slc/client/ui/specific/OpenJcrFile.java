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

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.io.IOUtils;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.repo.RepoService;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * RCP specific command handler to open a file retrieved from a local or distant
 * JCR Repository.
 */
public class OpenJcrFile extends AbstractHandler {
	// private final static Log log = LogFactory.getLog(OpenJcrFile.class);

	// Here is the trick that enable single sourcing: the ID is determined at
	// runtime so use (new OpenJcrFileCmdId()).getCmdId() instead of the usual
	// public final String ID = SlcRcpPlugin.PLUGIN_ID + ".openJcrFile";

	public final static String PARAM_REPO_NODE_PATH = "param.repoNodePath";
	public final static String PARAM_REPO_URI = "param.repoUri";
	public final static String PARAM_WORKSPACE_NAME = "param.workspaceName";
	public final static String PARAM_FILE_PATH = "param.filePath";

	/* DEPENDENCY INJECTION */
	private RepoService repoService;

	public Object execute(ExecutionEvent event) throws ExecutionException {

		String repoNodePath = event.getParameter(PARAM_REPO_NODE_PATH);
		String repoUri = event.getParameter(PARAM_REPO_URI);
		String wkspName = event.getParameter(PARAM_WORKSPACE_NAME);
		String filePath = event.getParameter(PARAM_FILE_PATH);

		// TODO sanity check
		if (filePath == null || "".equals(filePath.trim()))
			return null;
		retrieveAndOpen(repoNodePath, repoUri, wkspName, filePath);

		return null;
	}

	protected void retrieveAndOpen(String repoNodePath, String repoUri,
			String wkspName, String filePath) {
		Session businessSession = null;
		try {
			businessSession = repoService.getRemoteSession(repoNodePath,
					repoUri, wkspName);
			Node result = businessSession.getNode(filePath);

			boolean isValid = true;
			Node child = null;
			if (!result.isNodeType(NodeType.NT_FILE))
				isValid = false;
			else {
				child = result.getNode(Property.JCR_CONTENT);
				if (!(child.isNodeType(NodeType.NT_RESOURCE) || child
						.hasProperty(Property.JCR_DATA)))
					isValid = false;
			}

			if (!isValid)
				return;

			InputStream fis = null;
			String prefix = "", extension = "";
			String fileName = JcrUtils.lastPathElement(filePath);
			int ind = fileName.lastIndexOf('.');
			if (ind > 0) {
				prefix = fileName.substring(0, ind);
				extension = fileName.substring(ind);
			}
			try {

				fis = (InputStream) child.getProperty(Property.JCR_DATA)
						.getBinary().getStream();
				File file = createTmpFile(prefix, extension, fis);
				Desktop desktop = null;
				if (Desktop.isDesktopSupported()) {
					desktop = Desktop.getDesktop();
				}
				desktop.open(file);
			} catch (Exception e) {
				throw new SlcException(
						"Stream error while opening file " + filePath
								+ " from repo " + repoUri == null ? repoNodePath
								: repoUri, e);
			} finally {
				IOUtils.closeQuietly(fis);
			}
		} catch (RepositoryException e) {
			throw new SlcException("Unexpected error while "
					+ "getting repoNode info for repoNode at path "
					+ repoNodePath, e);
		} finally {
			JcrUtils.logoutQuietly(businessSession);
		}
	}

	private File createTmpFile(String prefix, String suffix, InputStream is) {
		File tmpFile = null;
		OutputStream os = null;
		try {
			tmpFile = File.createTempFile(prefix, suffix);
			os = new FileOutputStream(tmpFile);
			IOUtils.copy(is, os);
		} catch (IOException e) {
			throw new SlcException("Cannot open file " + prefix + "." + suffix,
					e);
		} finally {
			IOUtils.closeQuietly(os);
		}
		return tmpFile;
	}

	/* DEPENDENCY INJECTION */
	public void setRepoService(RepoService repoService) {
		this.repoService = repoService;
	}
}