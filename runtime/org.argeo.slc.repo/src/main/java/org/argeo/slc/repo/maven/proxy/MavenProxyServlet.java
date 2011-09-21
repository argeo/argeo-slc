package org.argeo.slc.repo.maven.proxy;

import java.io.IOException;
import java.io.InputStream;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.ArgeoNames;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.jcr.SlcNames;

/**
 * Expose the SLC repository as a regular Maven repository, proxying third party
 * repositories as well.
 */
public class MavenProxyServlet extends HttpServlet implements ArgeoNames,
		SlcNames {
	private static final long serialVersionUID = 5296857859305486588L;

	private final static Log log = LogFactory.getLog(MavenProxyServlet.class);

	private MavenProxyService proxyService;

	private Session jcrSession;
	private String contentTypeCharset = "UTF-8";

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String path = request.getPathInfo();

		String nodePath = proxyService.getNodePath(path);
		if (log.isTraceEnabled())
			log.trace("path=" + path + ", nodePath=" + nodePath);

		try {
			Node node;
			if (!jcrSession.itemExists(nodePath)) {
				String nodeIdentifier = proxyService.proxyFile(path);
				if (nodeIdentifier == null) {
					//log.warn("Could not proxy " + path);
					response.sendError(404);
					return;
				} else {
					node = jcrSession.getNodeByIdentifier(nodeIdentifier);
				}
			} else {
				node = jcrSession.getNode(nodePath);
			}
			processResponse(nodePath, node, response);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot proxy " + request, e);
		}
		//super.doGet(request, response);
	}

	/** Retrieve the content of the node. */
	protected void processResponse(String path, Node node,
			HttpServletResponse response) {
		Binary binary = null;
		InputStream in = null;
		try {
			String fileName = node.getName();
			String ext = FilenameUtils.getExtension(fileName);

			// TODO use a more generic / standard approach
			// see http://svn.apache.org/viewvc/tomcat/trunk/conf/web.xml
			String contentType;
			if ("xml".equals(ext))
				contentType = "text/xml;charset=" + contentTypeCharset;
			else if ("jar".equals(ext))
				contentType = "application/java-archive";
			else if ("zip".equals(ext))
				contentType = "application/zip";
			else if ("gz".equals(ext))
				contentType = "application/x-gzip";
			else if ("tar".equals(ext))
				contentType = "application/x-tar";
			else
				contentType = "application/octet-stream";
			contentType = contentType + ";name=\"" + fileName + "\"";
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ fileName + "\"");
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "no-cache, must-revalidate");
			response.setHeader("Pragma", "no-cache");

			response.setContentType(contentType);

			binary = node.getNode(Property.JCR_CONTENT)
					.getProperty(Property.JCR_DATA).getBinary();
			in = binary.getStream();
			IOUtils.copy(in, response.getOutputStream());
		} catch (Exception e) {
			throw new SlcException("Cannot download " + node, e);
		} finally {
			IOUtils.closeQuietly(in);
			JcrUtils.closeQuietly(binary);
		}
	}

	public void setJcrSession(Session jcrSession) {
		this.jcrSession = jcrSession;
	}

	public void setProxyService(MavenProxyService proxyService) {
		this.proxyService = proxyService;
	}

}
