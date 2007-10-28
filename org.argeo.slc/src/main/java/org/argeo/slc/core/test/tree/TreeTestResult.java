package org.argeo.slc.core.test.tree;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.StructurePath;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.NumericTRId;
import org.argeo.slc.core.test.TestResult;
import org.argeo.slc.core.test.TestResultId;
import org.argeo.slc.core.test.TestResultListener;
import org.argeo.slc.core.test.TestResultPart;

public class TreeTestResult implements TestResult, StructureAware {
	private Log log = LogFactory.getLog(TreeTestResult.class);
	/** For ORM */
	private Long tid;

	private NumericTRId testResultId;
	private List<TestResultListener> listeners;

	private TreeSPath currentPath;
	
	private boolean isClosed = false;

	private SortedMap<TreeSPath, PartSubList> resultParts = new TreeMap<TreeSPath, PartSubList>();

	public TestResultId getTestResultId() {
		return testResultId;
	}

	public NumericTRId getNumericResultId() {
		return testResultId;
	}

	public void setNumericResultId(NumericTRId testResultId) {
		this.testResultId = testResultId;
	}

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
		subList.getParts().add(part);

		// notify listeners
		synchronized (listeners) {
			for (TestResultListener listener : listeners) {
				listener.resultPartAdded(this, part);
			}
		}
	}

	public void notifyCurrentPath(StructureRegistry registry, StructurePath path) {
		currentPath = (TreeSPath) path;
	}

	public TreeSPath getCurrentPath() {
		return currentPath;
	}

	public SortedMap<TreeSPath, PartSubList> getResultParts() {
		return resultParts;
	}

	void setResultParts(SortedMap<TreeSPath, PartSubList> resultParts) {
		this.resultParts = resultParts;
	}

	public void close() {
		if(isClosed){
			throw new SlcException("Test Result #"+getTestResultId()+" alredy closed.");
		}
		
		synchronized (listeners) {
			for (TestResultListener listener : listeners) {
				listener.close();
			}
			listeners.clear();
		}
		isClosed = true;
		log.info("Test Result #"+getTestResultId()+" closed.");
	}

	Long getTid() {
		return tid;
	}

	void setTid(Long tid) {
		this.tid = tid;
	}

}
