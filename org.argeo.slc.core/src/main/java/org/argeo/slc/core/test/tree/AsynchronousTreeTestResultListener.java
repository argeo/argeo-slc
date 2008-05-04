package org.argeo.slc.core.test.tree;

import java.util.Vector;

import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.TestResult;
import org.argeo.slc.core.test.TestResultListener;
import org.argeo.slc.core.test.TestResultPart;

/**
 * Abstract asynchronous implementation of a listener listening to a
 * <code>TreeTestResult</code>.
 * 
 * @deprecated listeners should be called synchronously
 * @see TreeTestResult
 */
public abstract class AsynchronousTreeTestResultListener implements
		TestResultListener, Runnable {
	private Vector<PartStruct> partStructs = new Vector<PartStruct>();
	private Thread thread;

	private Boolean synchronous = true;

	protected AsynchronousTreeTestResultListener() {
		this(true);
	}

	protected AsynchronousTreeTestResultListener(Boolean synchronousByDefault) {
		synchronous = synchronousByDefault;
	}

	/** Starts the underlying thread. */
	public void init() {
		if (!synchronous) {
			thread = new Thread(this);
			thread.start();
		}
	}

	/** Finish the remaining and destroy */
	public void close(TestResult testResult) {
		// FIXME: make behavior more robust when multiple results are
		// registering this listener.
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
		postClose((TreeTestResult) testResult);
	}

	public final void resultPartAdded(TestResult testResult,
			TestResultPart testResultPart) {
		TreeTestResult result = (TreeTestResult) testResult;
		PartStruct partStruct = new PartStruct(result.getCurrentPath(), result
				.getUuid(), testResultPart, result);

		if (!synchronous) {
			synchronized (partStructs) {
				partStructs.add(partStruct);
				partStructs.notifyAll();
			}
		} else {
			resultPartAdded(partStruct);
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
		public final String uuid;
		/** The part itself */
		public final TestResultPart part;
		/** The tree test result itself. */
		public final TreeTestResult result;

		/** Constructor */
		public PartStruct(TreeSPath path, String uuid, TestResultPart part,
				TreeTestResult result) {
			super();
			this.path = path;
			this.uuid = uuid;
			this.part = part;
			this.result = result;
		}

	}

	public Boolean getSynchronous() {
		return synchronous;
	}

	public void setSynchronous(Boolean synchronous) {
		this.synchronous = synchronous;
	}

}
