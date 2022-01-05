package org.argeo.slc.repo;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.io.FilenameUtils;
import org.argeo.api.cms.CmsLog;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.NameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.repo.maven.AetherUtils;
import org.argeo.slc.repo.maven.MavenConventionsUtils;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;

/**
 * Creates pde sources from a source {@link Artifact} with name
 * "...-sources.jar"
 */
public class PdeSourcesIndexer implements NodeIndexer {
	private CmsLog log = CmsLog.getLog(PdeSourcesIndexer.class);

	private String artifactBasePath = RepoConstants.DEFAULT_ARTIFACTS_BASE_PATH;

	// private ArtifactIndexer artifactIndexer;
	// private JarFileIndexer jarFileIndexer;

	// public PdeSourcesIndexer(){
	// // ArtifactIndexer artifactIndexer,
	// // JarFileIndexer jarFileIndexer) {
	// // this.artifactIndexer = artifactIndexer;
	// // this.jarFileIndexer = jarFileIndexer;
	// }

	public Boolean support(String path) {
		// TODO implement clean management of same name siblings
		String name = FilenameUtils.getBaseName(path);
		// int lastInd = name.lastIndexOf("[");
		// if (lastInd != -1)
		// name = name.substring(0, lastInd);
		return name.endsWith("-sources") && FilenameUtils.getExtension(path).equals("jar");
	}

	public void index(Node sourcesNode) {
		try {
			if (!support(sourcesNode.getPath()))
				return;

			packageSourcesAsPdeSource(sourcesNode);
		} catch (Exception e) {
			throw new SlcException("Cannot generate pde sources for node " + sourcesNode, e);
		}
	}

	protected void packageSourcesAsPdeSource(Node sourcesNode) {
		Binary origBinary = null;
		Binary osgiBinary = null;
		try {
			Session session = sourcesNode.getSession();
			Artifact sourcesArtifact = AetherUtils.convertPathToArtifact(sourcesNode.getPath(), null);

			// read name version from manifest
			Artifact osgiArtifact = new DefaultArtifact(sourcesArtifact.getGroupId(), sourcesArtifact.getArtifactId(),
					sourcesArtifact.getExtension(), sourcesArtifact.getVersion());
			String osgiPath = MavenConventionsUtils.artifactPath(artifactBasePath, osgiArtifact);
			osgiBinary = session.getNode(osgiPath).getNode(Node.JCR_CONTENT).getProperty(Property.JCR_DATA).getBinary();

			NameVersion nameVersion = RepoUtils.readNameVersion(osgiBinary.getStream());
			if (nameVersion == null) {
				log.warn("Cannot package PDE sources for " + osgiPath + " as it is probably not an OSGi bundle");
				return;
			}

			// create PDe sources artifact
			Artifact pdeSourceArtifact = new DefaultArtifact(sourcesArtifact.getGroupId(),
					sourcesArtifact.getArtifactId() + ".source", sourcesArtifact.getExtension(),
					sourcesArtifact.getVersion());
			String targetSourceParentPath = MavenConventionsUtils.artifactParentPath(artifactBasePath,
					pdeSourceArtifact);
			String targetSourceFileName = MavenConventionsUtils.artifactFileName(pdeSourceArtifact);
			// String targetSourceJarPath = targetSourceParentPath + '/'
			// + targetSourceFileName;

			Node targetSourceParentNode = JcrUtils.mkfolders(session, targetSourceParentPath);
			origBinary = sourcesNode.getNode(Node.JCR_CONTENT).getProperty(Property.JCR_DATA).getBinary();
			byte[] targetJarBytes = RepoUtils.packageAsPdeSource(origBinary.getStream(), nameVersion);
			JcrUtils.copyBytesAsFile(targetSourceParentNode, targetSourceFileName, targetJarBytes);

			// reindex
			// Automagically done via the various listeners or manually
			// triggered.
			// Node targetSourceJarNode = session.getNode(targetSourceJarPath);
			// artifactIndexer.index(targetSourceJarNode);
			// jarFileIndexer.index(targetSourceJarNode);
			if (log.isTraceEnabled())
				log.trace("Created pde source artifact " + pdeSourceArtifact + " from " + sourcesNode);

		} catch (RepositoryException e) {
			throw new SlcException("Cannot add PDE sources for " + sourcesNode, e);
		} finally {
			JcrUtils.closeQuietly(origBinary);
			JcrUtils.closeQuietly(osgiBinary);
		}
	}
}