package org.argeo.slc.client.ui.dist.editors;

import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/** An editor input pointing to a distribution workspace */
public class DistWkspEditorInput implements IEditorInput, SlcNames {

	// Injected
	// private RepositoryFactory repositoryFactory;
	// private Keyring keyring;
	// private Node repoNode;
	private String repoNodePath;
	private String uri;

	// Local variables
	private String workspaceName;

	// public WorkspaceEditorInput(RepositoryFactory repositoryFactory,
	// Keyring keyring, Repository localRepository, Node repoNode,
	// String uri) {
	// // this.repositoryFactory = repositoryFactory;
	// // this.keyring = keyring;
	// this.localRepository = localRepository;
	// // this.repoNode= repoNode;
	// this.uri = uri;
	//
	// }

	/** uri and workspace name cannot be null */
	public DistWkspEditorInput(String repoNodePath, String uri,
			String workspaceName) {
		if (workspaceName == null)
			throw new SlcException("Workspace name cannot be null");
		if (uri == null)
			throw new SlcException("URI for repository cannot be null");
		this.repoNodePath = repoNodePath;
		this.workspaceName = workspaceName;
		this.uri = uri;
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
		return "Editor for workspace " + workspaceName
				+ " in repository of URI " + uri;
	}

	public String getName() {
		return workspaceName + "@" + uri;
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DistWkspEditorInput))
			return false;

		DistWkspEditorInput other = (DistWkspEditorInput) obj;

		if (!workspaceName.equals(other.getWorkspaceName()))
			return false;
		if (!uri.equals(other.getUri()))
			return false;

		if (repoNodePath == null)
			return other.getRepoNodePath() == null;
		else
			return repoNodePath.equals(other.getRepoNodePath());
	}

	public String getUri() {
		return uri;
	}

	public String getWorkspaceName() {
		return workspaceName;
	}

	public String getRepoNodePath() {
		return repoNodePath;
	}
}