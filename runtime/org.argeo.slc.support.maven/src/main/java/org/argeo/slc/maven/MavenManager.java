package org.argeo.slc.maven;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.apache.maven.artifact.Artifact;
//import org.apache.maven.artifact.repository.ArtifactRepository;
//import org.apache.maven.embedder.MavenEmbedder;
//import org.apache.maven.embedder.MavenEmbedderException;

import org.argeo.slc.SlcException;

public class MavenManager {
	/*
	private final Log log = LogFactory.getLog(getClass());

	private String localRepositoryPath = System.getProperty("user.home")
			+ File.separator + ".m2" + File.separator + "repository";

	private ArtifactRepository localRepository;
	private List<ArtifactRepository> remoteRepositoriesInternal;
	private List<RemoteRepository> remoteRepositories = new Vector<RemoteRepository>();

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

			remoteRepositoriesInternal = new Vector<ArtifactRepository>();
			for (RemoteRepository remoteRepository : remoteRepositories) {

				ArtifactRepository repository = mavenEmbedder.createRepository(
						remoteRepository.getUrl(), remoteRepository.getId());
				remoteRepositoriesInternal.add(repository);
			}
			
			MavenFile mavenFile = new MavenFile();
			mavenFile.setGroupId("org.argeo.slc.runtime");
			mavenFile.setArtifactId("org.argeo.slc.specs");
			mavenFile.setVersion("0.11.3-SNAPSHOT");
			Artifact artifact = resolve(mavenFile);
			log.debug("Dependencies of "+artifact);
			for(Object obj : artifact.getDependencyTrail()){
				log.debug("  "+obj);
			}

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

			mavenEmbedder.resolve(artifact, remoteRepositoriesInternal,
					localRepository);

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

	public void setLocalRepositoryPath(String localRepositoryPath) {
		this.localRepositoryPath = localRepositoryPath;
	}

	public List<RemoteRepository> getRemoteRepositories() {
		return remoteRepositories;
	}

	public void setRemoteRepositories(List<RemoteRepository> remoteRepositories) {
		this.remoteRepositories = remoteRepositories;
	}
*/
}
