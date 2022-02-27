package org.argeo.slc.repo.osgi;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.jar.Manifest;

import org.apache.commons.io.IOUtils;
import org.argeo.api.cms.CmsLog;
import org.argeo.slc.CategoryNameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.build.Distribution;
import org.argeo.slc.build.License;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.osgi.framework.Version;

import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.Jar;

/** Utilities around the BND library, which manipulates OSGi metadata. */
public class BndWrapper implements Constants, CategoryNameVersion, Distribution {
	private final static CmsLog log = CmsLog.getLog(BndWrapper.class);

	private String groupId;
	private String name;
	private Properties bndProperties = new Properties();

	private String version;
	private License license;

	private Boolean doNotModify = false;

	private Runnable factory = null;

	public void wrapJar(InputStream in, OutputStream out) {
		Builder b = new Builder();
		Jar jar = null;
		try {
			byte[] jarBytes = IOUtils.toByteArray(in);

			jar = new Jar(name, new ByteArrayInputStream(jarBytes));
			Manifest sourceManifest = jar.getManifest();

			Version versionToUse;
			if (sourceManifest != null) {
				// Symbolic name
				String sourceSymbolicName = sourceManifest.getMainAttributes().getValue(BUNDLE_SYMBOLICNAME);
				if (sourceSymbolicName != null && !sourceSymbolicName.equals(name))
					log.info("The new symbolic name (" + name
							+ ") is not consistant with the wrapped bundle symbolic name (" + sourceSymbolicName + ")");

				// Version
				String sourceVersion = sourceManifest.getMainAttributes().getValue(BUNDLE_VERSION);
				if (getVersion() == null && sourceVersion == null) {
					throw new SlcException("A bundle version must be defined.");
				} else if (getVersion() == null && sourceVersion != null) {
					versionToUse = new Version(sourceVersion);
					version = sourceVersion; // set wrapper version
				} else if (getVersion() != null && sourceVersion == null) {
					versionToUse = new Version(getVersion());
				} else {// both set
					versionToUse = new Version(getVersion());
					Version sv = new Version(sourceVersion);
					if (versionToUse.getMajor() != sv.getMajor() || versionToUse.getMinor() != sv.getMinor()
							|| versionToUse.getMicro() != sv.getMicro()) {
						log.warn("The new version (" + versionToUse
								+ ") is not consistant with the wrapped bundle version (" + sv + ")");
					}
				}
			} else {
				versionToUse = new Version(getVersion());
			}

			if (doNotModify) {
				IOUtils.write(jarBytes, out);
				// jar.write(out);
			} else {

				Properties properties = new Properties();
				properties.putAll(bndProperties);
				properties.setProperty(BUNDLE_SYMBOLICNAME, name);
				properties.setProperty(BUNDLE_VERSION, versionToUse.toString());

				// License
				if (license != null) {
					properties.setProperty(BUNDLE_LICENSE, license.toString());
					// TODO add LICENSE.TXT
				} else {
					log.warn("No license set for " + toString());
				}

				// b.addIncluded(jarFile);
				b.addClasspath(jar);

				if (log.isDebugEnabled())
					log.debug(properties);
				b.setProperties(properties);

				Jar newJar = b.build();
				newJar.write(out);
				newJar.close();
			}
		} catch (Exception e) {
			throw new SlcException("Cannot wrap jar", e);
		} finally {
			try {
				b.close();
				if (jar != null)
					jar.close();
			} catch (Exception e) {
				// silent
			}
		}

	}

	public Runnable getFactory() {
		return factory;
	}

	public void setFactory(Runnable factory) {
		if (this.factory != null)
			throw new SlcException("Factory already set on " + name);
		this.factory = factory;
	}

	public void setName(String bsn) {
		this.name = bsn;
	}

	public String getName() {
		return name;
	}

	public void setVersion(String version) {
		if (this.version != null)
			throw new SlcException("Version already set on " + name + " (" + this.version + ")");
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public License getLicense() {
		return license;
	}

	public void setLicense(License license) {
		if (this.license != null)
			throw new SlcException("License already set on " + name);
		this.license = license;
	}

	public Properties getBndProperties() {
		return bndProperties;
	}

	public void setBndProperties(Properties bndProperties) {
		this.bndProperties = bndProperties;
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
		return getCategory() + ":" + getName() + ":" + getVersion();
	}

	public Artifact getArtifact() {
		return new DefaultArtifact(groupId, name, "jar", getVersion());
	}

	@Override
	public String toString() {
		return getDistributionId();
	}

	@Override
	public int hashCode() {
		if (name != null)
			return name.hashCode();
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CategoryNameVersion) {
			CategoryNameVersion cnv = (CategoryNameVersion) obj;
			return getCategory().equals(cnv.getCategory()) && getName().equals(cnv.getName())
					&& getVersion().equals(cnv.getVersion());
		} else
			return false;
	}

	public void setDoNotModify(Boolean doNotModify) {
		this.doNotModify = doNotModify;
	}

	public Boolean getDoNotModify() {
		return doNotModify;
	}

}
