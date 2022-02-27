package org.argeo.slc.repo.osgi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Session;

import org.apache.commons.io.IOUtils;
import org.argeo.api.cms.CmsLog;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.DefaultNameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.repo.OsgiFactory;
import org.argeo.slc.repo.RepoUtils;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

import aQute.bnd.osgi.Jar;

public class UriWrapper extends BndWrapper implements Runnable {
	private final static CmsLog log = CmsLog.getLog(UriWrapper.class);

	private String uri;
	private String baseUri;
	private String versionSeparator = "-";
	private String extension = "jar";

	private OsgiFactory osgiFactory;

	private SourcesProvider sourcesProvider;

	public UriWrapper() {
		setFactory(this);
	}

	public void run() {
		Session distSession = null;
		Session javaSession = null;
		InputStream in = null;
		ByteArrayOutputStream out = null;
		Jar jar = null;
		try {
			distSession = osgiFactory.openDistSession();
			javaSession = osgiFactory.openJavaSession();
			String uri = getEffectiveUri();
//			if (uri == null) {
//				uri = baseUri + '/' + getName() + versionSeparator + getVersion() + "." + extension;
//			}
			Node sourceArtifact = osgiFactory.getDist(distSession, uri);

			// TODO factorize with Maven
			in = sourceArtifact.getNode(Node.JCR_CONTENT).getProperty(Property.JCR_DATA).getBinary().getStream();
			out = new ByteArrayOutputStream();
			wrapJar(in, out);
			Node newJarNode = RepoUtils.copyBytesAsArtifact(javaSession.getRootNode(), getArtifact(),
					out.toByteArray());
			osgiFactory.indexNode(newJarNode);
			newJarNode.getSession().save();
			if (log.isDebugEnabled())
				log.debug("Wrapped " + uri + " to " + newJarNode.getPath());

			// sources
			if (sourcesProvider != null) {
				IOUtils.closeQuietly(in);
				in = new ByteArrayInputStream(out.toByteArray());
				jar = new Jar(null, in);
				List<String> packages = jar.getPackages();

				IOUtils.closeQuietly(out);
				out = new ByteArrayOutputStream();
				sourcesProvider.writeSources(packages, new ZipOutputStream(out));

				IOUtils.closeQuietly(in);
				in = new ByteArrayInputStream(out.toByteArray());
				byte[] sourcesJar = RepoUtils.packageAsPdeSource(in, new DefaultNameVersion(this));
				Artifact sourcesArtifact = new DefaultArtifact(getArtifact().getGroupId(),
						getArtifact().getArtifactId() + ".source", "jar", getArtifact().getVersion());
				Node sourcesJarNode = RepoUtils.copyBytesAsArtifact(javaSession.getRootNode(), sourcesArtifact,
						sourcesJar);
				sourcesJarNode.getSession().save();

				if (log.isDebugEnabled())
					log.debug("Added sources " + sourcesArtifact + " for bundle " + getArtifact());
			}
		} catch (Exception e) {
			throw new SlcException("Cannot wrap URI " + uri, e);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
			JcrUtils.logoutQuietly(distSession);
			JcrUtils.logoutQuietly(javaSession);
			if (jar != null)
				jar.close();
		}
	}

	public void setUri(String sourceCoords) {
		this.uri = sourceCoords;
	}

	public String getEffectiveUri() {
		if (uri == null) {
			return baseUri + '/' + getName() + versionSeparator + getVersion() + "." + extension;
		} else
			return uri;
	}

	public void setOsgiFactory(OsgiFactory osgiFactory) {
		this.osgiFactory = osgiFactory;
	}

	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}

	public void setVersionSeparator(String versionSeparator) {
		this.versionSeparator = versionSeparator;
	}

	public void setSourcesProvider(SourcesProvider sourcesProvider) {
		this.sourcesProvider = sourcesProvider;
	}

	public String getUri() {
		return uri;
	}

	public String getBaseUri() {
		return baseUri;
	}

	public String getVersionSeparator() {
		return versionSeparator;
	}

}
