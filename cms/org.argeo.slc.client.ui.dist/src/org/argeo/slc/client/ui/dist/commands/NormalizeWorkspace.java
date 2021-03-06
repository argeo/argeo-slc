package org.argeo.slc.client.ui.dist.commands;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.jcr.util.TraversingItemVisitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.api.security.Keyring;
import org.argeo.eclipse.ui.EclipseJcrMonitor;
import org.argeo.jcr.JcrMonitor;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.repo.ArtifactIndexer;
import org.argeo.slc.repo.JarFileIndexer;
import org.argeo.slc.repo.ModularDistributionIndexer;
import org.argeo.slc.repo.PdeSourcesIndexer;
import org.argeo.slc.repo.RepoConstants;
import org.argeo.slc.repo.RepoUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Force the indexing of a given workspace by making sure than Maven and OSGi
 * metadata are consistent. This mechanism normally relies on JCR Listeners but
 * must sometimes be triggered manually
 */
public class NormalizeWorkspace extends AbstractHandler implements SlcNames {
	private final static Log log = LogFactory.getLog(NormalizeWorkspace.class);

	public final static String ID = DistPlugin.PLUGIN_ID + ".normalizeWorkspace";
	public final static ImageDescriptor DEFAULT_ICON = DistPlugin.getImageDescriptor("icons/normalize.gif");

	public final static String PARAM_WORKSPACE_NAME = "workspaceName";
	public final static String PARAM_TARGET_REPO_PATH = "targetRepoPath";

	private String artifactBasePath = RepoConstants.DEFAULT_ARTIFACTS_BASE_PATH;

	// DEPENDENCY INJECTION
	private RepositoryFactory repositoryFactory;
	private Keyring keyring;
	private Repository repository;

	// Relevant default node indexers
	private PdeSourcesIndexer pdeSourceIndexer = new PdeSourcesIndexer();
	// WARNING Order is important: must be called in the following order.
	private ModularDistributionIndexer modularDistributionIndexer = new ModularDistributionIndexer();
	private JarFileIndexer jarFileIndexer = new JarFileIndexer();
	private ArtifactIndexer artifactIndexer = new ArtifactIndexer();

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String targetRepoPath = event.getParameter(PARAM_TARGET_REPO_PATH);
		String wkspName = event.getParameter(PARAM_WORKSPACE_NAME);

		Session currSession = null;
		NormalizeJob job;
		try {
			String msg = "Your are about to normalize workspace: " + wkspName
					+ ".\nThis will index OSGi bundles and Maven artifacts, "
					+ "it will also convert Maven sources to PDE Sources if needed.\n"
					+ "Note that no information will be overwritten: " + "all existing information are kept."
					+ "\n\n Do you really want to proceed ?";

			if (!MessageDialog.openConfirm(DistPlugin.getDefault().getWorkbench().getDisplay().getActiveShell(),
					"Confirm workspace normalization", msg))
				return null;

			currSession = repository.login();
			Node repoNode = currSession.getNode(targetRepoPath);
			Repository repository = RepoUtils.getRepository(repositoryFactory, keyring, repoNode);
			Credentials credentials = RepoUtils.getRepositoryCredentials(keyring, repoNode);

			job = new NormalizeJob(repository.login(credentials, wkspName));
			job.setUser(true);
			job.schedule();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot normalize " + wkspName, e);
		} finally {
			JcrUtils.logoutQuietly(currSession);
		}
		return null;
	}

	private class NormalizeJob extends Job {
		private Session session;

		public NormalizeJob(Session session) {
			super("Normalize Distribution");
			this.session = session;
		}

		@Override
		protected IStatus run(IProgressMonitor progressMonitor) {
			try {
				JcrMonitor monitor = new EclipseJcrMonitor(progressMonitor);
				// Normalize artifacts
				Query countQuery = session.getWorkspace().getQueryManager()
						.createQuery("select file from [nt:file] as file", Query.JCR_SQL2);
				QueryResult result = countQuery.execute();
				Long expectedCount = result.getNodes().getSize();
				monitor.beginTask("Normalize artifacts of " + session.getWorkspace().getName(),
						expectedCount.intValue());
				NormalizingTraverser tiv = new NormalizingTraverser(monitor);
				Node artifactBaseNode = session.getNode(artifactBasePath);
				artifactBaseNode.accept(tiv);
			} catch (Exception e) {
				log.error("Error normalizing workspace " + session.getWorkspace().getName() + ": " + e.getMessage());
				if (log.isErrorEnabled())
					e.printStackTrace();
				return new Status(IStatus.ERROR, DistPlugin.PLUGIN_ID,
						"Cannot normalize distribution " + session.getWorkspace().getName(), e);
			} finally {
				JcrUtils.logoutQuietly(session);
			}
			return Status.OK_STATUS;
		}
	}

	private class NormalizingTraverser extends TraversingItemVisitor {
		JcrMonitor monitor;

		public NormalizingTraverser(JcrMonitor monitor) {
			super();
			this.monitor = monitor;
		}

		@Override
		protected void entering(Property property, int level) throws RepositoryException {
		}

		@Override
		protected void entering(Node node, int level) throws RepositoryException {
			if (node.getPath().startsWith(RepoConstants.DIST_DOWNLOAD_BASEPATH))
				return;

			if (node.isNodeType(NodeType.NT_FILE)) {
				if (node.getName().endsWith("-sources.jar")) {
					monitor.subTask(node.getName());
					pdeSourceIndexer.index(node);
					node.getSession().save();
					monitor.worked(1);
					if (log.isDebugEnabled())
						log.debug("Processed source artifact " + node.getPath());
				} else if (node.getName().endsWith("-javadoc.jar")) {
					if (log.isDebugEnabled())
						log.debug("Skip indexing of Javadoc jar " + node.getPath());
				} else if (node.getName().endsWith(".jar")) {
					if (jarFileIndexer.support(node.getPath()))
						if (artifactIndexer.support(node.getPath())) {
							monitor.subTask(node.getName());
							modularDistributionIndexer.index(node);
							jarFileIndexer.index(node);
							artifactIndexer.index(node);
							if (node.getSession().hasPendingChanges()) {
								node.getSession().save();
								if (log.isDebugEnabled())
									log.debug("Processed jar artifact " + node.getPath());
							}
							monitor.worked(1);
						}
				} else if (node.getName().endsWith(".pom")) {
					// Removed: we do not support binaries concept anymore.
					// if (distBundleIndexer.support(node.getPath()))
					// distBundleIndexer.index(node);
					if (artifactIndexer.support(node.getPath()))
						artifactIndexer.index(node);
					if (node.getSession().hasPendingChanges()) {
						node.getSession().save();
						if (log.isDebugEnabled())
							log.debug("Processed pom artifact " + node.getPath());
					}
					monitor.worked(1);
				} else {
					monitor.worked(1);
				}
			}
		}

		@Override
		protected void leaving(Property property, int level) throws RepositoryException {
		}

		@Override
		protected void leaving(Node node, int level) throws RepositoryException {
		}
	}

	/* DEPENDENCY INJECTION */
	public void setNodeRepository(Repository nodeRepository) {
		this.repository = nodeRepository;
	}

	public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
		this.repositoryFactory = repositoryFactory;
	}

	public void setKeyring(Keyring keyring) {
		this.keyring = keyring;
	}
}