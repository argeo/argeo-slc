package org.argeo.slc.repo.osgi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Session;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.repo.OsgiFactory;
import org.argeo.slc.repo.RepoUtils;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/** Download a software distribution and generates the related OSGi bundles. */
public class DistributionWrapper implements Runnable {
	private final static Log log = LogFactory.getLog(DistributionWrapper.class);

	private OsgiFactory osgiFactory;

	private String version;
	private String groupId;

	private String uri;

	private Map<String, BndWrapper> wrappers = new HashMap<String, BndWrapper>();

	public void run() {

		Session distSession = null;
		Session javaSession = null;
		ZipInputStream zin = null;
		try {
			javaSession = osgiFactory.openJavaSession();
			distSession = osgiFactory.openDistSession();

			if (log.isDebugEnabled())
				log.debug("Wrapping " + uri);

			Node distNode = osgiFactory.getDist(distSession, uri);
			zin = new ZipInputStream(distNode.getNode(Node.JCR_CONTENT)
					.getProperty(Property.JCR_DATA).getBinary().getStream());

			ZipEntry zentry = null;
			ByteArrayOutputStream out = null;
			ByteArrayInputStream in = null;
			while ((zentry = zin.getNextEntry()) != null) {
				try {
					String name = zentry.getName();
					// if (log.isDebugEnabled())
					// log.debug("Scanning " + name);
					if (wrappers.containsKey(name)) {

						BndWrapper wrapper = (BndWrapper) wrappers.get(name);
						if (wrapper.getVersion() == null)
							wrapper.setVersion(version);// FIXME stateful?

						out = new ByteArrayOutputStream((int) zentry.getSize());
						// we must copy since the stream is closed by BND
						byte[] sourceJarBytes = IOUtils.toByteArray(zin);
						in = new ByteArrayInputStream(sourceJarBytes);
						Properties properties = new Properties();
						wrapper.wrapJar(properties, in, out);

						Artifact newArtifact = new DefaultArtifact(groupId,
								wrapper.getBsn(), "jar", wrapper.getVersion());
						Node newJarNode = RepoUtils.copyBytesAsArtifact(
								javaSession.getRootNode(), newArtifact,
								out.toByteArray());
						osgiFactory.indexNode(newJarNode);
						if (log.isDebugEnabled())
							log.debug("Wrapped " + name + " to "
									+ newJarNode.getPath());
					}
				} finally {
					IOUtils.closeQuietly(out);
					IOUtils.closeQuietly(in);
				}
			}
		} catch (Exception e) {
			throw new SlcException("Cannot wrap distribution " + uri, e);
		} finally {
			IOUtils.closeQuietly(zin);
			JcrUtils.logoutQuietly(distSession);
			JcrUtils.logoutQuietly(javaSession);
		}
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setWrappers(Map<String, BndWrapper> wrappers) {
		this.wrappers = wrappers;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setOsgiFactory(OsgiFactory osgiFactory) {
		this.osgiFactory = osgiFactory;
	}

}
