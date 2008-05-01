package org.argeo.slc.core.test.tree;

import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.TestResult;
import org.argeo.slc.core.test.TestResultListener;
import org.argeo.slc.core.test.TestResultPart;
import org.argeo.slc.core.test.TestRun;
import org.argeo.slc.core.test.TestRunAware;

/**
 * Complex implementation of a test result compatible with a tree based
 * structure.
 */
public class TreeTestResult implements TestResult, StructureAware<TreeSPath> {
	private Log log = LogFactory.getLog(TreeTestResult.class);

	private List<TestResultListener> listeners = new Vector<TestResultListener>();

	private TreeSPath currentPath;
	private TestRun currentTestRun;

	private Date closeDate;

	private boolean isClosed = false;

	private String uuid;

	private SortedMap<TreeSPath, PartSubList> resultParts = new TreeMap<TreeSPath, PartSubList>();
	private SortedMap<TreeSPath, StructureElement> elements = new TreeMap<TreeSPath, StructureElement>();

	/** Sets the list of listeners. */
	public void setListeners(List<TestResultListener> listeners) {
		this.listeners = listeners;
	}

	public void addResultPart(TestResultPart part) {
		if (currentPath == null) {
			throw new SlcException("No current path set.");
		}
		PartSubList subList = resultParts.get(currentPath);
		if (subList == null) {
			subList = new PartSubList();
			resultParts.put(currentPath, subList);
		}
		if (part instanceof TestRunAware && currentTestRun != null) {
			((TestRunAware) part).notifyTestRun(currentTestRun);
		}
		subList.getParts().add(part);

		// notify listeners
		synchronized (listeners) {
			for (TestResultListener listener : listeners) {
				listener.resultPartAdded(this, part);
			}
		}
	}

	public void notifyCurrentPath(StructureRegistry<TreeSPath> registry,
			TreeSPath path) {
		if (registry != null) {
			for (TreeSPath p : path.getHierarchyAsList()) {
				if (!elements.containsKey(p)) {
					StructureElement elem = registry.getElement(p);
					if (elem != null) {
						elements.put(p, elem);
					}
				} else {
					if (log.isTraceEnabled())
						log.trace("An element is already registered for path "
								+ p + " and was not updated");
				}

			}
		}

		currentPath = (TreeSPath) path;
	}

	/** Gets the current path. */
	public TreeSPath getCurrentPath() {
		return currentPath;
	}

	/** Gets all the results structured as a map of <code>PartSubList<code>s. */
	public SortedMap<TreeSPath, PartSubList> getResultParts() {
		return resultParts;
	}

	/** Used by ORM systems. */
	void setResultParts(SortedMap<TreeSPath, PartSubList> resultParts) {
		this.resultParts = resultParts;
	}

	public void close() {
		if (isClosed) {
			throw new SlcException("Test Result #" + getUuid()
					+ " alredy closed.");
		}
		closeDate = new Date();

		synchronized (listeners) {
			for (TestResultListener listener : listeners) {
				listener.close(this);
			}
			listeners.clear();
		}
		isClosed = true;

		log.info("Test Result #" + getUuid() + " closed.");
	}

	public Date getCloseDate() {
		return closeDate;
	}

	/** Sets the close date (for ORM) */
	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}

	public void notifyTestRun(TestRun testRun) {
		currentTestRun = testRun;
	}

	public SortedMap<TreeSPath, StructureElement> getElements() {
		return elements;
	}

	public void setElements(SortedMap<TreeSPath, StructureElement> pathNames) {
		this.elements = pathNames;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public SortedMap<TreeSPath, StructureElement> getRelatedElements(
			TreeSPath path) {
		SortedMap<TreeSPath, StructureElement> relatedElements = new TreeMap<TreeSPath, StructureElement>();
		List<TreeSPath> hierarchy = path.getHierarchyAsList();
		for (TreeSPath currPath : elements.keySet()) {
			if (hierarchy.contains(currPath)) {
				relatedElements.put(currPath, elements.get(currPath));
			}
		}
		return relatedElements;
	}

	public TestRun getCurrentTestRun() {
		return currentTestRun;
	}
	
	
}
