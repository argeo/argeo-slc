package org.argeo.slc.client.ui.dist.editors;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/** Editor input for a JCR node object in a multi-repository environment */
public class ModuleEditorInput implements IEditorInput, SlcNames {

	// Define relevant workspace on a given repository
	private String repoNodePath;
	private String uri;
	private String workspaceName;
	private String modulePath;

	public ModuleEditorInput(String repoNodePath, String uri,
			String workspaceName, String artifactPath) {
		if (workspaceName == null)
			throw new SlcException("Workspace name cannot be null");
		if (uri == null && repoNodePath == null)
			throw new SlcException("Define at least one of the 2 "
					+ "parameters URI or Repo Node Path");
		if (artifactPath == null)
			throw new SlcException("Module path cannot be null");
		this.repoNodePath = repoNodePath;
		this.uri = uri;
		this.workspaceName = workspaceName;
		this.modulePath = artifactPath;
	}

	public String getModulePath() {
		return modulePath;
	}

	public String getWorkspaceName() {
		return workspaceName;
	}

	public String getRepoNodePath() {
		return repoNodePath;
	}

	public String getUri() {
		return uri;
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		return null;
	}

	public boolean exists() {
		return true;
	}

	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	// Dummy compulsory methods
	public String getToolTipText() {
		return getModulePath();
	}

	public String getName() {
		return JcrUtils.lastPathElement(modulePath);
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	/**
	 * equals method based on coordinates
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		ModuleEditorInput other = (ModuleEditorInput) obj;

		if (!modulePath.equals(other.getModulePath()))
			return false;
		if (!workspaceName.equals(other.getWorkspaceName()))
			return false;

		if (uri == null && other.getUri() != null
				|| !uri.equals(other.getUri()))
			return false;

		if (repoNodePath == null && other.getRepoNodePath() != null
				|| !repoNodePath.equals(other.getRepoNodePath()))
			return false;

		return true;
	}
}