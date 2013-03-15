package org.argeo.slc.client.ui.dist.controllers;

import javax.jcr.RepositoryFactory;

import org.argeo.slc.client.ui.dist.model.DistParentElem;
import org.argeo.slc.client.ui.dist.model.RepoElem;
import org.argeo.slc.client.ui.dist.model.WorkspaceElem;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Enables browsing in local public slc distribution repositories. For the time
 * being, it supports only one repository at a time. Repository factory must be
 * injected
 */

public class AnonymousDistTreeContentProvider implements ITreeContentProvider {

	// List<RepoElem> repositories = new ArrayList<RepoElem>();
	private RepoElem publicRepo;

	private RepositoryFactory repositoryFactory;

	/**
	 * @param input
	 *            the URI to the public repository to browse
	 */
	public Object[] getElements(Object input) {
		String uri = (String) input;
		publicRepo = new RepoElem(repositoryFactory, uri,
				"Argeo Public Repository");
		return publicRepo.getChildren();
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof DistParentElem) {
			return ((DistParentElem) parentElement).getChildren();
		} else
			return null;
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof WorkspaceElem)
			return false;
		else if (element instanceof DistParentElem)
			return true;
		else
			return false;
	}

	public void dispose() {
		publicRepo.dispose();
	}

	/*
	 * DEPENDENCY INJECTION
	 */
	public void setRepositoryFactory(RepositoryFactory repositoryFactory) {
		this.repositoryFactory = repositoryFactory;
	}
}