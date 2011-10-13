package org.argeo.slc.client.ui.dist.views;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.ArgeoException;
import org.argeo.slc.client.ui.dist.DistConstants;
import org.argeo.slc.client.ui.dist.DistPlugin;
import org.argeo.slc.client.ui.dist.providers.ArtifactLabelProvider;
import org.argeo.slc.client.ui.dist.providers.ArtifactsTreeContentProvider;
import org.argeo.slc.client.ui.dist.utils.GenericDoubleClickListener;
import org.argeo.slc.repo.RepoConstants;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;

/**
 * Basic View to browse a maven base repository
 */

public class ArtifactsBrowser extends ViewPart implements DistConstants,
		RepoConstants {
	private final static Log log = LogFactory.getLog(ArtifactsBrowser.class);
	public final static String ID = DistPlugin.ID + ".artifactsBrowser";

	/* DEPENDENCY INJECTION */
	private Session jcrSession;
	// Business objects
	private Node rootNode;

	// This page widgets
	private TreeViewer artifactTreeViewer;

	@Override
	public void createPartControl(Composite parent) {
		GridLayout gl = new GridLayout(1, false);
		parent.setLayout(gl);
		artifactTreeViewer = createArtifactsTreeViewer(parent);

		// context menu : it is completely defined in the plugin.xml file.

		// Nothing in the context menu for the time being
		// MenuManager menuManager = new MenuManager();
		// Menu menu =
		// menuManager.createContextMenu(artifactTreeViewer.getTree());
		// artifactTreeViewer.getTree().setMenu(menu);
		// getSite().registerContextMenu(menuManager, artifactTreeViewer);

		getSite().setSelectionProvider(artifactTreeViewer);
		// packagesViewer.setComparer(new NodeViewerComparer());

		// Model initialisation
		if (jcrSession != null) {
			try {
				rootNode = jcrSession.getNode(ARTIFACTS_BASE_PATH);
				artifactTreeViewer.setInput(rootNode);
			} catch (RepositoryException e) {
				throw new ArgeoException("Cannot load base artifact nodes", e);
			}
		}
	}

	protected TreeViewer createArtifactsTreeViewer(Composite parent) {
		int style = SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION;
		Tree tree = new Tree(parent, style);
		createColumn(tree, "Artifacts", SWT.LEFT, 300);
		createColumn(tree, "Date created", SWT.LEFT, 105);
		createColumn(tree, "Size", SWT.RIGHT, 55);
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);

		TreeViewer viewer = new TreeViewer(tree);

		viewer.setContentProvider(new ArtifactsTreeContentProvider());
		viewer.setLabelProvider(new ArtifactLabelProvider());
		viewer.addDoubleClickListener(new GenericDoubleClickListener(viewer));
		viewer.setInput(rootNode);
		return viewer;
	}

	private static TreeColumn createColumn(Tree parent, String name, int style,
			int width) {
		TreeColumn result = new TreeColumn(parent, style);
		result.setText(name);
		result.setWidth(width);
		result.setMoveable(true);
		result.setResizable(true);
		return result;
	}

	protected TreeViewer getArtifactTreeViewer() {
		return artifactTreeViewer;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	/* DEPENDENCY INJECTION */
	public void setJcrSession(Session jcrSession) {
		this.jcrSession = jcrSession;
	}
}
