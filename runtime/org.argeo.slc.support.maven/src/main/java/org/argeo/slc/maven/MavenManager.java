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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.cli.MavenCli;
import org.apache.maven.repository.internal.DefaultServiceLocator;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.argeo.slc.SlcException;
import org.codehaus.plexus.PlexusContainer;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.connector.wagon.WagonProvider;
import org.sonatype.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.spi.connector.RepositoryConnectorFactory;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.graph.PreorderNodeListGenerator;

public class MavenManager {
	private final static Log log = LogFactory.getLog(MavenManager.class);

	public void init() {
		try {
			testMaven();
			testAether();
			// List<ComponentDescriptor<?>> lst = plexusContainer
			// .discoverComponents(plexusContainer.getContainerRealm());
			// for (ComponentDescriptor<?> cd : lst) {
			// log.debug(cd);
			// }
			// ArtifactHandler artifactHandler = plexusContainer
			// .lookup(ArtifactHandler.class);
			// ArtifactRepository localRepository = new
			// DefaultArtifactRepositoryFactory().createArtifactRepository("local",
			// , layoutId, snapshots, releases);
			// Maven maven = mavenCli.getContainer().lookup(Maven.class);
			// Artifact artifact = new DefaultArtifact("org.argeo.slc.dist",
			// "org.argeo.slc.sdk", "0.13.1-SNAPSHOT", "compile", "pom",
			// null, artifactHandler);
			// ArtifactResolutionRequest req = new ArtifactResolutionRequest();
			// req.setLocalRepository(localRepository);
			// req.setResolveTransitively(true);
			// req.setArtifact(artifact);
			// ArtifactResolver artifactResolver = plexusContainer
			// .lookup(ArtifactResolver.class);
			// ArtifactResolutionResult res = artifactResolver.resolve(req);
			// Set<Artifact> artifacts = res.getArtifacts();
			// for (Artifact art : artifacts) {
			// log.debug(art);
			// }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testMaven() {
		Thread.currentThread().setContextClassLoader(
				getClass().getClassLoader());
		String[] goals = { "-o", "-e", "-f",
				"/home/mbaudier/dev/src/slc/dist/org.argeo.slc.sdk/pom.xml",
				"dependency:tree" };

		// String m2Home = "/opt/apache-maven-3.0.1";
		// System.setProperty("classworlds.conf", m2Home + "/bin/m2.conf");
		// System.setProperty("maven.home", m2Home);
		//
		// Launcher.main(goals);

		CustomCli mavenCli = new CustomCli();
		mavenCli.doMain(goals,
				"/home/mbaudier/dev/src/slc/dist/org.argeo.slc.sdk",
				System.out, System.err);

		PlexusContainer plexusContainer = mavenCli.getContainer();
		log.debug(plexusContainer.getContext().getContextData());
		plexusContainer.dispose();
	}

	public void testAether() {
		try {
			RepositorySystem repoSystem = newRepositorySystem();

			RepositorySystemSession session = newSession(repoSystem);

			Dependency dependency = new Dependency(new DefaultArtifact(
					"org.argeo.slc.dep:org.argeo.slc.dep.sdk:0.13.1-SNAPSHOT"),
					"compile");
			RemoteRepository argeo = new RemoteRepository("argeo", "default",
					"http://maven.argeo.org/argeo/");
			RemoteRepository argeoSnapshots = new RemoteRepository(
					"argeo-snapshots", "default",
					"http://dev.argeo.org/maven/argeo-snapshots/");

			CollectRequest collectRequest = new CollectRequest();
			collectRequest.setRoot(dependency);
			collectRequest.addRepository(argeo);
			collectRequest.addRepository(argeoSnapshots);
			DependencyNode node = repoSystem.collectDependencies(session,
					collectRequest).getRoot();

			repoSystem.resolveDependencies(session, node, null);

			PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
			node.accept(nlg);

			for (Artifact artifact : nlg.getArtifacts(true)) {
				log.debug(artifact);
			}

			logDependencyNode(0, node);
			// System.out.println(nlg.getClassPath());
			
		} catch (Exception e) {
			throw new SlcException("Cannot resolve", e);
		}

	}

	private void logDependencyNode(int depth, DependencyNode dependencyNode) {
		StringBuffer prefix = new StringBuffer(depth * 2 + 2);
		// prefix.append("|-");
		for (int i = 0; i < depth * 2; i++) {
			prefix.append(' ');
		}
		Artifact artifact = dependencyNode.getDependency().getArtifact();
		log.debug(prefix + "|-> " + artifact.getArtifactId() + " ["
				+ artifact.getVersion() + "]");
		for (DependencyNode child : dependencyNode.getChildren())
			logDependencyNode(depth + 1, child);
	}

	private RepositorySystem newRepositorySystem() {
		try {
			// return new
			// DefaultPlexusContainer().lookup(RepositorySystem.class);

			DefaultServiceLocator locator = new DefaultServiceLocator();

			locator.setServices(WagonProvider.class, new ManualWagonProvider());
			locator.addService(RepositoryConnectorFactory.class,
					WagonRepositoryConnectorFactory.class);

			return locator.getService(RepositorySystem.class);
		} catch (Exception e) {
			throw new SlcException("Cannot lookup repository system", e);
		}
	}

	private RepositorySystemSession newSession(RepositorySystem system) {
		MavenRepositorySystemSession session = new MavenRepositorySystemSession();

		LocalRepository localRepo = new LocalRepository(
				System.getProperty("user.home") + "/.m2/repository");
		session.setLocalRepositoryManager(system
				.newLocalRepositoryManager(localRepo));
		session.setTransferListener(new ConsoleTransferListener(System.out));
		session.setRepositoryListener(new ConsoleRepositoryListener());

		return session;
	}

	public static void main(String[] args) {
		new MavenManager().init();
	}

	static class CustomCli extends MavenCli {
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
	 * private final Log log = LogFactory.getLog(getClass());
	 * 
	 * private String localRepositoryPath = System.getProperty("user.home") +
	 * File.separator + ".m2" + File.separator + "repository";
	 * 
	 * private ArtifactRepository localRepository; private
	 * List<ArtifactRepository> remoteRepositoriesInternal; private
	 * List<RemoteRepository> remoteRepositories = new
	 * Vector<RemoteRepository>();
	 * 
	 * private MavenEmbedder mavenEmbedder; private ClassLoader classLoader;
	 * private Boolean offline = false;
	 * 
	 * public void init() { try { mavenEmbedder = new SlcMavenEmbedder();
	 * mavenEmbedder.setOffline(offline); //
	 * mavenEmbedder.setAlignWithUserInstallation(true); if (classLoader !=
	 * null) mavenEmbedder.setClassLoader(classLoader); else
	 * mavenEmbedder.setClassLoader(getClass().getClassLoader()); // else //
	 * mavenEmbedder.setClassLoader(Thread.currentThread() //
	 * .getContextClassLoader()); mavenEmbedder.start();
	 * 
	 * mavenEmbedder.setLocalRepositoryDirectory(new File(
	 * localRepositoryPath));
	 * 
	 * localRepository = mavenEmbedder.getLocalRepository();
	 * 
	 * remoteRepositoriesInternal = new Vector<ArtifactRepository>(); for
	 * (RemoteRepository remoteRepository : remoteRepositories) {
	 * 
	 * ArtifactRepository repository = mavenEmbedder.createRepository(
	 * remoteRepository.getUrl(), remoteRepository.getId());
	 * remoteRepositoriesInternal.add(repository); }
	 * 
	 * MavenFile mavenFile = new MavenFile();
	 * mavenFile.setGroupId("org.argeo.slc.dist");
	 * mavenFile.setArtifactId("org.argeo.slc.sdk");
	 * mavenFile.setVersion("0.12.2-SNAPSHOT"); mavenFile.setType("pom");
	 * Artifact artifact = resolve(mavenFile); log.debug("Location of " +
	 * artifact + " : " + artifact.getFile()); // log.debug("Dependencies of " +
	 * artifact); // for (Object obj : artifact.getDependencyTrail()) { //
	 * log.debug("  " + obj); // }
	 * 
	 * File pomFile = new File(
	 * "/home/mbaudier/dev/src/slc/dist/org.argeo.slc.sdk/pom.xml");
	 * MavenProject project = mavenEmbedder
	 * .readProjectWithDependencies(pomFile); // MavenProject project =
	 * mavenEmbedder // .readProjectWithDependencies(artifact.getFile());
	 * 
	 * // EventMonitor eventMonitor = new EventMonitor() { // // public void
	 * startEvent(String eventName, String target, // long timestamp) { //
	 * log.debug(eventName + ", " + target + ", " + timestamp); // } // //
	 * public void errorEvent(String eventName, String target, // long
	 * timestamp, Throwable cause) { // log.debug(eventName + ", " + target +
	 * ", " + timestamp); // } // // public void endEvent(String eventName,
	 * String target, // long timestamp) { // log.debug(eventName + ", " +
	 * target + ", " + timestamp); // } // }; // // String[] goals = { "clean",
	 * "install" }; // mavenEmbedder.execute(project, Arrays.asList(goals), //
	 * eventMonitor, // null, null, pomFile.getParentFile());
	 * 
	 * Set<Artifact> transitDeps = getTransitiveProjectDependencies( project,
	 * remoteRepositoriesInternal, localRepository);
	 * log.debug(transitDeps.size() + " dependencies for " + artifact); for
	 * (Object obj : transitDeps) { log.debug("  " + obj); }
	 * 
	 * } catch (Exception e) { throw new
	 * SlcException("Cannot initialize Maven manager", e); } }
	 * 
	 * @SuppressWarnings("unchecked") public Set<Artifact>
	 * getTransitiveProjectDependencies(MavenProject project,
	 * List<ArtifactRepository> remoteRepos, ArtifactRepository local) {
	 * Embedder embedder = mavenEmbedder.getEmbedder(); try { ArtifactFactory
	 * artifactFactory = (ArtifactFactory) embedder
	 * .lookup(ArtifactFactory.ROLE);
	 * 
	 * ArtifactResolver artifactResolver = (ArtifactResolver) embedder
	 * .lookup(ArtifactResolver.ROLE);
	 * 
	 * ArtifactMetadataSource artifactMetadataSource = (ArtifactMetadataSource)
	 * embedder .lookup(ArtifactMetadataSource.ROLE);
	 * 
	 * Set<Artifact> artifacts = project.createArtifacts(artifactFactory, null,
	 * null);
	 * 
	 * ArtifactResolutionResult arr = artifactResolver
	 * .resolveTransitively(artifacts, project.getArtifact(), local,
	 * remoteRepos, artifactMetadataSource, null);
	 * 
	 * return arr.getArtifacts(); } catch (Exception e) { throw new
	 * SlcException("Cannot resolve dependency for " + project, e); } // Order,
	 * just for display // Set dependencies = new TreeSet(new
	 * ArtifactComparator()); // dependencies.addAll(arr.getArtifacts()); //
	 * return dependencies; }
	 * 
	 * private Artifact resolve(MavenFile mavenDistribution) { try { Artifact
	 * artifact; if (mavenDistribution.getClassifier() == null) { artifact =
	 * mavenEmbedder.createArtifact(mavenDistribution .getGroupId(),
	 * mavenDistribution.getArtifactId(), mavenDistribution.getVersion(),
	 * Artifact.SCOPE_PROVIDED, mavenDistribution.getType()); } else { artifact
	 * = mavenEmbedder.createArtifactWithClassifier(
	 * mavenDistribution.getGroupId(), mavenDistribution .getArtifactId(),
	 * mavenDistribution .getVersion(), mavenDistribution.getType(),
	 * mavenDistribution.getClassifier()); }
	 * 
	 * mavenEmbedder.resolve(artifact, remoteRepositoriesInternal,
	 * localRepository);
	 * 
	 * return artifact; } catch (Exception e) { throw new
	 * SlcException("Cannot resolve artifact.", e); } }
	 * 
	 * public File getPackageLocation(MavenFile mavenDistribution) { return
	 * resolve(mavenDistribution).getFile(); }
	 * 
	 * public void destroy() { try { if (mavenEmbedder != null) {
	 * mavenEmbedder.stop(); } } catch (MavenEmbedderException e) {
	 * log.error("Cannot destroy Maven manager", e); } }
	 * 
	 * public void setLocalRepositoryPath(String localRepositoryPath) {
	 * this.localRepositoryPath = localRepositoryPath; }
	 * 
	 * public List<RemoteRepository> getRemoteRepositories() { return
	 * remoteRepositories; }
	 * 
	 * public void setRemoteRepositories(List<RemoteRepository>
	 * remoteRepositories) { this.remoteRepositories = remoteRepositories; }
	 * 
	 * public void setClassLoader(ClassLoader classLoader) { this.classLoader =
	 * classLoader; }
	 * 
	 * public void setOffline(Boolean offline) { this.offline = offline; }
	 */
}
