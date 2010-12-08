/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

package org.argeo.slc.maven;

import org.apache.maven.cli.MavenCli;
import org.codehaus.plexus.PlexusContainer;



public class MavenManager {
	
	public static void main(String[] args){
		//CustomCli mavenCli = new CustomCli();
		MavenCli mavenCli = new MavenCli();
		String[] goals = { "-e","dependency:tree" };
		mavenCli.doMain(goals, "/home/mbaudier/dev/src/slc/dist/org.argeo.slc.sdk", System.out, System.err);
	}
	
	static class CustomCli extends MavenCli{
		private PlexusContainer container;

		@Override
		protected void customizeContainer(PlexusContainer container) {
			this.container = container;
		}

		public PlexusContainer getContainer() {
			return container;
		}
		
		
		
	}
/*
	private final Log log = LogFactory.getLog(getClass());

	private String localRepositoryPath = System.getProperty("user.home")
			+ File.separator + ".m2" + File.separator + "repository";

	private ArtifactRepository localRepository;
	private List<ArtifactRepository> remoteRepositoriesInternal;
	private List<RemoteRepository> remoteRepositories = new Vector<RemoteRepository>();

	private MavenEmbedder mavenEmbedder;
	private ClassLoader classLoader;
	private Boolean offline = false;

	public void init() {
		try {
			mavenEmbedder = new SlcMavenEmbedder();
			mavenEmbedder.setOffline(offline);
			// mavenEmbedder.setAlignWithUserInstallation(true);
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

			File pomFile = new File(
					"/home/mbaudier/dev/src/slc/dist/org.argeo.slc.sdk/pom.xml");
			MavenProject project = mavenEmbedder
					.readProjectWithDependencies(pomFile);
			// MavenProject project = mavenEmbedder
			// .readProjectWithDependencies(artifact.getFile());

			// EventMonitor eventMonitor = new EventMonitor() {
			//
			// public void startEvent(String eventName, String target,
			// long timestamp) {
			// log.debug(eventName + ", " + target + ", " + timestamp);
			// }
			//
			// public void errorEvent(String eventName, String target,
			// long timestamp, Throwable cause) {
			// log.debug(eventName + ", " + target + ", " + timestamp);
			// }
			//
			// public void endEvent(String eventName, String target,
			// long timestamp) {
			// log.debug(eventName + ", " + target + ", " + timestamp);
			// }
			// };
			//
			// String[] goals = { "clean", "install" };
			// mavenEmbedder.execute(project, Arrays.asList(goals),
			// eventMonitor,
			// null, null, pomFile.getParentFile());

			Set<Artifact> transitDeps = getTransitiveProjectDependencies(
					project, remoteRepositoriesInternal, localRepository);
			log.debug(transitDeps.size() + " dependencies for " + artifact);
			for (Object obj : transitDeps) {
				log.debug("  " + obj);
			}

		} catch (Exception e) {
			throw new SlcException("Cannot initialize Maven manager", e);
		}
	}

	@SuppressWarnings("unchecked")
	public Set<Artifact> getTransitiveProjectDependencies(MavenProject project,
			List<ArtifactRepository> remoteRepos, ArtifactRepository local) {
		Embedder embedder = mavenEmbedder.getEmbedder();
		try {
			ArtifactFactory artifactFactory = (ArtifactFactory) embedder
					.lookup(ArtifactFactory.ROLE);

			ArtifactResolver artifactResolver = (ArtifactResolver) embedder
					.lookup(ArtifactResolver.ROLE);

			ArtifactMetadataSource artifactMetadataSource = (ArtifactMetadataSource) embedder
					.lookup(ArtifactMetadataSource.ROLE);

			Set<Artifact> artifacts = project.createArtifacts(artifactFactory,
					null, null);

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
*/
}
