package org.argeo.slc.core.test.tree;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.structure.StructureAware;
import org.argeo.slc.core.structure.StructurePath;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.TestResult;
import org.argeo.slc.core.test.TestResultId;
import org.argeo.slc.core.test.TestResultListener;
import org.argeo.slc.core.test.TestResultPart;

public class TreeTestResult implements TestResult, StructureAware {
	private TestResultId testResultId;
	private List<TestResultListener> listeners;

	private TreeSPath currentPath;

	private SortedMap<TreeSPath, List<TestResultPart>> resultParts = new TreeMap<TreeSPath, List<TestResultPart>>();

	public TestResultId getTestResultId() {
		return testResultId;
	}

	public void setTestResultId(TestResultId testResultId) {
		this.testResultId = testResultId;
	}

	public void setListeners(List<TestResultListener> listeners) {
		this.listeners = listeners;
	}
	
	public void addResultPart(TestResultPart part) {
		if(currentPath==null){
			throw new SlcException("No current path set.");
		}
		List<TestResultPart> list = resultParts.get(currentPath);
		if(list == null){
			list = new Vector<TestResultPart>();
			resultParts.put(currentPath, list);
		}
		list.add(part);
		
		// notify listeners
		for(TestResultListener listener: listeners){
			listener.resultPartAdded(this, part);
		}
	}

	public List<TestResultPart> listResultParts() {
		List<TestResultPart> all = new Vector<TestResultPart>();
		for(TreeSPath path:resultParts.keySet()){
			all.addAll(resultParts.get(path));
		}
		return all;
	}

	public void notifyCurrentPath(StructureRegistry registry, StructurePath path) {
		currentPath = (TreeSPath) path;
	}

	public TreeSPath getCurrentPath() {
		return currentPath;
	}

	public SortedMap<TreeSPath, List<TestResultPart>> getResultParts() {
		return resultParts;
	}

	
}
