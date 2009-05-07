package org.argeo.slc.core.test.tree;

import org.argeo.slc.core.attachment.Attachment;
import org.argeo.slc.test.TestResultListener;

public interface TreeTestResultListener extends
		TestResultListener<TreeTestResult> {
	public void addAttachment(TreeTestResult testResult,
			Attachment attachment);
}
