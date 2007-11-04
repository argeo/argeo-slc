package org.argeo.slc.core.test.tree;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Listener logging tree-based test results to the underlying logging system.
 * 
 * @see TreeTestResult
 * 
 */
public class TreeTestResultLogger extends AsynchronousTreeTestResultListener {

	private static Log log = LogFactory.getLog(TreeTestResultLogger.class);

	@Override
	protected void resultPartAdded(PartStruct partStruct) {
		log.info(partStruct.part + " - " + partStruct.resultId + ":"
				+ partStruct.path);
	}

}
