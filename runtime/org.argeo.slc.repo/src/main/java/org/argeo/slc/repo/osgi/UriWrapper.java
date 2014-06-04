package org.argeo.slc.repo.osgi;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.repo.OsgiFactory;
import org.argeo.slc.repo.RepoUtils;

public class UriWrapper extends BndWrapper implements Runnable {
	private final static Log log = LogFactory.getLog(UriWrapper.class);

	private String uri;
	private String baseUri;
	private String versionSeparator = "-";
	private String extension = "jar";

	private OsgiFactory osgiFactory;

	public UriWrapper() {
		setFactory(this);
	}

	public void run() {
		Session distSession = null;
		Session javaSession = null;
		InputStream in;
		ByteArrayOutputStream out;
		try {
			distSession = osgiFactory.openDistSession();
			javaSession = osgiFactory.openJavaSession();
			if (uri == null) {
				uri = baseUri + '/' + getName() + versionSeparator
						+ getVersion() + "." + extension;
			}
			Node sourceArtifact = osgiFactory.getDist(distSession, uri);

			// TODO factorize with Maven
			in = sourceArtifact.getNode(Node.JCR_CONTENT)
					.getProperty(Property.JCR_DATA).getBinary().getStream();
			out = new ByteArrayOutputStream();
			wrapJar(in, out);
			Node newJarNode = RepoUtils
					.copyBytesAsArtifact(javaSession.getRootNode(),
							getArtifact(), out.toByteArray());
			osgiFactory.indexNode(newJarNode);
			newJarNode.getSession().save();
			if (log.isDebugEnabled())
				log.debug("Wrapped " + uri + " to " + newJarNode.getPath());
		} catch (RepositoryException e) {
			throw new SlcException("Cannot wrap Maven " + uri, e);
		} finally {
			JcrUtils.logoutQuietly(distSession);
			JcrUtils.logoutQuietly(javaSession);
		}
	}

	public void setUri(String sourceCoords) {
		this.uri = sourceCoords;
	}

	public void setOsgiFactory(OsgiFactory osgiFactory) {
		this.osgiFactory = osgiFactory;
	}

	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}

	public void setVersionSeparator(String versionSeparator) {
		this.versionSeparator = versionSeparator;
	}
}
