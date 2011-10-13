package org.argeo.slc.client.ui.dist.utils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;

import org.argeo.ArgeoException;
import org.argeo.eclipse.ui.specific.FileHandler;
import org.argeo.jcr.ui.explorer.utils.JcrFileProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * Centralizes the management of double click on an ArtifactTreeViewer
 */
public class GenericDoubleClickListener implements IDoubleClickListener {

	// private final static Log log = LogFactory
	// .getLog(GenericNodeDoubleClickListener.class);

	private TreeViewer viewer;
	private JcrFileProvider jfp;
	private FileHandler fileHandler;

	public GenericDoubleClickListener(TreeViewer viewer) {
		this.viewer = viewer;
		jfp = new JcrFileProvider();
		fileHandler = new FileHandler(jfp);
	}

	public void doubleClick(DoubleClickEvent event) {
		if (event.getSelection() == null || event.getSelection().isEmpty())
			return;
		Object obj = ((IStructuredSelection) event.getSelection())
				.getFirstElement();
		if (obj instanceof Node) {
			Node node = (Node) obj;
			try {
				if (node.isNodeType(NodeType.NT_FILE)) {
					// double click on a file node triggers its opening
					String name = node.getName();
					String id = node.getIdentifier();

					// For the file provider to be able to browse the
					// various
					// repository.
					// TODO : enhanced that.
					// ITreeContentProvider itcp = (ITreeContentProvider)
					// nodeViewer
					// .getContentProvider();
					// jfp.setRootNodes((Object[]) itcp.getElements(null));
					fileHandler.openFile(name, id);
				}
			} catch (RepositoryException re) {
				throw new ArgeoException(
						"Repository error while getting node info", re);
			}
			// catch (PartInitException pie) {
			// throw new ArgeoException(
			// "Unexepected exception while opening node editor", pie);
			// }
		}
	}
}
