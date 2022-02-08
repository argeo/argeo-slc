import java.nio.file.Path;
import java.nio.file.Paths;

import org.argeo.slc.factory.A2Factory;

class Make {
	public static void main(String[] args) {
		Path originBase = Paths.get("./output/origin").toAbsolutePath().normalize();
		Path factoryBase = Paths.get("./output/a2").toAbsolutePath().normalize();
		A2Factory factory = new A2Factory(originBase, factoryBase);

		Path descriptorsBase = Paths.get("./tp").toAbsolutePath().normalize();

//		factory.processSingleM2ArtifactDistributionUnit(descriptorsBase.resolve("org.argeo.tp.apache").resolve("org.apache.xml.resolver.bnd"));
//		factory.processM2BasedDistributionUnit(descriptorsBase.resolve("org.argeo.tp/slf4j"));
//		System.exit(0);

		// Eclipse
		factory.processEclipseArchive(
				descriptorsBase.resolve("org.argeo.tp.eclipse.equinox").resolve("eclipse-equinox"));
		factory.processEclipseArchive(descriptorsBase.resolve("org.argeo.tp.eclipse.rap").resolve("eclipse-rap"));
		factory.processEclipseArchive(descriptorsBase.resolve("org.argeo.tp.eclipse.rcp").resolve("eclipse-rcp"));

		// Maven
		factory.processCategory(descriptorsBase.resolve("org.argeo.tp.sdk"));
		factory.processCategory(descriptorsBase.resolve("org.argeo.tp"));
		factory.processCategory(descriptorsBase.resolve("org.argeo.tp.apache"));
		factory.processCategory(descriptorsBase.resolve("org.argeo.tp.jetty"));
		factory.processCategory(descriptorsBase.resolve("org.argeo.tp.jcr"));
	}

}