package org.argeo.slc.repo.osgi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.jar.Manifest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.osgi.framework.Version;

import aQute.lib.osgi.Builder;
import aQute.lib.osgi.Constants;
import aQute.lib.osgi.Jar;

/** Utilities around the BND library, which manipulates OSI meta-data. */
public class BndWrapper implements Constants {
	private final static Log log = LogFactory.getLog(BndWrapper.class);

	private String bsn;
	private String version;

	public void wrapJar(Properties properties, InputStream in, OutputStream out) {
		Builder b = new Builder();
		try {
			Jar jar = new Jar(null, in);

			Manifest sourceManifest = jar.getManifest();
			String sourceVersion = sourceManifest.getMainAttributes().getValue(
					BUNDLE_VERSION);
			Version versionToUse;
			if (version == null && sourceVersion == null) {
				throw new SlcException("A bundle version must be defined.");
			} else if (version == null && sourceVersion != null) {
				versionToUse = new Version(sourceVersion);
			} else if (version != null && sourceVersion == null) {
				versionToUse = new Version(version);
			} else {// both set
				versionToUse = new Version(version);
				Version sv = new Version(sourceVersion);
				if (versionToUse.getMajor() != sv.getMajor()
						|| versionToUse.getMinor() != sv.getMinor()
						|| versionToUse.getMicro() != sv.getMicro()) {
					log.warn("The new version ("
							+ versionToUse
							+ ") is not consistant with the wrapped bundle version ("
							+ sv + ")");
				}
			}

			properties.setProperty(BUNDLE_SYMBOLICNAME, bsn);
			properties.setProperty(BUNDLE_VERSION, versionToUse.toString());

			// b.addIncluded(jarFile);
			b.addClasspath(jar);

			b.setProperties(properties);

			Jar newJar = b.build();
			newJar.write(out);
		} catch (Exception e) {
			throw new SlcException("Cannot wrap jar", e);
		} finally {
			b.close();
		}

	}

	public void setBsn(String bsn) {
		this.bsn = bsn;
	}

	public String getBsn() {
		return bsn;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public static void main(String[] args) {
		BndWrapper bndWrapper = new BndWrapper();
		bndWrapper.setBsn("org.slf4j");

		InputStream in = null;
		InputStream propertiesIn = null;
		OutputStream out = null;
		Properties properties = new Properties();
		File jarFile = new File(
				"/home/mbaudier/dev/work/130129-Distribution/slf4j/slf4j-1.7.5/slf4j-api-1.7.5.jar");
		File propertiesFile = new File(
				"/home/mbaudier/dev/git/git.argeo.org/distribution/bnd/org.slf4j/bnd.bnd");
		try {
			in = new FileInputStream(jarFile);
			// propertiesIn = new FileInputStream(propertiesFile);
			out = new FileOutputStream(new File("test.jar"));
			// properties.load(propertiesIn);
			bndWrapper.wrapJar(properties, in, out);
		} catch (Exception e) {
			throw new SlcException("Cannot test", e);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(propertiesIn);
			IOUtils.closeQuietly(out);
		}
	}
}
