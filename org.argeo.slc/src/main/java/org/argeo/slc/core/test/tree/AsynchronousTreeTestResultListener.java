package org.argeo.slc.core.test.tree;

import java.util.Vector;

import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.NumericTRId;
import org.argeo.slc.core.test.TestResult;
import org.argeo.slc.core.test.TestResultListener;
import org.argeo.slc.core.test.TestResultPart;

public abstract class AsynchronousTreeTestResultListener implements
		TestResultListener, Runnable {
	private Vector<PartStruct> partStructs = new Vector<PartStruct>();
	private Thread thread;

	public void init() {
		thread = new Thread(this);
		thread.start();
	}

	public void destroy() {
		thread = null;
		synchronized (partStructs) {
			partStructs.notifyAll();
		}
	}

	public final void resultPartAdded(TestResult testResult,
			TestResultPart testResultPart) {
		TreeTestResult result = (TreeTestResult) testResult;
		synchronized (partStructs) {
			partStructs.add(new PartStruct(result.getCurrentPath(),
					(NumericTRId) result.getTestResultId(), testResultPart,
					result));
			partStructs.notifyAll();
		}
	}

	public void run() {
		while (thread != null) {
			synchronized (partStructs) {
				for (PartStruct partStruct : partStructs) {
					resultPartAdded(partStruct);
				}

				partStructs.clear();
				while (partStructs.size() == 0) {
					try {
						partStructs.wait();
					} catch (InterruptedException e) {
						// silent
					}
				}
			}
		}
	}

	protected abstract void resultPartAdded(PartStruct partStruct);

	protected static class PartStruct {
		public final TreeSPath path;
		public final NumericTRId resultId;
		public final TestResultPart part;
		public final TreeTestResult result;

		public PartStruct(TreeSPath path, NumericTRId resultId,
				TestResultPart part, TreeTestResult result) {
			super();
			this.path = path;
			this.resultId = resultId;
			this.part = part;
			this.result = result;
		}

	}

}
