package org.argeo.slc.maven;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.InvalidDependencyVersionException;
import org.argeo.slc.SlcException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.embed.Embedder;

public class MavenManager {

	private final Log log = LogFactory.getLog(getClass());

	private String localRepositoryPath = System.getProperty("user.home")
			+ File.separator + ".m2" + File.separator + "repository";

	private ArtifactRepository localRepository;
	private List<ArtifactRepository> remoteRepositoriesInternal;
	private List<RemoteRepository> remoteRepositories = new Vector<RemoteRepository>();

	private SlcMavenEmbedder mavenEmbedder;
	private ClassLoader classLoader;
	private Boolean offline = false;

	public void init() {
		try {
			mavenEmbedder = new SlcMavenEmbedder();
			mavenEmbedder.setOffline(offline);
			//mavenEmbedder.setAlignWithUserInstallation(true);
			if (classLoader != null)
				mavenEmbedder.setClassLoader(classLoader);
			else
				mavenEmbedder.setClassLoader(getClass().getClassLoader());
			// else
			// mavenEmbedder.setClassLoader(Thread.currentThread()
			// .getContextClassLoader());
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
			mavenFile.setGroupId("org.argeo.slc.dist");
			mavenFile.setArtifactId("org.argeo.slc.sdk");
			mavenFile.setVersion("0.12.2-SNAPSHOT");
			mavenFile.setType("pom");
			Artifact artifact = resolve(mavenFile);
			log.debug("Location of " + artifact + " : " + artifact.getFile());
			// log.debug("Dependencies of " + artifact);
			// for (Object obj : artifact.getDependencyTrail()) {
			// log.debug("  " + obj);
			// }

			MavenProject project = mavenEmbedder
					.readProjectWithDependencies(new File(
							"/home/mbaudier/dev/src/slc/dist/org.argeo.slc.sdk/pom.xml"));
			// MavenProject project = mavenEmbedder
			// .readProjectWithDependencies(artifact.getFile());

			log.debug("Dependencies of " + artifact);
			for (Object obj : getTransitiveProjectDependencies(project,
					remoteRepositoriesInternal, localRepository)) {
				log.debug("  " + obj);
			}

		} catch (Exception e) {
			throw new SlcException("Cannot initialize Maven manager", e);
		}
	}

	public Set getTransitiveProjectDependencies(MavenProject project,
			List remoteRepos, ArtifactRepository local) {
		Embedder embedder = mavenEmbedder.getEmbedder();
		try {
			ArtifactFactory artifactFactory = (ArtifactFactory) embedder
					.lookup(ArtifactFactory.ROLE);

			ArtifactResolver artifactResolver = (ArtifactResolver) embedder
					.lookup(ArtifactResolver.ROLE);

			ArtifactMetadataSource artifactMetadataSource = (ArtifactMetadataSource) embedder
					.lookup(ArtifactMetadataSource.ROLE);

			Set artifacts = project
					.createArtifacts(artifactFactory, null, null);

			ArtifactResolutionResult arr = artifactResolver
					.resolveTransitively(artifacts, project.getArtifact(),
							local, remoteRepos, artifactMetadataSource, null);

			return arr.getArtifacts();
		} catch (Exception e) {
			throw new SlcException("Cannot resolve dependency for " + project,
					e);
		}
		// Order, just for display
		// Set dependencies = new TreeSet(new ArtifactComparator());
		// dependencies.addAll(arr.getArtifacts());
		// return dependencies;
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

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	public void setOffline(Boolean offline) {
		this.offline = offline;
	}

}
