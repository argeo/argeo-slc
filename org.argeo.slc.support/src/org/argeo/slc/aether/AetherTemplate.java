/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.aether;

import java.io.File;
import java.util.List;

import org.argeo.slc.SlcException;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

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

			// FIXME adapt to changes in Aether
			// repositorySystem.resolveDependencies(repositorySystemSession,
			// node,
			// null);
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
