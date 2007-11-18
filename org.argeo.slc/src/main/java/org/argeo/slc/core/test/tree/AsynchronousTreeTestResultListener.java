package org.argeo.slc.core.test.tree;

import java.util.Vector;

import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.NumericTRId;
import org.argeo.slc.core.test.TestResult;
import org.argeo.slc.core.test.TestResultListener;
import org.argeo.slc.core.test.TestResultPart;

/**
 * Abstract asynchronous implementation of a listener listening to a
 * <code>TreeTestResult</code>.
 * 
 * @see TreeTestResult
 */
public abstract class AsynchronousTreeTestResultListener implements
		TestResultListener, Runnable {
	private Vector<PartStruct> partStructs = new Vector<PartStruct>();
	private Thread thread;

	/** Starts the underlying thread. */
	public void init() {
		thread = new Thread(this);
		thread.start();
	}

	/** Finish the remaining and destroy */
	public void close(TestResult testResult) {
		synchronized (partStructs) {
			// TODO: put a timeout
			while (partStructs.size() != 0) {
				try {
					partStructs.wait(500);
				} catch (InterruptedException e) {
					// silent
				}
			}
			thread = null;
			partStructs.notifyAll();
		}
		postClose((TreeTestResult)testResult);
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

	/** Called when a result part has been added. */
	protected abstract void resultPartAdded(PartStruct partStruct);

	/**
	 * Called at the end of close. Default implementation is empty. To be
	 * overridden.
	 */
	protected void postClose(TreeTestResult testResult) {

	}

	public void run() {
		while (thread != null) {
			synchronized (partStructs) {
				for (PartStruct partStruct : partStructs) {
					resultPartAdded(partStruct);
				}

				partStructs.clear();
				partStructs.notifyAll();
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

	/** Structure used to pass tree specific information to subclasses. */
	protected static class PartStruct {
		/** The tree path of this part. */
		public final TreeSPath path;
		/** The test result id of the related test result */
		public final NumericTRId resultId;
		/** The part itself */
		public final TestResultPart part;
		/** The tree test result itself. */
		public final TreeTestResult result;

		/** Constructor */
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
