package org.argeo.slc.core.test.tree;

import java.util.Date;

import org.argeo.slc.core.test.NumericTRId;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.TestStatus;

public abstract class TreeTestResultTestUtils {

	public static TreeTestResult createSimpleTreeTestResult(){
		TreeTestResult treeTestResult = new TreeTestResult();
		treeTestResult.setNumericResultId( new NumericTRId(System.currentTimeMillis()));
		treeTestResult.setCloseDate(new Date());
		return treeTestResult;
	}
	
	public static SimpleResultPart createSimpleResultPartPassed(){
		SimpleResultPart partPassed = new SimpleResultPart();
		String msgPassed = "message";
		partPassed.setStatus(TestStatus.PASSED);
		partPassed.setMessage(msgPassed);
		return partPassed;
	}

	public static SimpleResultPart createSimpleResultPartFailed(){
		SimpleResultPart partFailed = new SimpleResultPart();
		String msgFailed = "too bad";
		partFailed.setStatus(TestStatus.FAILED);
		partFailed.setMessage(msgFailed);
		return partFailed;
	}
	
	private TreeTestResultTestUtils(){
		
	}
}
