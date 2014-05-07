package org.argeo.slc.client.rap;

import java.io.IOException;
import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.repo.RepoService;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.IServiceManager;

/**
 * Basic Default service handler that retrieves and launch download of a file
 * stored in a JCR Repository
 */
public class OpenJcrFileService implements IServiceHandler {

	public final static String ID = SlcRapPlugin.PLUGIN_ID + ".openJcrFileService";

	// use local node repo and repository factory to retrieve and log to
	// relevant repository
	public final static String PARAM_REPO_NODE_PATH = "param.repoNodePath";
	// use URI and repository factory to retrieve and ANONYMOUSLY log in
	// relevant repository
	public final static String PARAM_REPO_URI = "param.repoUri";
	public final static String PARAM_WORKSPACE_NAME = "param.workspaceName";
	public final static String PARAM_FILE_PATH = "param.filePath";

	public final static String SCHEME_HOST_SEPARATOR = "://";

	/* DEPENDENCY INJECTION */
	private RepoService repoService;

	public OpenJcrFileService() {
	}

	public void init() {
		IServiceManager manager = RWT.getServiceManager();
		manager.registerServiceHandler(ID, this);
	}

	public void destroy() {
		IServiceManager manager = RWT.getServiceManager();
		manager.unregisterServiceHandler(ID);
	}

	public void service() throws IOException, ServletException {
		String repoNodePath = RWT.getRequest().getParameter(PARAM_REPO_NODE_PATH);
		String repoUri = RWT.getRequest().getParameter(PARAM_REPO_URI);
		String wkspName = RWT.getRequest().getParameter(PARAM_WORKSPACE_NAME);
		String filePath = RWT.getRequest().getParameter(PARAM_FILE_PATH);

		// Get the file content
		byte[] download = getFileAsByteArray(repoNodePath, repoUri, wkspName,
				filePath);

		// Send the file in the response
		HttpServletResponse response = RWT.getResponse();
		response.setContentType("application/octet-stream");
		response.setContentLength(download.length);
		String contentDisposition = "attachment; filename=\""
				+ JcrUtils.lastPathElement(filePath) + "\"";
		response.setHeader("Content-Disposition", contentDisposition);

		try {
			response.getOutputStream().write(download);
		} catch (IOException ioe) {
			throw new SlcException("Error while writing the file " + filePath
					+ " to the servlet response", ioe);
		}
	}

	protected byte[] getFileAsByteArray(String repoNodePath, String repoUri,
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
				return null;
			
			byte[] ba = null;
			InputStream fis = null;
			try {
				fis = (InputStream) child.getProperty(Property.JCR_DATA)
						.getBinary().getStream();
				ba = IOUtils.toByteArray(fis);
			} catch (Exception e) {
				throw new SlcException(
						"Stream error while opening file " + filePath
								+ " from repo " + repoUri == null ? repoNodePath
								: repoUri, e);
			} finally {
				IOUtils.closeQuietly(fis);
			}
			return ba;

		} catch (RepositoryException e) {
			throw new SlcException("Unexpected error while "
					+ "getting repoNode info for repoNode at path "
					+ repoNodePath, e);
		} finally {
			JcrUtils.logoutQuietly(businessSession);
		}
	}

	/* DEPENDENCY INJECTION */
	public void setRepoService(RepoService repoService) {
		this.repoService = repoService;
	}

}