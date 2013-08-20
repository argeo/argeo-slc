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
import org.argeo.slc.NameVersion;
import org.argeo.slc.SlcException;
import org.osgi.framework.Version;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.springframework.beans.factory.BeanNameAware;

import aQute.lib.osgi.Builder;
import aQute.lib.osgi.Constants;
import aQute.lib.osgi.Jar;

/** Utilities around the BND library, which manipulates OSI meta-data. */
public class BndWrapper implements Constants, NameVersion, BeanNameAware {
	private final static Log log = LogFactory.getLog(BndWrapper.class);

	private String groupId;
	private String name;
	private String version;
	private Properties bndProperties = new Properties();

	public void wrapJar(InputStream in, OutputStream out) {
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

			Properties properties = new Properties();
			properties.putAll(bndProperties);
			properties.setProperty(BUNDLE_SYMBOLICNAME, name);
			properties.setProperty(BUNDLE_VERSION, versionToUse.toString());

			// b.addIncluded(jarFile);
			b.addClasspath(jar);

			log.debug(properties);
			b.setProperties(properties);

			Jar newJar = b.build();
			newJar.write(out);
		} catch (Exception e) {
			throw new SlcException("Cannot wrap jar", e);
		} finally {
			b.close();
		}

	}

	public void setName(String bsn) {
		this.name = bsn;
	}

	public String getName() {
		return name;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public Properties getBndProperties() {
		return bndProperties;
	}

	public void setBndProperties(Properties bndProperties) {
		this.bndProperties = bndProperties;
	}

	public void setBeanName(String name) {
		this.name = name;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public Artifact getArtifact() {
		return new DefaultArtifact(groupId, name, "jar", version);
	}

	public static void main(String[] args) {
		BndWrapper bndWrapper = new BndWrapper();
		bndWrapper.setName("org.slf4j");

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
			bndWrapper.wrapJar(in, out);
		} catch (Exception e) {
			throw new SlcException("Cannot test", e);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(propertiesIn);
			IOUtils.closeQuietly(out);
		}
	}
}
