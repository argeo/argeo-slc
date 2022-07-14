import java.nio.file.Path;
import java.nio.file.Paths;

import org.argeo.slc.factory.A2Factory;

class Make {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: <path to a2 output dir>");
			System.exit(1);
		}
		Path a2Base = Paths.get(args[0]).toAbsolutePath().normalize();
		Path descriptorsBase = Paths.get("./tp").toAbsolutePath().normalize();
		A2Factory factory = new A2Factory(a2Base, descriptorsBase, true);

		// SDK
		factory.processCategory(Paths.get("org.argeo.tp.sdk"));

		// Eclipse
		factory.processCategory(Paths.get("osgi/api/org.argeo.tp.osgi"));
		factory.processEclipseArchive(Paths.get("osgi/equinox/org.argeo.tp.osgi", "eclipse-equinox"));
		factory.processEclipseArchive(Paths.get("osgi/equinox/org.argeo.tp.eclipse", "eclipse-equinox"));

		factory.processEclipseArchive(Paths.get("swt/rap/org.argeo.tp.swt", "eclipse-rap"));
		factory.processEclipseArchive(Paths.get("swt/rap/org.argeo.tp.swt.workbench", "eclipse-rap"));

		factory.processEclipseArchive(Paths.get("swt/rcp/org.argeo.tp.swt", "eclipse-rcp"));

		factory.processEclipseArchive(Paths.get("lib/linux/x86_64/swt/rcp/org.argeo.tp.swt", "eclipse-rcp"));
		factory.processEclipseArchive(Paths.get("lib/linux/aarch64/swt/rcp/org.argeo.tp.swt", "eclipse-rcp"));
		factory.processEclipseArchive(Paths.get("lib/win32/x86_64/swt/rcp/org.argeo.tp.swt", "eclipse-rcp"));
		factory.processEclipseArchive(Paths.get("lib/macosx/x86_64/swt/rcp/org.argeo.tp.swt", "eclipse-rcp"));

		factory.processEclipseArchive(Paths.get("swt/rcp/org.argeo.tp.swt", "eclipse-nebula"));
		factory.processEclipseArchive(Paths.get("swt/rcp/org.argeo.tp.swt.workbench", "eclipse-rcp"));
		factory.processCategory(Paths.get("swt/rcp/org.argeo.tp.swt.workbench"));

		// Maven
		factory.processCategory(Paths.get("org.argeo.tp"));
		factory.processCategory(Paths.get("org.argeo.tp.apache"));
		factory.processCategory(Paths.get("org.argeo.tp.jetty"));
		factory.processCategory(Paths.get("org.argeo.tp.jcr"));
		factory.processCategory(Paths.get("org.argeo.tp.formats"));
		factory.processCategory(Paths.get("org.argeo.tp.poi"));
		factory.processCategory(Paths.get("org.argeo.tp.gis"));
	}

}