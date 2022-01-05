package org.argeo.slc.repo.osgi;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Session;

import org.apache.commons.io.IOUtils;
import org.argeo.api.cms.CmsLog;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.DefaultNameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.repo.OsgiFactory;
import org.argeo.slc.repo.RepoUtils;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

/**
 * BND wrapper based on a Maven artifact available from one of the configured
 * repositories.
 */
public class MavenWrapper extends BndWrapper implements Runnable {
	private final static CmsLog log = CmsLog.getLog(MavenWrapper.class);

	private String sourceCoords;

	private OsgiFactory osgiFactory;

	private Boolean doNotModifySources = false;

	public MavenWrapper() {
		setFactory(this);
	}

	@Override
	public String getVersion() {
		String version = super.getVersion();
		if (version != null)
			return version;
		return new DefaultArtifact(sourceCoords).getVersion();
	}

	public void run() {
		Session distSession = null;
		Session javaSession = null;
		InputStream in = null;
		ByteArrayOutputStream out = null;
		try {
			distSession = osgiFactory.openDistSession();
			javaSession = osgiFactory.openJavaSession();
			Node origArtifact;
			try {
				origArtifact = osgiFactory.getMaven(distSession, sourceCoords);
			} catch (Exception e1) {
				origArtifact = osgiFactory.getMaven(distSession, sourceCoords + ":" + getVersion());
			}

			in = origArtifact.getNode(Node.JCR_CONTENT).getProperty(Property.JCR_DATA).getBinary().getStream();
			out = new ByteArrayOutputStream();
			wrapJar(in, out);
			Node newJarNode = RepoUtils.copyBytesAsArtifact(javaSession.getRootNode(), getArtifact(),
					out.toByteArray());
			osgiFactory.indexNode(newJarNode);
			newJarNode.getSession().save();

			if (log.isDebugEnabled())
				log.debug("Wrapped Maven " + sourceCoords + " to " + newJarNode.getPath());

			// sources
			Artifact sourcesArtifact = new SubArtifact(new DefaultArtifact(sourceCoords), "sources", null);
			Node sourcesArtifactNode;
			try {

				sourcesArtifactNode = osgiFactory.getMaven(distSession, sourcesArtifact.toString());
			} catch (SlcException e) {
				// no sources available
				return;
			}

			IOUtils.closeQuietly(in);
			in = sourcesArtifactNode.getNode(Node.JCR_CONTENT).getProperty(Property.JCR_DATA).getBinary().getStream();
			byte[] pdeSource;
			if (doNotModifySources)
				pdeSource = IOUtils.toByteArray(in);
			else
				pdeSource = RepoUtils.packageAsPdeSource(in, new DefaultNameVersion(getName(), getVersion()));
			Node pdeSourceNode = RepoUtils.copyBytesAsArtifact(javaSession.getRootNode(),
					new DefaultArtifact(getCategory(), getName() + ".source", "jar", getVersion()), pdeSource);
			osgiFactory.indexNode(pdeSourceNode);
			pdeSourceNode.getSession().save();

			if (log.isDebugEnabled())
				log.debug("Wrapped Maven " + sourcesArtifact + " to PDE sources " + pdeSourceNode.getPath());
		} catch (Exception e) {
			throw new SlcException("Cannot wrap Maven " + sourceCoords, e);
		} finally {
			JcrUtils.logoutQuietly(distSession);
			JcrUtils.logoutQuietly(javaSession);
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}

	public void setSourceCoords(String sourceCoords) {
		this.sourceCoords = sourceCoords;
	}

	public String getSourceCoords() {
		return sourceCoords;
	}

	public void setOsgiFactory(OsgiFactory osgiFactory) {
		this.osgiFactory = osgiFactory;
	}

	public void setDoNotModifySources(Boolean doNotModifySources) {
		this.doNotModifySources = doNotModifySources;
	}

}
