package org.argeo.slc.repo;

/** Node types used programatically. */
public interface RepoTypes {

	public final static String SLC_ARTIFACT = "slc:artifact";
	public final static String SLC_JAR_FILE = "slc:jarFile";
	public final static String SLC_BUNDLE_ARTIFACT = "slc:bundleArtifact";
	public final static String SLC_OSGI_VERSION = "slc:osgiVersion";
	public final static String SLC_JAVA_PACKAGE = "slc:javaPackage";
	public final static String SLC_EXPORTED_PACKAGE = "slc:exportedPackage";
	public final static String SLC_IMPORTED_PACKAGE = "slc:importedPackage";
	public final static String SLC_DYNAMIC_IMPORTED_PACKAGE = "slc:dynamicImportedPackage";
	public final static String SLC_REQUIRED_BUNDLE = "slc:requiredBundle";
	public final static String SLC_FRAGMENT_HOST = "slc:fragmentHost";
}
