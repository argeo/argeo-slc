package org.argeo.slc.core.test.tree;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.TestResultPart;
import org.argeo.slc.core.test.TestStatus;

/**
 * Listener logging tree-based test results to the underlying logging system.
 * 
 * @see TreeTestResult
 * 
 */
public class TreeTestResultLogger extends AsynchronousTreeTestResultListener {

	private static Log log = LogFactory.getLog(TreeTestResultLogger.class);

	public TreeTestResultLogger() {
		super(true);
	}

	@Override
	protected void resultPartAdded(PartStruct partStruct) {
		TestResultPart part = partStruct.part;
		String msg = partStruct.part + " - " + partStruct.uuid + ":"
				+ partStruct.path;
		if (part.getStatus().equals(TestStatus.PASSED)) {
			log.info(msg);
		} else if (part.getStatus().equals(TestStatus.FAILED)) {
			log.warn(msg);
		} else if (part.getStatus().equals(TestStatus.ERROR)) {
			log.error(msg + ((SimpleResultPart) part).getExceptionMessage());
		} else {
			log.error("Unknow test status: " + msg);
		}
	}

}
