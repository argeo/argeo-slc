package org.argeo.slc.core.test.tree;

import java.util.Date;
import java.util.List;
import java.util.Map;
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
public class TreeTestResult implements TestResult, StructureAware<TreeSPath>,
		Comparable<TreeTestResult> {
	private Log log = LogFactory.getLog(TreeTestResult.class);

	private List<TestResultListener<TreeTestResult>> listeners = new Vector<TestResultListener<TreeTestResult>>();

	private TreeSPath currentPath;
	private TestRun currentTestRun;

	private Date closeDate;

	private Boolean isClosed = false;

	private Boolean warnIfAlreadyClosed = true;

	private String uuid;

	private SortedMap<TreeSPath, PartSubList> resultParts = new TreeMap<TreeSPath, PartSubList>();
	private SortedMap<TreeSPath, StructureElement> elements = new TreeMap<TreeSPath, StructureElement>();

	private Map<String, String> attributes = new TreeMap<String, String>();

	/** Sets the list of listeners. */
	public void setListeners(List<TestResultListener<TreeTestResult>> listeners) {
		this.listeners = listeners;
	}

	public void addResultPart(TestResultPart part) {
		if (isClosed)
			throw new SlcException("Cannot result parts to a closed result");

		if (currentPath == null)
			throw new SlcException("No current path set.");

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
			for (TestResultListener<TreeTestResult> listener : listeners) {
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

		currentPath = path;
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
		if (resultParts.size() == 0) {
			if (log.isTraceEnabled())
				log.trace("Test Result #" + getUuid()
						+ " contains no results, no need to close it.");
			return;
		}

		if (isClosed) {
			if (warnIfAlreadyClosed)
				log.warn("Test Result #" + getUuid()
						+ " already closed. Doing nothing.");
			return;
		}

		closeDate = new Date();

		synchronized (listeners) {
			for (TestResultListener<TreeTestResult> listener : listeners) {
				listener.close(this);
			}
			listeners.clear();
		}
		isClosed = true;

		if (log.isTraceEnabled())
			log.trace("Test Result " + getUuid() + " closed.");
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
		if (path == null)
			throw new SlcException(
					"Cannot retrieve element for a null path in result #"
							+ uuid);

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

	public int compareTo(TreeTestResult ttr2) {
		TreeTestResult ttr1 = this;
		if (ttr1.getCloseDate() != null && ttr2.getCloseDate() != null) {
			return -ttr1.getCloseDate().compareTo(ttr2.getCloseDate());
		} else if (ttr1.getCloseDate() != null && ttr2.getCloseDate() == null) {
			return 1;
		} else if (ttr1.getCloseDate() == null && ttr2.getCloseDate() != null) {
			return -1;
		} else {
			return ttr1.getUuid().compareTo(ttr2.getUuid());
		}
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public void setWarnIfAlreadyClosed(Boolean warnIfAlreadyClosed) {
		this.warnIfAlreadyClosed = warnIfAlreadyClosed;
	}

}
