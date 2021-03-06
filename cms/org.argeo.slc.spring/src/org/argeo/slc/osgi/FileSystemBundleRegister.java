package org.argeo.slc.osgi;

import java.io.File;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Constants;

/** <b>Experimental</b> */
public class FileSystemBundleRegister implements BundleRegister {
	private final static Log log = LogFactory
			.getLog(FileSystemBundleRegister.class);
	private Properties packagesBundles = null;

	public String bundleProvidingPackage(String pkg, String version) {
		if (packagesBundles == null)
			return null;
		return packagesBundles.getProperty(pkg);
	}

	protected void scan(File baseDirectory) {
		long begin = System.currentTimeMillis();
		int bundleCount = 0;
		int packageCount = 0;

		packagesBundles = new Properties();

		File[] files = baseDirectory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {

			} else {
				JarFile jarFile = null;
				try {
					jarFile = new JarFile(file);
					Manifest manifest = jarFile.getManifest();
					String symbolicName = manifest.getMainAttributes()
							.getValue(Constants.BUNDLE_SYMBOLICNAME);
					String exportPackage = manifest.getMainAttributes()
							.getValue(Constants.EXPORT_PACKAGE);

					// List exported packages
					Set<String> exportedPackages = exportPackageToPackageNames(exportPackage);

					for (String exportedPackage : exportedPackages) {
						packagesBundles.put(exportedPackage, symbolicName);
						packageCount++;
						if (log.isTraceEnabled())
							log.trace("Register " + exportedPackage + "="
									+ symbolicName);
					}
					bundleCount++;
				} catch (Exception e) {
					log.warn("Cannot scan " + file, e);
					if (log.isTraceEnabled())
						e.printStackTrace();
				} finally {
					IOUtils.closeQuietly(jarFile);
				}
			}
		}
		if (log.isDebugEnabled())
			log.debug("Scanned " + bundleCount + " bundles with "
					+ packageCount + " packages in "
					+ (System.currentTimeMillis() - begin) + " ms");
	}

	protected Set<String> exportPackageToPackageNames(String exportPackage) {
		Set<String> exportedPackages = new HashSet<String>();
		if (exportPackage == null)
			return exportedPackages;
		char[] arr = exportPackage.toCharArray();

		StringBuffer currentPkg = new StringBuffer("");
		boolean skip = false;
		boolean inQuote = false;
		for (char c : arr) {
			if (c == ' ' || c == '\n') {
				// ignore
			} else if (c == ';') {
				if (!skip)
					skip = true;
			} else if (c == ',') {
				if (skip && !inQuote) {
					skip = false;
					// add new package
					exportedPackages.add(currentPkg.toString());
					currentPkg = new StringBuffer("");
				}
			} else if (c == '\"') {
				inQuote = inQuote ? false : true;
			} else {
				if (!skip)
					currentPkg.append(c);
			}
		}

		return exportedPackages;
	}

	public static void main(String[] args) {
		FileSystemBundleRegister fsbr = new FileSystemBundleRegister();
		fsbr.scan(new File(
				"/home/mbaudier/dev/src/slc/dist/org.argeo.slc.sdk/target/lib"));

	}
}
