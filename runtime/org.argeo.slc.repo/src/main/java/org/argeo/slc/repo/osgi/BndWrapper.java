package org.argeo.slc.repo.osgi;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.jar.Manifest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.CategorizedNameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.build.Distribution;
import org.osgi.framework.Version;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.springframework.beans.factory.BeanNameAware;

import aQute.lib.osgi.Builder;
import aQute.lib.osgi.Constants;
import aQute.lib.osgi.Jar;

/** Utilities around the BND library, which manipulates OSGi metadata. */
public class BndWrapper implements Constants, CategorizedNameVersion,
		Distribution, BeanNameAware {
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

			Version versionToUse;
			if (sourceManifest != null) {
				String sourceSymbolicName = sourceManifest.getMainAttributes()
						.getValue(BUNDLE_SYMBOLICNAME);
				if (sourceSymbolicName != null
						&& sourceSymbolicName.equals(name))
					log.warn("The new symbolic name ("
							+ name
							+ ") is not consistant with the wrapped bundle symbolic name ("
							+ sourceSymbolicName + ")");

				String sourceVersion = sourceManifest.getMainAttributes()
						.getValue(BUNDLE_VERSION);
				if (version == null && sourceVersion == null) {
					throw new SlcException("A bundle version must be defined.");
				} else if (version == null && sourceVersion != null) {
					versionToUse = new Version(sourceVersion);
					version = sourceVersion; // set wrapper version
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
			} else {
				versionToUse = new Version(version);
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

	public String getCategory() {
		return getGroupId();
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getDistributionId() {
		return getArtifact().toString();
	}

	public Artifact getArtifact() {
		return new DefaultArtifact(groupId, name, "jar", version);
	}

	@Override
	public String toString() {
		return getArtifact().toString();
	}

	@Override
	public int hashCode() {
		return getArtifact().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CategorizedNameVersion) {
			CategorizedNameVersion cnv = (CategorizedNameVersion) obj;
			return getCategory().equals(cnv.getCategory())
					&& getName().equals(cnv.getName())
					&& getVersion().equals(cnv.getVersion());
		} else
			return false;
	}

}