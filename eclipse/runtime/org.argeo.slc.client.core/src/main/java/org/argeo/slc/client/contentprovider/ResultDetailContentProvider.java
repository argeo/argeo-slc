package org.argeo.slc.client.contentprovider;

import java.util.List;
import java.util.SortedMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.eclipse.ui.TreeParent;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.tree.PartSubList;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.test.TestResultPart;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Basic tree view of the chosen process details
 */
public class ResultDetailContentProvider implements ITreeContentProvider {
	private final static Log log = LogFactory
			.getLog(ResultDetailContentProvider.class);

	private TreeTestResult treeTestResult;

	public Object[] getChildren(Object parent) {
		if (parent instanceof TreeTestResult) {
			log.error("We should not reach this point.");
			return null;
		}

		if (parent instanceof TreeParent) {
			return ((TreeParent) parent).getChildren();
		}

		if (parent instanceof ResultPartNode) {
			// we reached a leaf
			return null;
		}
		return null;
	}

	public Object getParent(Object node) {
		if (node instanceof TreeParent) {
			return ((TreeParent) node).getParent();
		}
		return null;
	}

	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeParent && ((TreeParent) parent).isLoaded()) {
			return ((TreeParent) parent).hasChildren();
		}
		return false;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}

	public Object[] getElements(Object parent) {
		if (parent instanceof TreeTestResult) {
			treeTestResult = (TreeTestResult) parent;

			// We wrap domain object in ViewSpecificObjects.
			ResultTreeParent root = new ResultTreeParent("Test "
					+ treeTestResult.getUuid());
			SortedMap<TreeSPath, PartSubList> partSubLists = treeTestResult
					.getResultParts();

			for (TreeSPath key : partSubLists.keySet()) {
				String relPath = key.getAsUniqueString();

				// get rid of '/' that begins every TreeSPath Unique string
				relPath = relPath.substring(1);
				String[] pathes = relPath.split("/"); // parse the TreeSPath
				ResultTreeParent curTreeParent = root;

				// We create intermediate folders if needed
				for (int i = 0; i < pathes.length; i++) {
					// if (log.isDebugEnabled())
					// log.debug("i = " + i + " - " + pathes[i]);

					if (curTreeParent.getChildByName(pathes[i]) == null) {
						ResultTreeParent child = new ResultTreeParent(pathes[i]);
						curTreeParent.addChild(child);
						curTreeParent = child;
					} else
						curTreeParent = (ResultTreeParent) curTreeParent
								.getChildByName(pathes[i]);
				}

				// We create leafs
				List<TestResultPart> parts = partSubLists.get(key).getParts();
				for (TestResultPart part : parts) {
					ResultPartNode node = new ResultPartNode(part.toString(),
							part.getStatus(), part.getMessage(),
							part.getExceptionMessage());
					curTreeParent.addChild(node);
				}
			}

			// We must set status isPassed for each node.
			setIsPassed(root);
			return root.getChildren();
		}
		return null;
	}

	public void setIsPassed(StatusAware node) {

		if (node instanceof ResultTreeObject) {
			ResultTreeObject rto = (ResultTreeObject) node;
			rto.isPassed = rto.isPassed();
			return;
		}
		if (node instanceof ResultTreeParent) {
			ResultTreeParent rtp = (ResultTreeParent) node;
			// we dig the tree recursivly
			for (TreeParent to : rtp.getChildren())
				setIsPassed((StatusAware) to);
			// we set is passed
			for (TreeParent to : rtp.getChildren()) {
				if (!((StatusAware) to).isPassed()) {
					rtp.isPassed = false;
					return;
				}
			}
			return;
		}
	}

	// To enable display of color to show if a test is passed or not even when
	// hidden. We say a test is in error if its status is FAILED or ERROR (e.g,
	// if it has not executed completely due to technical problems).
	public interface StatusAware {
		public void setPassed(boolean isPassed);

		public boolean isPassed();
	}

	public class ResultTreeParent extends TreeParent implements StatusAware {

		public ResultTreeParent(String name) {
			super(name);
		}

		private boolean isPassed = true;

		public void setPassed(boolean isPassed) {
			this.isPassed = isPassed;
		}

		public boolean isPassed() {
			return isPassed;
		}
	}

	public class ResultTreeObject extends TreeParent implements StatusAware {

		public ResultTreeObject(String name) {
			super(name);
		}

		private boolean isPassed = true;

		public void setPassed(boolean isPassed) {
			this.isPassed = isPassed;
		}

		public boolean isPassed() {
			return isPassed;
		}
	}

	// Specific inner classes
	public class ResultPartNode extends ResultTreeObject {

		private String status;
		private String message;
		private String exceptionMessage;

		public ResultPartNode(String label, Integer status, String message) {
			super(label);
			handleStatus(status);
			this.message = message;
		}

		public ResultPartNode(String label, Integer status, String message,
				String exceptionMessage) {
			super(label);
			handleStatus(status);
			this.message = message;
			this.exceptionMessage = exceptionMessage;
		}

		private void handleStatus(Integer status) {
			switch (status) {
			case 0:
				this.status = "PASSED";
				setPassed(true);
				break;
			case 1:
				this.status = "FAILED";
				setPassed(false);
				break;
			case 2:
				this.status = "ERROR";
				setPassed(false);
				break;
			}
			// for the moment being we don't have a relevant label
		}

		public String getStatus() {
			return status;
		}

		public String getMessage() {
			return message;
		}

		public String getExceptionMessage() {
			return exceptionMessage;
		}
	}
}
