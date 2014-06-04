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
import org.argeo.slc.repo.OsgiFactory;
import org.argeo.slc.repo.maven.MavenConventionsUtils;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;

public class ProcessDistribution implements Runnable {
	private final static Log log = LogFactory.getLog(ProcessDistribution.class);

	private ArgeoOsgiDistribution osgiDistribution;
	private OsgiFactory osgiFactory;

	public void run() {
		Session javaSession = null;
		try {
			javaSession = osgiFactory.openJavaSession();

			Iterator<NameVersion> it = osgiDistribution.nameVersions();
			while (it.hasNext()) {
				NameVersion t = it.next();
				if (log.isTraceEnabled())
					log.trace("Check " + t + "...");
				if (!(t instanceof CategorizedNameVersion))
					throw new SlcException("Unsupported type " + t.getClass());
				CategorizedNameVersion nv = (CategorizedNameVersion) t;
				Artifact artifact = new DefaultArtifact(nv.getCategory(),
						nv.getName(), "jar", nv.getVersion());
				String path = MavenConventionsUtils.artifactPath("/", artifact);
				if (!javaSession.itemExists(path)) {
					// if (nv instanceof Runnable) {
					// if (log.isDebugEnabled())
					// log.debug("Run " + nv + "...");
					// ((Runnable) nv).run();
					// } else
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
		} catch (RepositoryException e) {
			throw new SlcException("Cannot process distribution "
					+ osgiDistribution, e);
		} finally {
			JcrUtils.logoutQuietly(javaSession);
		}
	}

	public void setOsgiDistribution(ArgeoOsgiDistribution osgiDistribution) {
		this.osgiDistribution = osgiDistribution;
	}

	public void setOsgiFactory(OsgiFactory osgiFactory) {
		this.osgiFactory = osgiFactory;
	}

}
