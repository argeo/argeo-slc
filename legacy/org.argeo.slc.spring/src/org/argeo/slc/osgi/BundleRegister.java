package org.argeo.slc.osgi;

/** <b>Experimental</b> A structured set of OSGi bundles. */
public interface BundleRegister {
	/**
	 * @param pkg
	 *            the Java package
	 * @param version
	 *            the version, can be only major.minor or null
	 * @return the bundle providing this package or null if none was found
	 */
	public String bundleProvidingPackage(String pkg, String version);
}
