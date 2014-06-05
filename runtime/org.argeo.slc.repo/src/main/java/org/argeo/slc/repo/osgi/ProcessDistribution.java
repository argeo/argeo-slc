package org.argeo.slc.repo.osgi;

import java.util.Iterator;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.CategorizedNameVersion;
import org.argeo.slc.NameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.repo.ArgeoOsgiDistribution;
import org.argeo.slc.repo.OsgiFactory;
import org.argeo.slc.repo.maven.MavenConventionsUtils;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/** Executes the processes required so that all managed bundles are available. */
public class ProcessDistribution implements Runnable {
	private final static Log log = LogFactory.getLog(ProcessDistribution.class);

	private ArgeoOsgiDistribution osgiDistribution;
	private OsgiFactory osgiFactory;

	public void run() {
		Session javaSession = null;
		try {
			javaSession = osgiFactory.openJavaSession();

			for (Iterator<? extends NameVersion> it = osgiDistribution
					.nameVersions(); it.hasNext();)
				processNameVersion(javaSession, it.next());

			// TODO generate distribution indexes (pom.xml, P2, OBR)
			// osgiFactory.indexNode(node);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot process distribution "
					+ osgiDistribution, e);
		} finally {
			JcrUtils.logoutQuietly(javaSession);
		}
	}

	protected void processNameVersion(Session javaSession,
			NameVersion nameVersion) throws RepositoryException {
		if (log.isTraceEnabled())
			log.trace("Check " + nameVersion + "...");
		if (!(nameVersion instanceof CategorizedNameVersion))
			throw new SlcException("Unsupported type " + nameVersion.getClass());
		CategorizedNameVersion nv = (CategorizedNameVersion) nameVersion;
		Artifact artifact = new DefaultArtifact(nv.getCategory(), nv.getName(),
				"jar", nv.getVersion());
		String path = MavenConventionsUtils.artifactPath("/", artifact);
		if (!javaSession.itemExists(path)) {
			if (nv instanceof BndWrapper) {
				if (log.isDebugEnabled())
					log.debug("Run factory for   : " + nv + "...");
				((BndWrapper) nv).getFactory().run();
			} else {
				log.warn("Skip unsupported   : " + nv);
			}
		} else {
			if (log.isDebugEnabled())
				log.debug("Already available : " + nv);
		}

	}

	public void setOsgiDistribution(ArgeoOsgiDistribution osgiDistribution) {
		this.osgiDistribution = osgiDistribution;
	}

	public void setOsgiFactory(OsgiFactory osgiFactory) {
		this.osgiFactory = osgiFactory;
	}

}
