package org.argeo.slc.repo.maven.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
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
import org.argeo.slc.repo.RepoConstants;
import org.sonatype.aether.repository.RemoteRepository;

/**
 * Expose the SLC repository as a regular Maven repository, proxying third party
 * repositories as well.
 */
public class MavenProxyServlet extends HttpServlet implements ArgeoNames,
		SlcNames {
	private static final long serialVersionUID = 5296857859305486588L;

	private final static Log log = LogFactory.getLog(MavenProxyServlet.class);

	private Session jcrSession;
	private List<RemoteRepository> defaultRepositories = new ArrayList<RemoteRepository>();
	private String contentTypeCharset = "UTF-8";

	@Override
	public void init() throws ServletException {
		try {
			Node proxiedRepositories = JcrUtils.mkdirs(jcrSession,
					RepoConstants.PROXIED_REPOSITORIES);
			for (RemoteRepository repository : defaultRepositories) {
				if (!proxiedRepositories.hasNode(repository.getId())) {
					Node proxiedRepository = proxiedRepositories
							.addNode(repository.getId());
					proxiedRepository.setProperty(SLC_URL, repository.getUrl());
					proxiedRepository.setProperty(SLC_TYPE,
							repository.getContentType());
				}
			}
			jcrSession.save();
		} catch (RepositoryException e) {
			JcrUtils.discardQuietly(jcrSession);
			throw new SlcException("Cannot initialize Maven proxy", e);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String path = request.getPathInfo();

		String nodePath = RepoConstants.ARTIFACTS_BASE_PATH + path;
		if (log.isDebugEnabled())
			log.debug("path=" + path + ", nodePath=" + nodePath);

		try {
			Node node = null;
			if (jcrSession.itemExists(nodePath)) {
				node = jcrSession.getNode(nodePath);
			} else {
				proxiedRepositories: for (NodeIterator nit = jcrSession
						.getNode(RepoConstants.PROXIED_REPOSITORIES).getNodes(); nit
						.hasNext();) {
					Node proxiedRepository = nit.nextNode();
					String repoUrl = proxiedRepository.getProperty(SLC_URL)
							.getString();
					String remoteUrl = repoUrl + path;
					if (log.isDebugEnabled())
						log.debug("remoteUrl=" + remoteUrl);
					InputStream in = null;
					try {
						URL u = new URL(remoteUrl);
						in = u.openStream();
						node = importFile(nodePath, in);
						break proxiedRepositories;
					} catch (Exception e) {
						if (log.isTraceEnabled())
							log.trace("Cannot read " + remoteUrl
									+ ", skipping...");
					} finally {
						IOUtils.closeQuietly(in);
					}
				}

				if (node == null) {
					response.sendError(404);
					return;
					// throw new SlcException("Could not find " + path
					// + " among proxies");
				}
			}
			processResponse(nodePath, node, response);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot proxy " + request, e);
		}
		super.doGet(request, response);
	}

	/** Download the content of the node. */
	protected void processResponse(String path, Node node,
			HttpServletResponse response) {
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
		} catch (RepositoryException e) {
			throw new SlcException("Cannot download " + node, e);
		}
	}

	protected Node importFile(String nodePath, InputStream in) {
		Binary binary = null;
		try {
			Node node = JcrUtils.mkdirs(jcrSession, nodePath, NodeType.NT_FILE,
					NodeType.NT_FOLDER, false);
			Node content = node.addNode(Node.JCR_CONTENT, NodeType.NT_RESOURCE);
			binary = jcrSession.getValueFactory().createBinary(in);
			content.setProperty(Property.JCR_DATA, binary);
			jcrSession.save();
			return node;
		} catch (RepositoryException e) {
			JcrUtils.discardQuietly(jcrSession);
			throw new SlcException("Cannot initialize Maven proxy", e);
		} finally {
			JcrUtils.closeQuietly(binary);
		}
	}
}
