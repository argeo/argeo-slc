package org.argeo.slc.client.ui.dist.editors;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.repo.RepoService;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

/** Artifact editor in a multiple repository environment */
public class ArtifactVersionEditor extends FormEditor implements SlcNames {
	private static final long serialVersionUID = 1109872666962757000L;

	public final static String ID = DistPlugin.PLUGIN_ID + ".artifactVersionEditor";

	/* DEPENDENCY INJECTION */
	private RepoService repoService;

	// Business Objects
	private Session businessSession;
	private Node artifact;

	private ModuleEditorInput editorInput;

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		editorInput = (ModuleEditorInput) input;
		businessSession = repoService.getRemoteSession(
				editorInput.getRepoNodePath(), editorInput.getUri(),
				editorInput.getWorkspaceName());
		try {
			artifact = businessSession.getNode(editorInput.getModulePath());
		} catch (RepositoryException e) {
			throw new PartInitException(
					"Unable to initialise editor for artifact "
							+ editorInput.getModulePath() + " in workspace "
							+ editorInput.getWorkspaceName(), e);
		}
		super.init(site, input);
	}

	/** Override to provide a specific part name */
	protected String getFormattedName() {
		try {
			String partName = null;
			if (artifact.hasProperty(SLC_ARTIFACT_ID))
				partName = artifact.getProperty(SLC_ARTIFACT_ID).getString();
			else
				partName = artifact.getName();

			if (partName.length() > 10) {
				partName = "..." + partName.substring(partName.length() - 10);
			}
			return partName;
		} catch (RepositoryException re) {
			throw new SlcException(
					"unable to get slc:artifactId Property for node "
							+ artifact, re);
		}
	}

	@Override
	protected void addPages() {
		setPartName(getFormattedName());

		try {
			addPage(new BundleDetailPage(this, "Details ", artifact));
			addPage(new BundleDependencyPage(this, "Dependencies ", artifact));
			addPage(new BundleRawPage(this, "Raw Meta-Data ", artifact));
		} catch (PartInitException e) {
			throw new SlcException("Cannot add distribution editor pages", e);
		}

	}

	@Override
	public void doSave(IProgressMonitor arg0) {
	}

	@Override
	public void dispose() {
		JcrUtils.logoutQuietly(businessSession);
		super.dispose();
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	protected RepoService getRepoService() {
		return repoService;
	}

	protected Node getArtifact() {
		return artifact;
	}

	/* DEPENDENCY INJECTION */
	public void setRepoService(RepoService repoService) {
		this.repoService = repoService;
	}
}