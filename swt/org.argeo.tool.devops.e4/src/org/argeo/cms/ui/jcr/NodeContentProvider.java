package org.argeo.cms.ui.jcr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.argeo.api.cms.CmsConstants;
import org.argeo.api.cms.keyring.Keyring;
import org.argeo.cms.jcr.CmsJcrUtils;
import org.argeo.cms.ui.jcr.model.RepositoriesElem;
import org.argeo.cms.ui.jcr.model.SingleJcrNodeElem;
import org.argeo.cms.ux.widgets.TreeParent;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Implementation of the {@code ITreeContentProvider} to display multiple
 * repository environment in a tree like structure
 */
public class NodeContentProvider implements ITreeContentProvider {
	private static final long serialVersionUID = -4083809398848374403L;
	final private RepositoryRegister repositoryRegister;
	final private RepositoryFactory repositoryFactory;

	// Current user session on the default workspace of the argeo Node
	final private Session userSession;
	final private Keyring keyring;
	private boolean sortChildren;

	// Reference for cleaning
	private SingleJcrNodeElem homeNode = null;
	private RepositoriesElem repositoriesNode = null;

	// Utils
	private TreeBrowserComparator itemComparator = new TreeBrowserComparator();

	public NodeContentProvider(Session userSession, Keyring keyring,
			RepositoryRegister repositoryRegister,
			RepositoryFactory repositoryFactory, Boolean sortChildren) {
		this.userSession = userSession;
		this.keyring = keyring;
		this.repositoryRegister = repositoryRegister;
		this.repositoryFactory = repositoryFactory;
		this.sortChildren = sortChildren;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput == null)// dispose
			return;

		if (userSession != null) {
			Node userHome = CmsJcrUtils.getUserHome(userSession);
			if (userHome != null) {
				// TODO : find a way to dynamically get alias for the node
				if (homeNode != null)
					homeNode.dispose();
				homeNode = new SingleJcrNodeElem(null, userHome,
						userSession.getUserID(), CmsConstants.EGO_REPOSITORY);
			}
		}
		if (repositoryRegister != null) {
			if (repositoriesNode != null)
				repositoriesNode.dispose();
			repositoriesNode = new RepositoriesElem("Repositories",
					repositoryRegister, repositoryFactory, null, userSession,
					keyring);
		}
	}

	/**
	 * Sends back the first level of the Tree. Independent from inputElement
	 * that can be null
	 */
	public Object[] getElements(Object inputElement) {
		List<Object> objs = new ArrayList<Object>();
		if (homeNode != null)
			objs.add(homeNode);
		if (repositoriesNode != null)
			objs.add(repositoriesNode);
		return objs.toArray();
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof TreeParent) {
			if (sortChildren) {
				Object[] tmpArr = ((TreeParent) parentElement).getChildren();
				if (tmpArr == null)
					return new Object[0];
				TreeParent[] arr = new TreeParent[tmpArr.length];
				for (int i = 0; i < tmpArr.length; i++)
					arr[i] = (TreeParent) tmpArr[i];
				Arrays.sort(arr, itemComparator);
				return arr;
			} else
				return ((TreeParent) parentElement).getChildren();
		} else
			return new Object[0];
	}

	/**
	 * Sets whether the content provider should order the children nodes or not.
	 * It is user duty to call a full refresh of the tree after changing this
	 * parameter.
	 */
	public void setSortChildren(boolean sortChildren) {
		this.sortChildren = sortChildren;
	}

	public Object getParent(Object element) {
		if (element instanceof TreeParent) {
			return ((TreeParent) element).getParent();
		} else
			return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof RepositoriesElem) {
			RepositoryRegister rr = ((RepositoriesElem) element)
					.getRepositoryRegister();
			return rr.getRepositories().size() > 0;
		} else if (element instanceof TreeParent) {
			TreeParent tp = (TreeParent) element;
			return tp.hasChildren();
		}
		return false;
	}

	public void dispose() {
		if (homeNode != null)
			homeNode.dispose();
		if (repositoriesNode != null) {
			// logs out open sessions
			// see https://bugzilla.argeo.org/show_bug.cgi?id=23
			repositoriesNode.dispose();
		}
	}

	/**
	 * Specific comparator for this view. See specification here:
	 * https://www.argeo.org/bugzilla/show_bug.cgi?id=139
	 */
	private class TreeBrowserComparator implements Comparator<TreeParent> {

		public int category(TreeParent element) {
			if (element instanceof SingleJcrNodeElem) {
				Node node = ((SingleJcrNodeElem) element).getNode();
				try {
					if (node.isNodeType(NodeType.NT_FOLDER))
						return 5;
				} catch (RepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return 10;
		}

		public int compare(TreeParent o1, TreeParent o2) {
			int cat1 = category(o1);
			int cat2 = category(o2);

			if (cat1 != cat2) {
				return cat1 - cat2;
			}
			return o1.getName().compareTo(o2.getName());
		}
	}
}
