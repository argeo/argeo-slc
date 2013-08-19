package org.argeo.slc.repo.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcConstants;
import org.argeo.slc.SlcException;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.argeo.slc.repo.NodeIndexer;
import org.argeo.slc.repo.OsgiFactory;

public class OsgiFactoryImpl implements OsgiFactory {
	private final static Log log = LogFactory.getLog(OsgiFactoryImpl.class);

	private String workspace;
	private Repository distRepository;
	private Repository javaRepository;

	private List<NodeIndexer> nodeIndexers = new ArrayList<NodeIndexer>();

	public void init() {
		if (workspace == null)
			throw new SlcException("A workspace must be specified");

		Session javaSession = null;
		Session distSession = null;
		try {
			javaSession = JcrUtils.loginOrCreateWorkspace(javaRepository,
					workspace);
			distSession = JcrUtils.loginOrCreateWorkspace(distRepository,
					workspace);

			// Privileges
			JcrUtils.addPrivilege(javaSession, "/", SlcConstants.ROLE_SLC,
					"jcr:all");
			JcrUtils.addPrivilege(distSession, "/", SlcConstants.ROLE_SLC,
					"jcr:all");
		} catch (RepositoryException e) {
			throw new SlcException("Cannot initialize OSGi Factory "
					+ workspace, e);
		} finally {
			JcrUtils.logoutQuietly(javaSession);
			JcrUtils.logoutQuietly(distSession);
		}
	}

	public void destroy() {

	}

	public Session openJavaSession() throws RepositoryException {
		return javaRepository.login(workspace);
	}

	public Session openDistSession() throws RepositoryException {
		return distRepository.login(workspace);
	}

	public void indexNode(Node node) {
		for (NodeIndexer nodeIndexer : nodeIndexers) {
			nodeIndexer.index(node);
		}
	}

	public Node getDist(Session distSession, String uri)
			throws RepositoryException {

		String distPath = '/' + JcrUtils.urlAsPath(uri);

		// TODO manage mirrors

		if (!distSession.itemExists(distPath))
			loadUrlToPath(uri, distSession, distPath);
		Node distNode = distSession.getNode(distPath);
		return distNode;
	}

	protected void loadUrlToPath(String url, Session distSession, String path)
			throws RepositoryException {
		if (log.isDebugEnabled())
			log.debug("Downloading " + url + "...");

		InputStream in = null;
		Node folderNode = JcrUtils.mkfolders(distSession,
				JcrUtils.parentPath(path));
		try {
			URL u = new URL(url);
			in = u.openStream();
			Node fileNode = JcrUtils.copyStreamAsFile(folderNode,
					JcrUtils.nodeNameFromPath(path), in);
			fileNode.addMixin(SlcTypes.SLC_KNOWN_ORIGIN);
			Node origin = fileNode.addNode(SlcNames.SLC_ORIGIN,
					SlcTypes.SLC_PROXIED);
			JcrUtils.urlToAddressProperties(origin, url);
			distSession.save();

		} catch (MalformedURLException e) {
			throw new SlcException("URL " + url + " not valid.", e);
		} catch (IOException e) {
			throw new SlcException("Cannot load " + url + " to " + path, e);
		}

	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	public void setDistRepository(Repository distRepository) {
		this.distRepository = distRepository;
	}

	public void setJavaRepository(Repository javaRepository) {
		this.javaRepository = javaRepository;
	}

	public void setNodeIndexers(List<NodeIndexer> nodeIndexers) {
		this.nodeIndexers = nodeIndexers;
	}

}
