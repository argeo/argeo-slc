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
package org.argeo.slc.client.ui.dist.wizards;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.ArgeoMonitor;
import org.argeo.eclipse.ui.EclipseArgeoMonitor;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.PrivilegedJob;
import org.argeo.slc.client.ui.dist.utils.ViewerUtils;
import org.argeo.slc.jcr.SlcTypes;
import org.argeo.slc.repo.RepoConstants;
import org.argeo.slc.repo.RepoService;
import org.argeo.slc.repo.RepoUtils;
import org.argeo.slc.repo.maven.GenerateBinaries;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.sonatype.aether.artifact.Artifact;

/**
 * Define parameters to asynchronously generate binaries, sources and sdk pom
 * artifacts for this group using a {@link GenerateBinaries} runnable
 */
public class GenerateBinariesWizard extends Wizard {
	private final static Log log = LogFactory
			.getLog(GenerateBinariesWizard.class);

	// Business objects
	private final RepoService repoService;
	private final String repoNodePath;
	private String wkspName;
	private String groupNodePath;

	// The pages
	private RecapPage recapPage;

	// Controls with parameters
	private Text versionTxt;
	private Text latestVersionTxt;
	private Text highestArtifactVersionTxt;

	public GenerateBinariesWizard(RepoService repoService, String repoNodePath,
			String wkspName, String groupNodePath) {
		super();
		this.repoService = repoService;
		this.repoNodePath = repoNodePath;
		this.wkspName = wkspName;
		this.groupNodePath = groupNodePath;
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void addPages() {
		try {
			recapPage = new RecapPage();
			addPage(recapPage);
			setWindowTitle("Define Binary Generation Procedure");
		} catch (Exception e) {
			throw new SlcException("Cannot add page to wizard ", e);
		}
	}

	@Override
	public boolean performFinish() {
		if (!canFinish())
			return false;
		try {
			String msg = "Your are about to generate binaries, sources and sdk "
					+ "pom artifacts for this group, "
					+ "do you really want to proceed ?";

			boolean result = MessageDialog.openConfirm(DistPlugin.getDefault()
					.getWorkbench().getDisplay().getActiveShell(),
					"Confirm Launch", msg);

			if (result) {
				GenerateBinaryJob job = new GenerateBinaryJob(repoService,
						repoNodePath, wkspName, groupNodePath,
						versionTxt.getText());
				job.setUser(true);
				job.schedule();
			}
		} catch (Exception e) {
			throw new SlcException(
					"Unexpected error while launching the fetch", e);
		}
		return true;
	}

	// ///////////////////////////////
	// ////// THE PAGES
	private class RecapPage extends WizardPage {

		private TableViewer recapViewer;

		public RecapPage() {
			super("Define parameters and launch");
			setTitle("Define parameters and launch");
		}

		@Override
		public boolean isPageComplete() {
			return isCurrentPage();
		}

		public IWizardPage getNextPage() {
			return null; // always last
		}

		private void refreshValues() {
			Session session = null;
			try {
				session = repoService.getRemoteSession(repoNodePath, null,
						wkspName);
				Node groupNode = session.getNode(groupNodePath);
				GenerateBinaries gb = GenerateBinaries.preProcessGroupNode(
						groupNode, null);

				List<Artifact> binaries = new ArrayList<Artifact>();
				binaries.addAll(gb.getBinaries());

				Artifact highestVersion = gb.getHighestArtifactVersion();
				if (highestVersion != null)
					highestArtifactVersionTxt.setText(highestVersion
							.getBaseVersion());

				if (groupNode.hasNode(RepoConstants.BINARIES_ARTIFACT_ID)) {
					Node binaryNode = groupNode
							.getNode(RepoConstants.BINARIES_ARTIFACT_ID);
					Artifact currHighestVersion = null;
					for (NodeIterator ni = binaryNode.getNodes(); ni.hasNext();) {
						Node currN = ni.nextNode();
						if (currN
								.isNodeType(SlcTypes.SLC_ARTIFACT_VERSION_BASE)) {
							Artifact currVersion = RepoUtils.asArtifact(currN);

							if (currHighestVersion == null
									|| currVersion.getBaseVersion()
											.compareTo(
													currHighestVersion
															.getBaseVersion()) > 0)
								currHighestVersion = currVersion;
						}
					}
					if (currHighestVersion != null)
						latestVersionTxt.setText(currHighestVersion
								.getBaseVersion());
				}
				recapViewer.setInput(binaries);
				recapViewer.refresh();
			} catch (RepositoryException re) {
				throw new SlcException("Unable to get repositories URIs", re);
			} finally {
				JcrUtils.logoutQuietly(session);
			}
		}

		public void createControl(Composite parent) {
			setMessage("Configure Maven Indexing", IMessageProvider.NONE);

			Composite composite = new Composite(parent, SWT.NO_FOCUS);
			composite.setLayout(new GridLayout(2, false));

			versionTxt = createLT(composite, "Version");
			versionTxt
					.setToolTipText("Enter a version for the new Modular Distribution");

			latestVersionTxt = createLT(composite, "Latest version");
			latestVersionTxt.setEditable(false);
			latestVersionTxt
					.setToolTipText("The actual latest version of this modular distribution");

			highestArtifactVersionTxt = createLT(composite,
					"Highest version in current category");
			highestArtifactVersionTxt.setEditable(false);
			highestArtifactVersionTxt
					.setToolTipText("The highest version among all version of the below listed modules.");

			// Creates the table
			Table table = new Table(composite, SWT.H_SCROLL | SWT.V_SCROLL
					| SWT.BORDER);
			table.setLinesVisible(true);
			table.setHeaderVisible(true);
			table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2,
					1));
			recapViewer = new TableViewer(table);

			TableViewerColumn column = ViewerUtils.createTableViewerColumn(
					recapViewer, "Name", SWT.NONE, 250);
			column.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((Artifact) element).getArtifactId();
				}
			});

			column = ViewerUtils.createTableViewerColumn(recapViewer,
					"Version", SWT.NONE, 250);
			column.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return ((Artifact) element).getBaseVersion();
				}
			});

			recapViewer.setContentProvider(new IStructuredContentProvider() {
				List<Artifact> artifacts;

				@SuppressWarnings("unchecked")
				public void inputChanged(Viewer viewer, Object oldInput,
						Object newInput) {
					artifacts = (List<Artifact>) newInput;
					if (artifacts != null)
						recapViewer.refresh();
				}

				public void dispose() {
				}

				public Object[] getElements(Object inputElement) {
					return artifacts == null ? null : artifacts.toArray();
				}
			});

			// A basic comparator
			recapViewer.setComparator(new ViewerComparator());
			refreshValues();
			setControl(composite);
		}
	}

	/**
	 * Define the privileged job that will be run asynchronously generate
	 * corresponding artifacts
	 */
	private class GenerateBinaryJob extends PrivilegedJob {

		private final RepoService repoService;
		private final String repoNodePath;
		private final String wkspName;
		private final String groupNodePath;
		private final String version;

		public GenerateBinaryJob(RepoService repoService, String repoNodePath,
				String wkspName, String groupNodePath, String version) {
			super("Fetch");
			this.version = version;
			this.repoService = repoService;
			this.repoNodePath = repoNodePath;
			this.wkspName = wkspName;
			this.groupNodePath = groupNodePath;
		}

		@Override
		protected IStatus doRun(IProgressMonitor progressMonitor) {
			Session session = null;
			try {
				ArgeoMonitor monitor = new EclipseArgeoMonitor(progressMonitor);
				session = repoService.getRemoteSession(repoNodePath, null,
						wkspName);
				Node groupBaseNode = session.getNode(groupNodePath);
				GenerateBinaries.processGroupNode(groupBaseNode, version,
						monitor);
			} catch (Exception e) {
				if (log.isDebugEnabled())
					e.printStackTrace();
				return new Status(IStatus.ERROR, DistPlugin.ID,
						"Cannot normalize group", e);
			} finally {
				JcrUtils.logoutQuietly(session);
			}
			return Status.OK_STATUS;
		}
	}

	// ////////////////////////////
	// // Helpers
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
}