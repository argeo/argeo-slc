package org.argeo.slc.repo.maven.proxy;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.ArgeoNames;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.repo.RepoConstants;
import org.sonatype.aether.repository.RemoteRepository;

/** Synchronizes the node repository with remote Maven repositories */
public class MavenProxyServiceImpl implements MavenProxyService, ArgeoNames,
		SlcNames {
	private final static Log log = LogFactory
			.getLog(MavenProxyServiceImpl.class);

	private Repository jcrRepository;
	private Session jcrAdminSession;
	private List<RemoteRepository> defaultRepositories = new ArrayList<RemoteRepository>();

	public void init() {
		try {
			jcrAdminSession = jcrRepository.login();

			JcrUtils.mkdirs(jcrAdminSession, RepoConstants.ARTIFACTS_BASE_PATH);
			Node proxiedRepositories = JcrUtils.mkdirs(jcrAdminSession,
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
			jcrAdminSession.save();
		} catch (RepositoryException e) {
			JcrUtils.discardQuietly(jcrAdminSession);
			throw new SlcException("Cannot initialize Maven proxy", e);
		}
	}

	public void destroy() {
		JcrUtils.logoutQuietly(jcrAdminSession);
	}

	/**
	 * Retrieve and add this file to the repository
	 */
	public synchronized String proxyFile(String path) {
		try {
			Node node = null;
			proxiedRepositories: for (NodeIterator nit = jcrAdminSession
					.getNode(RepoConstants.PROXIED_REPOSITORIES).getNodes(); nit
					.hasNext();) {
				Node proxiedRepository = nit.nextNode();
				String repoUrl = proxiedRepository.getProperty(SLC_URL)
						.getString();
				String remoteUrl = repoUrl + path;
				if (log.isTraceEnabled())
					log.trace("remoteUrl=" + remoteUrl);
				InputStream in = null;
				try {
					URL u = new URL(remoteUrl);
					in = u.openStream();
					node = importFile(getNodePath(path), in);
					if (log.isDebugEnabled())
						log.debug("Imported " + remoteUrl + " to " + node);
					break proxiedRepositories;
				} catch (Exception e) {
					if (log.isDebugEnabled())
						log.debug("Cannot read " + remoteUrl + ", skipping... "
								+ e.getMessage());
					// e.printStackTrace();
				} finally {
					IOUtils.closeQuietly(in);
				}
			}

			if (node != null)
				return node.getIdentifier();
			else {
				log.warn("Could not proxy " + path);
				return null;
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot proxy " + path, e);
		}
	}

	/** The JCR path where this file could be found */
	public String getNodePath(String path) {
		return RepoConstants.ARTIFACTS_BASE_PATH + path;
	}

	protected Node importFile(String nodePath, InputStream in) {
		Binary binary = null;
		try {
			Node node = JcrUtils.mkdirs(jcrAdminSession, nodePath,
					NodeType.NT_FILE, NodeType.NT_FOLDER, false);
			Node content = node.addNode(Node.JCR_CONTENT, NodeType.NT_RESOURCE);
			binary = jcrAdminSession.getValueFactory().createBinary(in);
			content.setProperty(Property.JCR_DATA, binary);
			jcrAdminSession.save();
			return node;
		} catch (RepositoryException e) {
			JcrUtils.discardQuietly(jcrAdminSession);
			throw new SlcException("Cannot initialize Maven proxy", e);
		} finally {
			JcrUtils.closeQuietly(binary);
		}
	}

	public void setJcrRepository(Repository jcrRepository) {
		this.jcrRepository = jcrRepository;
	}

	public void setDefaultRepositories(
			List<RemoteRepository> defaultRepositories) {
		this.defaultRepositories = defaultRepositories;
	}

}
