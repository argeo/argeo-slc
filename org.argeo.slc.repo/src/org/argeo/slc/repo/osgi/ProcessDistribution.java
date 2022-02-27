package org.argeo.slc.repo.osgi;

import java.util.Iterator;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.api.cms.CmsLog;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.CategoryNameVersion;
import org.argeo.slc.NameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.repo.ArgeoOsgiDistribution;
import org.argeo.slc.repo.ModularDistributionFactory;
import org.argeo.slc.repo.OsgiFactory;
import org.argeo.slc.repo.maven.MavenConventionsUtils;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

/**
 * Executes the processes required so that all managed bundles are available.
 */
public class ProcessDistribution implements Runnable {
	private final static CmsLog log = CmsLog.getLog(ProcessDistribution.class);

	private ArgeoOsgiDistribution osgiDistribution;
	private OsgiFactory osgiFactory;

	public void run() {
		Session javaSession = null;
		try {
			javaSession = osgiFactory.openJavaSession();
			for (Iterator<? extends NameVersion> it = osgiDistribution.nameVersions(); it.hasNext();)
				processNameVersion(javaSession, it.next());

			// Check sources
			for (Iterator<? extends NameVersion> it = osgiDistribution.nameVersions(); it.hasNext();) {
				CategoryNameVersion nv = (CategoryNameVersion) it.next();
				Artifact artifact = new DefaultArtifact(nv.getCategory(), nv.getName() + ".source", "jar",
						nv.getVersion());
				String path = MavenConventionsUtils.artifactPath("/", artifact);
				if (!javaSession.itemExists(path))
					log.warn("No source available for " + nv);
			}

			// explicitly create the corresponding modular distribution as we
			// have here all necessary info.
			ModularDistributionFactory mdf = new ModularDistributionFactory(osgiFactory, osgiDistribution);
			mdf.run();

		} catch (RepositoryException e) {
			throw new SlcException("Cannot process distribution " + osgiDistribution, e);
		} finally {
			JcrUtils.logoutQuietly(javaSession);
		}
	}

	protected void processNameVersion(Session javaSession, NameVersion nameVersion) throws RepositoryException {
		if (log.isTraceEnabled())
			log.trace("Check " + nameVersion + "...");
		if (!(nameVersion instanceof CategoryNameVersion))
			throw new SlcException("Unsupported type " + nameVersion.getClass());
		CategoryNameVersion nv = (CategoryNameVersion) nameVersion;
		Artifact artifact = new DefaultArtifact(nv.getCategory(), nv.getName(), "jar", nv.getVersion());
		String path = MavenConventionsUtils.artifactPath("/", artifact);
		if (!javaSession.itemExists(path)) {
			if (nv instanceof BndWrapper) {
				if (log.isDebugEnabled())
					log.debug("Run factory for   : " + nv + "...");
				((BndWrapper) nv).getFactory().run();
			} else if (nv instanceof Runnable) {
				((Runnable) nv).run();
			} else {
				log.warn("Skip unsupported   : " + nv);
			}
		} else {
			if (log.isTraceEnabled())
				log.trace("Already available : " + nv);
		}
	}

	/* DEPENDENCY INJECTION */
	public void setOsgiDistribution(ArgeoOsgiDistribution osgiDistribution) {
		this.osgiDistribution = osgiDistribution;
	}

	public void setOsgiFactory(OsgiFactory osgiFactory) {
		this.osgiFactory = osgiFactory;
	}
}