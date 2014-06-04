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

public class MavenWrapper extends BndWrapper implements Runnable {
	private final static Log log = LogFactory.getLog(MavenWrapper.class);

	private String sourceCoords;

	private OsgiFactory osgiFactory;

	public MavenWrapper() {
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
			Node sourceArtifact = osgiFactory.getMaven(distSession,
					sourceCoords);

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
				log.debug("Wrapped Maven " + sourceCoords + " to "
						+ newJarNode.getPath());
		} catch (RepositoryException e) {
			throw new SlcException("Cannot wrap Maven " + sourceCoords, e);
		} finally {
			JcrUtils.logoutQuietly(distSession);
			JcrUtils.logoutQuietly(javaSession);
		}
	}

	public void setSourceCoords(String sourceCoords) {
		this.sourceCoords = sourceCoords;
	}

	public void setOsgiFactory(OsgiFactory osgiFactory) {
		this.osgiFactory = osgiFactory;
	}

}
