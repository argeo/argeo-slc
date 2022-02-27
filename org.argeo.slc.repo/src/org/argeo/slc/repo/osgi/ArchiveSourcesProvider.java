package org.argeo.slc.repo.osgi;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Session;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.argeo.api.cms.CmsLog;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.repo.OsgiFactory;

public class ArchiveSourcesProvider implements SourcesProvider {
	private final static CmsLog log = CmsLog.getLog(ArchiveSourcesProvider.class);

	private OsgiFactory osgiFactory;
	private String uri;
	private String base = "";

	@Override
	public void writeSources(List<String> packages, ZipOutputStream zout) {
		Session distSession = null;
		ZipInputStream zin = null;
		try {
			distSession = osgiFactory.openDistSession();

			if (log.isDebugEnabled())
				log.debug("Wrapping " + uri);

			Node distNode = osgiFactory.getDist(distSession, uri);
			zin = new ZipInputStream(
					distNode.getNode(Node.JCR_CONTENT).getProperty(Property.JCR_DATA).getBinary().getStream());

			// prepare
			Set<String> directories = new TreeSet<String>();
			for (String pkg : packages)
				if (!pkg.equals("META-INF"))
					directories.add(base + pkg.replace('.', '/') + '/');

			ZipEntry zentry = null;
			entries: while ((zentry = zin.getNextEntry()) != null) {
				String name = zentry.getName();
				if (!name.startsWith(base))
					continue entries;

				String dirPath = FilenameUtils.getPath(name);
				if (name.equals(dirPath))// directory
					continue entries;

				if (directories.contains(dirPath)) {
					String path = name.substring(base.length());
					zout.putNextEntry(new JarEntry(path));
					IOUtils.copy(zin, zout);
					zin.closeEntry();
					zout.closeEntry();
					continue entries;
				}
			}
		} catch (Exception e) {
			throw new SlcException("Cannot retrieve sources from " + uri, e);
		} finally {
			IOUtils.closeQuietly(zin);
			JcrUtils.logoutQuietly(distSession);
		}

	}

	public void setOsgiFactory(OsgiFactory osgiFactory) {
		this.osgiFactory = osgiFactory;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setBase(String base) {
		this.base = base;
	}

}
