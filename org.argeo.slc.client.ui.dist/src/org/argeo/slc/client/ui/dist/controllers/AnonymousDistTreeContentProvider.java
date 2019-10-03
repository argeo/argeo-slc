package org.argeo.slc.client.ui.dist.controllers;

import javax.jcr.RepositoryFactory;

import org.argeo.eclipse.ui.TreeParent;
import org.argeo.slc.client.ui.dist.model.RepoElem;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Enable browsing in local public slc distribution repositories. For the time
 * being, it supports only one repository at a time. Repository factory must be
 * injected
 */
public class AnonymousDistTreeContentProvider implements ITreeContentProvider {
	private static final long serialVersionUID = -4149180221319229128L;

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
		// force connection and creation of the children UI object
		publicRepo.login();
		return publicRepo.getChildren();
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	// @Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof TreeParent)
			return ((TreeParent) parentElement).getChildren();
		else
			return null;
	}

	// @Override
	public Object getParent(Object element) {
		if (element instanceof TreeParent)
			return ((TreeParent) element).getParent();
		return null;
	}

	// @Override
	public boolean hasChildren(Object element) {
		if (element instanceof TreeParent)
			return ((TreeParent) element).hasChildren();
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