package org.argeo.slc.maven;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;

import org.argeo.slc.core.SlcException;

public class MavenManager {
	private final Log log = LogFactory.getLog(getClass());

	private String repositoryId;
	private String repositoryUrl;
	private String localRepositoryPath;

	private ArtifactRepository localRepository;
	private List<ArtifactRepository> remoteRepositories;

	private MavenEmbedder mavenEmbedder;

	public void init() {
		try {
			mavenEmbedder = new MavenEmbedder();
			mavenEmbedder.setOffline(true);
			mavenEmbedder.setClassLoader(Thread.currentThread()
					.getContextClassLoader());
			mavenEmbedder.start();

			mavenEmbedder.setLocalRepositoryDirectory(new File(
					localRepositoryPath));

			localRepository = mavenEmbedder.getLocalRepository();

			// localRepository = mavenEmbedder.createLocalRepository(new File(
			// localRepositoryPath));

			ArtifactRepository repository = mavenEmbedder.createRepository(
					repositoryUrl, repositoryId);

			remoteRepositories = new Vector<ArtifactRepository>();
			remoteRepositories.add(repository);
		} catch (Exception e) {
			throw new SlcException("Cannot initialize Maven manager", e);
		}
	}

	private Artifact resolve(MavenFile mavenDistribution) {
		try {
			Artifact artifact;
			if (mavenDistribution.getClassifier() == null) {
				artifact = mavenEmbedder.createArtifact(mavenDistribution
						.getGroupId(), mavenDistribution.getArtifactId(),
						mavenDistribution.getVersion(),
						Artifact.SCOPE_PROVIDED, mavenDistribution.getType());
			} else {
				artifact = mavenEmbedder.createArtifactWithClassifier(
						mavenDistribution.getGroupId(), mavenDistribution
								.getArtifactId(), mavenDistribution
								.getVersion(), mavenDistribution.getType(),
						mavenDistribution.getClassifier());
			}

			mavenEmbedder
					.resolve(artifact, remoteRepositories, localRepository);

			return artifact;
		} catch (Exception e) {
			throw new SlcException("Cannot resolve artifact.", e);
		}
	}

	public File getPackageLocation(MavenFile mavenDistribution) {
		return resolve(mavenDistribution).getFile();
	}

	public void destroy() {
		try {
			if (mavenEmbedder != null) {
				mavenEmbedder.stop();
			}
		} catch (MavenEmbedderException e) {
			log.error("Cannot destroy Maven manager", e);
		}
	}

	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
	}

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	public void setLocalRepositoryPath(String localRepositoryPath) {
		this.localRepositoryPath = localRepositoryPath;
	}

}
