package org.argeo.slc.repo;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.ArgeoJcrUtils;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;

/** Sync to from software repositories */
public class RepoSync implements Runnable {
	private final static Log log = LogFactory.getLog(RepoSync.class);

	private String sourceRepo;
	private String targetRepo;

	private String sourceWksp;

	private String sourceUsername;
	private char[] sourcePassword;

	private RepositoryFactory repositoryFactory;

	public void run() {
		try {
			long begin = System.currentTimeMillis();

			Repository sourceRepository = ArgeoJcrUtils.getRepositoryByUri(
					repositoryFactory, sourceRepo);
			Repository targetRepository = ArgeoJcrUtils.getRepositoryByUri(
					repositoryFactory, targetRepo);
			Credentials sourceCredentials = null;
			if (sourceUsername != null)
				sourceCredentials = new SimpleCredentials(sourceUsername,
						sourcePassword);
			Session sourceSession = sourceRepository.login(sourceCredentials,
					sourceWksp);

			Credentials targetCredentials = null;
			Session targetSession = targetRepository.login(targetCredentials,
					sourceWksp);

			Long count = JcrUtils.copyFiles(sourceSession.getRootNode(),
					targetSession.getRootNode(), true, null);

			long duration = (System.currentTimeMillis() - begin) / 1000;// in
			// s
			if (log.isDebugEnabled())
				log.debug("Copied " + count + " files in " + (duration / 60)
						+ "min " + (duration % 60) + "s");
		} catch (RepositoryException e) {
			throw new SlcException("Cannot sync " + sourceRepo + " to "
					+ targetRepo, e);
		}
	}

	public void setSourceRepo(String sourceRepo) {
		this.sourceRepo = sourceRepo;
	}

	public void setTargetRepo(String targetRepo) {
		this.targetRepo = targetRepo;
	}

	public void setSourceWksp(String sourceWksp) {
		this.sourceWksp = sourceWksp;
	}

	public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
		this.repositoryFactory = repositoryFactory;
	}

	public void setSourceUsername(String sourceUsername) {
		this.sourceUsername = sourceUsername;
	}

	public void setSourcePassword(char[] sourcePassword) {
		this.sourcePassword = sourcePassword;
	}

}
