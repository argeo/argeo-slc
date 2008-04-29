package org.argeo.slc.core.test.tree;

import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.core.process.SlcExecutionAware;
import org.argeo.slc.core.process.SlcExecutionStep;
import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.NumericTRId;
import org.argeo.slc.core.test.TestResult;
import org.argeo.slc.core.test.TestResultId;
import org.argeo.slc.core.test.TestResultListener;
import org.argeo.slc.core.test.TestResultPart;

/**
 * Complex implementation of a test result compatible with a tree based
 * structure.
 */
public class TreeTestResult implements TestResult, StructureAware<TreeSPath>,
		SlcExecutionAware {
	private Log log = LogFactory.getLog(TreeTestResult.class);
	/** For ORM */
	private Long tid;

	//private NumericTRId testResultId;
	private List<TestResultListener> listeners = new Vector<TestResultListener>();

	private TreeSPath currentPath;
	private String currentSlcExecutionUuid;
	private String currentSlcExecutionStepUuid;

	private Date closeDate;

	private boolean isClosed = false;

	private String uuid;

	private SortedMap<TreeSPath, PartSubList> resultParts = new TreeMap<TreeSPath, PartSubList>();
	private SortedMap<TreeSPath, StructureElement> elements = new TreeMap<TreeSPath, StructureElement>();

	private StructureRegistry<TreeSPath> registry;

//	public TestResultId getTestResultId() {
//		return testResultId;
//	}

	/**
	 * Use of a <code>NumericTRId</code> is required by Hibernate. <b>It may
	 * change in the future.</b>
	 */
//	public NumericTRId getNumericResultId() {
//		return testResultId;
//	}

	/** Sets the test result id as a numeric test result id. */
//	public void setNumericResultId(NumericTRId testResultId) {
//		this.testResultId = testResultId;
//	}

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
			subList.setSlcExecutionUuid(currentSlcExecutionUuid);
			subList.setSlcExecutionStepUuid(currentSlcExecutionStepUuid);
			resultParts.put(currentPath, subList);
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
						// elements.put(p, elem.getLabel());
						elements.put(p, elem);
					} else {
						log.warn("An element is already registered for path "
								+ p);
					}
				}
			}
		}

		currentPath = (TreeSPath) path;
		this.registry = registry;
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

	Long getTid() {
		return tid;
	}

	void setTid(Long tid) {
		this.tid = tid;
	}

	/** Gets the related registry (can be null). */
	public StructureRegistry<TreeSPath> getRegistry() {
		return registry;
	}

	/** Sets the related registry. */
	// public void setRegistry(StructureRegistry<TreeSPath> registry) {
	// this.registry = registry;
	// }
	public Date getCloseDate() {
		return closeDate;
	}

	/** Sets the close date (for ORM) */
	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}

	public void notifySlcExecution(SlcExecution slcExecution) {
		currentSlcExecutionUuid = slcExecution.getUuid();
		SlcExecutionStep step = slcExecution.currentStep();
		if (step != null) {
			currentSlcExecutionStepUuid = step.getUuid();
		}
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

}
