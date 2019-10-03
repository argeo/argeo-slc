/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.client.ui.dist.commands;

import javax.jcr.Binary;
import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
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
import org.argeo.eclipse.ui.EclipseJcrMonitor;
import org.argeo.jcr.JcrMonitor;
import org.argeo.jcr.JcrUtils;
import org.argeo.node.security.Keyring;
import org.argeo.slc.NameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.repo.ArtifactIndexer;
import org.argeo.slc.repo.JarFileIndexer;
import org.argeo.slc.repo.RepoConstants;
import org.argeo.slc.repo.RepoUtils;
import org.argeo.slc.repo.maven.AetherUtils;
import org.argeo.slc.repo.maven.MavenConventionsUtils;
import org.argeo.slc.repo.osgi.NormalizeGroup;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.HandlerUtil;

/** Legacy - Make sure than Maven and OSGi metadata are consistent */
public class NormalizeDistribution extends AbstractHandler implements SlcNames {
	private final static Log log = LogFactory
			.getLog(NormalizeDistribution.class);

	public final static String ID = DistPlugin.PLUGIN_ID
			+ ".normalizeDistribution";
	public final static String DEFAULT_LABEL = "Legacy Normalization...";
	public final static ImageDescriptor DEFAULT_ICON = DistPlugin
			.getImageDescriptor("icons/normalize.gif");

	public final static String PARAM_WORKSPACE_NAME = "workspaceName";
	public final static String PARAM_TARGET_REPO_PATH = "targetRepoPath";

	private String artifactBasePath = RepoConstants.DEFAULT_ARTIFACTS_BASE_PATH;

	private ArtifactIndexer artifactIndexer = new ArtifactIndexer();
	private JarFileIndexer jarFileIndexer = new JarFileIndexer();

	// DEPENDENCY INJECTION
	private RepositoryFactory repositoryFactory;
	private Keyring keyring;
	private Repository nodeRepository;

	public Object execute(ExecutionEvent event) throws ExecutionException {

		String targetRepoPath = event.getParameter(PARAM_TARGET_REPO_PATH);
		String wkspName = event.getParameter(PARAM_WORKSPACE_NAME);

		Session nodeSession = null;
		NormalizeJob job;
		try {

			NormalizationDialog dialog = new NormalizationDialog(
					HandlerUtil.getActiveShell(event));
			if (dialog.open() != Dialog.OK)
				return null;

			nodeSession = nodeRepository.login();
			Node repoNode = nodeSession.getNode(targetRepoPath);
			Repository repository = RepoUtils.getRepository(repositoryFactory,
					keyring, repoNode);
			Credentials credentials = RepoUtils.getRepositoryCredentials(
					keyring, repoNode);

			String version = dialog.getVersion();
			Boolean overridePoms = dialog.getOverridePoms();

			job = new NormalizeJob(repository.login(credentials, wkspName),
					version, overridePoms);
			job.setUser(true);
			job.schedule();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot normalize " + wkspName, e);
		} finally {
			JcrUtils.logoutQuietly(nodeSession);
		}
		return null;
	}

	protected void packageSourcesAsPdeSource(Node sourcesNode) {
		Binary origBinary = null;
		Binary osgiBinary = null;
		try {
			Session session = sourcesNode.getSession();
			Artifact sourcesArtifact = AetherUtils.convertPathToArtifact(
					sourcesNode.getPath(), null);

			// read name version from manifest
			Artifact osgiArtifact = new DefaultArtifact(
					sourcesArtifact.getGroupId(),
					sourcesArtifact.getArtifactId(),
					sourcesArtifact.getExtension(),
					sourcesArtifact.getVersion());
			String osgiPath = MavenConventionsUtils.artifactPath(
					artifactBasePath, osgiArtifact);
			osgiBinary = session.getNode(osgiPath).getNode(Node.JCR_CONTENT)
					.getProperty(Property.JCR_DATA).getBinary();

			NameVersion nameVersion = RepoUtils.readNameVersion(osgiBinary
					.getStream());

			// create PDe sources artifact
			Artifact pdeSourceArtifact = new DefaultArtifact(
					sourcesArtifact.getGroupId(),
					sourcesArtifact.getArtifactId() + ".source",
					sourcesArtifact.getExtension(),
					sourcesArtifact.getVersion());
			String targetSourceParentPath = MavenConventionsUtils
					.artifactParentPath(artifactBasePath, pdeSourceArtifact);
			String targetSourceFileName = MavenConventionsUtils
					.artifactFileName(pdeSourceArtifact);
			String targetSourceJarPath = targetSourceParentPath + '/'
					+ targetSourceFileName;

			Node targetSourceParentNode = JcrUtils.mkfolders(session,
					targetSourceParentPath);
			origBinary = sourcesNode.getNode(Node.JCR_CONTENT)
					.getProperty(Property.JCR_DATA).getBinary();
			byte[] targetJarBytes = RepoUtils.packageAsPdeSource(
					origBinary.getStream(), nameVersion);
			JcrUtils.copyBytesAsFile(targetSourceParentNode,
					targetSourceFileName, targetJarBytes);

			// reindex
			Node targetSourceJarNode = session.getNode(targetSourceJarPath);
			artifactIndexer.index(targetSourceJarNode);
			jarFileIndexer.index(targetSourceJarNode);
		} catch (RepositoryException e) {
			throw new SlcException("Cannot add PDE sources for " + sourcesNode,
					e);
		} finally {
			JcrUtils.closeQuietly(origBinary);
			JcrUtils.closeQuietly(osgiBinary);
		}

	}

	private class NormalizeJob extends Job {
		private Session session;
		private String version;
		private Boolean overridePoms;

		public NormalizeJob(Session session, String version,
				Boolean overridePoms) {
			super("Normalize Distribution");
			this.session = session;
			this.version = version;
			this.overridePoms = overridePoms;
		}

		@Override
		protected IStatus run(IProgressMonitor progressMonitor) {

			try {
				JcrMonitor monitor = new EclipseJcrMonitor(progressMonitor);
				// normalize artifacts
				Query countQuery = session
						.getWorkspace()
						.getQueryManager()
						.createQuery("select file from [nt:file] as file",
								Query.JCR_SQL2);
				QueryResult result = countQuery.execute();
				Long expectedCount = result.getNodes().getSize();
				monitor.beginTask("Normalize artifacts of "
						+ session.getWorkspace().getName(),
						expectedCount.intValue());
				NormalizingTraverser tiv = new NormalizingTraverser(monitor);
				session.getNode(artifactBasePath).accept(tiv);

				// normalize groups
				Query groupQuery = session
						.getWorkspace()
						.getQueryManager()
						.createQuery(
								"select group from [" + SlcTypes.SLC_GROUP_BASE
										+ "] as group", Query.JCR_SQL2);
				NodeIterator groups = groupQuery.execute().getNodes();
				monitor.beginTask("Normalize groups of "
						+ session.getWorkspace().getName(),
						(int) groups.getSize());
				while (groups.hasNext()) {
					NormalizeGroup.processGroupNode(groups.nextNode(), version,
							overridePoms, monitor);
				}
			} catch (Exception e) {
				return new Status(IStatus.ERROR, DistPlugin.PLUGIN_ID,
						"Cannot normalize distribution "
								+ session.getWorkspace().getName(), e);
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
		protected void entering(Property property, int level)
				throws RepositoryException {
		}

		@Override
		protected void entering(Node node, int level)
				throws RepositoryException {
			if (node.isNodeType(NodeType.NT_FILE)) {
				if (node.getName().endsWith("-sources.jar")) {
					monitor.subTask(node.getName());
					packageSourcesAsPdeSource(node);
					node.getSession().save();
					monitor.worked(1);
					if (log.isDebugEnabled())
						log.debug("Processed source artifact " + node.getPath());
				} else if (node.getName().endsWith(".jar")) {
					if (jarFileIndexer.support(node.getPath()))
						if (artifactIndexer.support(node.getPath())) {
							monitor.subTask(node.getName());
							artifactIndexer.index(node);
							jarFileIndexer.index(node);
							node.getSession().save();
							monitor.worked(1);
							if (log.isDebugEnabled())
								log.debug("Processed artifact "
										+ node.getPath());
						}
				} else {
					monitor.worked(1);
				}
			}
		}

		@Override
		protected void leaving(Property property, int level)
				throws RepositoryException {
		}

		@Override
		protected void leaving(Node node, int level) throws RepositoryException {
		}

	}

	public class NormalizationDialog extends TitleAreaDialog {
		private static final long serialVersionUID = -3103886455862638580L;

		private Text versionT;
		private String version;
		private Button overridePomsC;
		private Boolean overridePoms;

		public NormalizationDialog(Shell parentShell) {
			super(parentShell);
		}

		protected Point getInitialSize() {
			return new Point(300, 250);
		}

		protected Control createDialogArea(Composite parent) {
			Composite dialogarea = (Composite) super.createDialogArea(parent);
			dialogarea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
					true));
			Composite composite = new Composite(dialogarea, SWT.NONE);
			composite.setLayout(new GridLayout(2, false));
			composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
					false));
			versionT = createLT(composite, "Version");
			overridePomsC = createLC(composite, "Override POMs");
			setMessage("Configure normalization", IMessageProvider.NONE);

			parent.pack();
			return composite;
		}

		@Override
		protected void okPressed() {
			version = versionT.getText();
			overridePoms = overridePomsC.getSelection();
			super.okPressed();
		}

		/** Creates label and text. */
		protected Text createLT(Composite parent, String label) {
			new Label(parent, SWT.NONE).setText(label);
			Text text = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER
					| SWT.NONE);
			text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			return text;
		}

		/** Creates label and check. */
		protected Button createLC(Composite parent, String label) {
			new Label(parent, SWT.NONE).setText(label);
			Button check = new Button(parent, SWT.CHECK);
			check.setSelection(false);
			check.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			return check;
		}

		protected void configureShell(Shell shell) {
			super.configureShell(shell);
			shell.setText("Normalize...");
		}

		public String getVersion() {
			return version;
		}

		public Boolean getOverridePoms() {
			return overridePoms;
		}

	}

	/* DEPENDENCY INJECTION */
	public void setNodeRepository(Repository nodeRepository) {
		this.nodeRepository = nodeRepository;
	}

	public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
		this.repositoryFactory = repositoryFactory;
	}

	public void setKeyring(Keyring keyring) {
		this.keyring = keyring;
	}
}