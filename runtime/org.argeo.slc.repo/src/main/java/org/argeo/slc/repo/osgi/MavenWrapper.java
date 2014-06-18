package org.argeo.slc.repo.osgi;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.DefaultNameVersion;
import org.argeo.slc.NameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.repo.OsgiFactory;
import org.argeo.slc.repo.RepoUtils;
import org.sonatype.aether.util.artifact.DefaultArtifact;

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
			Node origArtifact = osgiFactory.getMaven(distSession, sourceCoords);

			in = origArtifact.getNode(Node.JCR_CONTENT)
					.getProperty(Property.JCR_DATA).getBinary().getStream();
			out = new ByteArrayOutputStream();
			wrapJar(in, out);
			Node newJarNode = RepoUtils
					.copyBytesAsArtifact(javaSession.getRootNode(),
							getArtifact(), out.toByteArray());
			osgiFactory.indexNode(newJarNode);
			newJarNode.getSession().save();

			// sources
			try {
				Node sourcesArtifact = osgiFactory.getMaven(distSession,
						sourceCoords + ":sources");
				IOUtils.closeQuietly(in);
				in = origArtifact.getNode(Node.JCR_CONTENT)
						.getProperty(Property.JCR_DATA).getBinary().getStream();
				byte[] pdeSource = RepoUtils.packageAsPdeSource(in,
						new DefaultNameVersion(getName(), getVersion()));
				Node pdeSourceNode = RepoUtils.copyBytesAsArtifact(javaSession
						.getRootNode(), new DefaultArtifact(getCategory(),
						getName() + ".source", "jar", getVersion()), pdeSource);
				osgiFactory.indexNode(pdeSourceNode);
				pdeSourceNode.getSession().save();
			} catch (SlcException e) {
				// no sources available
			}

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
