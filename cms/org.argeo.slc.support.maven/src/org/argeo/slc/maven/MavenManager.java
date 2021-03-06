package org.argeo.slc.maven;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.aether.AetherUtils;
import org.argeo.slc.aether.ConsoleRepositoryListener;
import org.argeo.slc.aether.ConsoleTransferListener;
import org.codehaus.plexus.PlexusContainer;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator;

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
			RepositorySystem repoSystem = createRepositorySystem();

			RepositorySystemSession session = createRepositorySystemSession(repoSystem);

			Dependency dependency = new Dependency(new DefaultArtifact(
					"org.argeo.slc.dep:org.argeo.slc.dep.sdk:0.13.1-SNAPSHOT"),
					"compile");
//			RemoteRepository argeo = new RemoteRepository("argeo", "default",
//					"http://maven.argeo.org/argeo/");
//			RemoteRepository argeoSnapshots = new RemoteRepository(
//					"argeo-snapshots", "default",
//					"http://dev.argeo.org/maven/argeo-snapshots/");
			RemoteRepository argeo =null;
			RemoteRepository argeoSnapshots =null;

			CollectRequest collectRequest = new CollectRequest();
			collectRequest.setRoot(dependency);
			collectRequest.addRepository(argeo);
			collectRequest.addRepository(argeoSnapshots);
			DependencyNode node = repoSystem.collectDependencies(session,
					collectRequest).getRoot();

//			repoSystem.resolveDependencies(session, node, null);

			PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
			node.accept(nlg);

			for (Artifact artifact : nlg.getArtifacts(true)) {
				log.debug(artifact);
			}

			AetherUtils.logDependencyNode(0, node);
			// System.out.println(nlg.getClassPath());

		} catch (Exception e) {
			throw new SlcException("Cannot resolve", e);
		}

	}

	/** Creates a Maven {@link RepositorySystem}. */
	public static RepositorySystem createRepositorySystem() {
		try {
			DefaultServiceLocator locator = new DefaultServiceLocator();

//			locator.setServices(WagonProvider.class, new ManualWagonProvider());
//			locator.addService(RepositoryConnectorFactory.class,
//					WagonRepositoryConnectorFactory.class);

			return locator.getService(RepositorySystem.class);
		} catch (Exception e) {
			throw new SlcException("Cannot lookup repository system", e);
		}
	}

	public static RepositorySystemSession createRepositorySystemSession(
			RepositorySystem system) {
//		MavenRepositorySystemSession session = new MavenRepositorySystemSession();
	       DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
	       
		LocalRepository localRepo = new LocalRepository(
				System.getProperty("user.home") + "/.m2/repository");
        session.setLocalRepositoryManager( system.newLocalRepositoryManager( session, localRepo ) );
//		session.setLocalRepositoryManager(system
//				.newLocalRepositoryManager(localRepo));
		session.setTransferListener(new ConsoleTransferListener(System.out));
		session.setRepositoryListener(new ConsoleRepositoryListener());
		return session;
	}

	public static void main(String[] args) {
		new MavenManager().init();
	}

}
