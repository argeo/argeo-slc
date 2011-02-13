package org.argeo.slc.aether;

import java.io.File;
import java.util.List;

import org.argeo.slc.SlcException;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;

/** Simplifies access to Aether. */
public class AetherTemplate {
	private RepositorySystem repositorySystem;
	private RepositorySystemSession repositorySystemSession;
	private List<RemoteRepository> remoteRepositories;

	/** Resolves the artifact in order to give access to its file. */
	public File getResolvedFile(Artifact artifact) {
		try {
			ArtifactRequest artifactRequest = new ArtifactRequest(artifact,
					remoteRepositories, null);
			ArtifactResult result = repositorySystem.resolveArtifact(
					repositorySystemSession, artifactRequest);
			return result.getArtifact().getFile();
		} catch (ArtifactResolutionException e) {
			throw new SlcException("Cannot resolve " + artifact, e);
		}
	}

	/**
	 * Transitively resolves the dependencies of this artifact (with scope
	 * 'compile')
	 * 
	 * @param artifact
	 *            the artifact to resolve
	 */
	public DependencyNode resolveDependencies(Artifact artifact) {
		return resolveDependencies(artifact, "compile");
	}

	/**
	 * Transitively resolves the dependencies of this artifact.
	 * 
	 * @param artifact
	 *            the artifact to resolve
	 * @param scope
	 *            the scope
	 */
	public DependencyNode resolveDependencies(Artifact artifact, String scope) {
		try {
			Dependency dependency = new Dependency(artifact, scope);
			CollectRequest collectRequest = new CollectRequest();
			collectRequest.setRoot(dependency);
			for (RemoteRepository remoteRepository : remoteRepositories)
				collectRequest.addRepository(remoteRepository);
			DependencyNode node = repositorySystem.collectDependencies(
					repositorySystemSession, collectRequest).getRoot();

			repositorySystem.resolveDependencies(repositorySystemSession, node,
					null);
			return node;
		} catch (Exception e) {
			throw new SlcException("Cannot resolve dependencies of " + artifact
					+ " (scope: " + scope + ")", e);
		}
	}

	public RepositorySystem getRepositorySystem() {
		return repositorySystem;
	}

	public void setRepositorySystem(RepositorySystem repositorySystem) {
		this.repositorySystem = repositorySystem;
	}

	public RepositorySystemSession getRepositorySystemSession() {
		return repositorySystemSession;
	}

	public void setRepositorySystemSession(
			RepositorySystemSession repositorySystemSession) {
		this.repositorySystemSession = repositorySystemSession;
	}

	public List<RemoteRepository> getRemoteRepositories() {
		return remoteRepositories;
	}

	public void setRemoteRepositories(List<RemoteRepository> remoteRepositories) {
		this.remoteRepositories = remoteRepositories;
	}

}
