package org.argeo.slc.repo.osgi;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.Privilege;

import org.apache.commons.io.IOUtils;
import org.argeo.api.cms.CmsLog;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcConstants;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;
import org.argeo.slc.repo.NodeIndexer;
import org.argeo.slc.repo.OsgiFactory;
import org.argeo.slc.repo.RepoConstants;
import org.argeo.slc.repo.maven.MavenConventionsUtils;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

/** Default implementation of {@link OsgiFactory}. */
public class OsgiFactoryImpl implements OsgiFactory, SlcNames {
	private final static CmsLog log = CmsLog.getLog(OsgiFactoryImpl.class);

	private String workspace;
	private Repository distRepository;
	private Repository javaRepository;

	private List<NodeIndexer> nodeIndexers = new ArrayList<NodeIndexer>();

	/** key is URI prefix, value list of base URLs */
	private Map<String, List<String>> mirrors = new HashMap<String, List<String>>();

	private List<String> mavenRepositories = new ArrayList<String>();
	private String downloadBase = RepoConstants.DIST_DOWNLOAD_BASEPATH;
	private String mavenProxyBase = downloadBase + "/maven";

	public void init() {
		if (workspace == null)
			throw new SlcException("A workspace must be specified");

		// default Maven repo
		if (mavenRepositories.size() == 0) {
			// mavenRepositories
			// .add("http://search.maven.org/remotecontent?filepath=");
			mavenRepositories.add("http://repo1.maven.org/maven2");
		}

		Session javaSession = null;
		Session distSession = null;
		try {
			// TODO rather user a JavaRepoManager that will also implicitely
			// manage the indexing of newly created nodes.
			javaSession = JcrUtils.loginOrCreateWorkspace(javaRepository, workspace);
			distSession = JcrUtils.loginOrCreateWorkspace(distRepository, workspace);

			// Privileges
			JcrUtils.addPrivilege(javaSession, "/", SlcConstants.ROLE_SLC, Privilege.JCR_ALL);
			JcrUtils.addPrivilege(distSession, "/", SlcConstants.ROLE_SLC, Privilege.JCR_ALL);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot initialize OSGi Factory " + workspace, e);
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

	public Node getMaven(Session distSession, String coords) throws RepositoryException {
		Artifact artifact = new DefaultArtifact(coords);
		String path = MavenConventionsUtils.artifactPath(mavenProxyBase, artifact);

		// exists
		if (distSession.itemExists(path))
			return distSession.getNode(path);

		for (String mavenRepo : mavenRepositories) {
			String url = MavenConventionsUtils.artifactUrl(mavenRepo, artifact);
			try {
				Node node = loadUrlToPath(url, distSession, path);
				if (node != null) {
					// checksums
					try {
						loadUrlToPath(url + ".md5", distSession, path + ".md5");
					} catch (FileNotFoundException e) {
						// silent
					}
					try {
						loadUrlToPath(url + ".sha1", distSession, path + ".sha1");
					} catch (FileNotFoundException e) {
						// silent
					}
					return node;
				}
			} catch (FileNotFoundException e) {
				if (log.isDebugEnabled())
					log.debug("Maven " + coords + " could not be downloaded from " + url);
			}
		}
		throw new SlcException("Could not download Maven " + coords);
	}

	public Node getDist(Session distSession, String uri) throws RepositoryException {
		String distPath = downloadBase + '/' + JcrUtils.urlAsPath(uri);

		// already retrieved
		if (distSession.itemExists(distPath))
			return distSession.getNode(distPath);

		// find mirror
		List<String> urlBases = null;
		String uriPrefix = null;
		uriPrefixes: for (String uriPref : mirrors.keySet()) {
			if (uri.startsWith(uriPref)) {
				if (mirrors.get(uriPref).size() > 0) {
					urlBases = mirrors.get(uriPref);
					uriPrefix = uriPref;
					break uriPrefixes;
				}
			}
		}
		if (urlBases == null)
			try {
				return loadUrlToPath(uri, distSession, distPath);
			} catch (FileNotFoundException e) {
				throw new SlcException("Cannot download " + uri, e);
			}

		// try to download
		for (String urlBase : urlBases) {
			String relativePath = uri.substring(uriPrefix.length());
			String url = urlBase + relativePath;
			try {
				return loadUrlToPath(url, distSession, distPath);
			} catch (FileNotFoundException e) {
				if (log.isDebugEnabled())
					log.debug("Cannot download " + url + ", trying another mirror");
			}
		}

		throw new SlcException("Could not download " + uri);
	}

	/** Actually downloads a file to an internal location */
	protected Node loadUrlToPath(String url, Session distSession, String path)
			throws RepositoryException, FileNotFoundException {
		if (log.isDebugEnabled())
			log.debug("Downloading " + url + "...");

		InputStream in = null;
		URLConnection conn = null;
		Node folderNode = JcrUtils.mkfolders(distSession, JcrUtils.parentPath(path));
		try {
			URL u = new URL(url);
			conn = u.openConnection();
			conn.connect();
			in = new BufferedInputStream(conn.getInputStream());
			// byte[] arr = IOUtils.toByteArray(in);
			// Node fileNode = JcrUtils.copyBytesAsFile(folderNode,
			// JcrUtils.nodeNameFromPath(path), arr);
			Node fileNode = JcrUtils.copyStreamAsFile(folderNode, JcrUtils.nodeNameFromPath(path), in);
			fileNode.addMixin(SlcTypes.SLC_KNOWN_ORIGIN);
			Node origin = fileNode.addNode(SLC_ORIGIN, SlcTypes.SLC_PROXIED);
			JcrUtils.urlToAddressProperties(origin, url);
			distSession.save();
			return fileNode;
		} catch (MalformedURLException e) {
			throw new SlcException("URL " + url + " not valid.", e);
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw new SlcException("Cannot load " + url + " to " + path, e);
		} finally {
			IOUtils.closeQuietly(in);
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

	public void setMirrors(Map<String, List<String>> mirrors) {
		this.mirrors = mirrors;
	}

	public void setMavenRepositories(List<String> mavenRepositories) {
		this.mavenRepositories = mavenRepositories;
	}

	public void setMavenProxyBase(String mavenProxyBase) {
		this.mavenProxyBase = mavenProxyBase;
	}

}
