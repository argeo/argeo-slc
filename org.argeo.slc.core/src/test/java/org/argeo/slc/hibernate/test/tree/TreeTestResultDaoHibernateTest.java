package org.argeo.slc.hibernate.test.tree;

import java.util.Date;

import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.NumericTRId;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.TestStatus;
import org.argeo.slc.core.test.tree.PartSubList;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.structure.tree.TreeSPathDao;
import org.argeo.slc.dao.test.TestResultDao;
import org.argeo.slc.unit.SpringBasedTestCase;

public class TreeTestResultDaoHibernateTest extends SpringBasedTestCase {

	public void testCreate() {
		TreeSPathDao treeSPathDao = (TreeSPathDao) getContext().getBean(
				"treeSPathDao");

		TestResultDao testResultDao = (TestResultDao) getContext().getBean(
				"testResultDao");

		String pathParentStr = "/root/testParent";
		String pathStr = pathParentStr + "/test";
		TreeSPath path = TreeSPath.parseToCreatePath(pathStr);
		treeSPathDao.create(path);

		TreeTestResult treeTestResult = new TreeTestResult();
		SimpleResultPart partPassed = new SimpleResultPart();
		String msgPassed = "message";
		partPassed.setStatus(TestStatus.PASSED);
		partPassed.setMessage(msgPassed);

		SimpleResultPart partFailed = new SimpleResultPart();
		String msgFailed = "too bad";
		partFailed.setStatus(TestStatus.FAILED);
		partFailed.setMessage(msgFailed);

		NumericTRId trId = new NumericTRId();
		trId.setValue(1l);
		treeTestResult.setNumericResultId(trId);
		treeTestResult.notifyCurrentPath(null, path);
		treeTestResult.addResultPart(partPassed);
		treeTestResult.addResultPart(partFailed);

		Date closeDate = new Date();
		treeTestResult.setCloseDate(closeDate);

		testResultDao.create(treeTestResult);

		TreeTestResult treeTestResult2 = (TreeTestResult) testResultDao
				.getTestResult(trId);
		PartSubList list = treeTestResult2.getResultParts().get(path);

		assertEquals(2, list.getParts().size());
		SimpleResultPart part0 = (SimpleResultPart) list.getParts().get(0);
		assertEquals(TestStatus.PASSED, (int) part0.getStatus());
		assertEquals(msgPassed, part0.getMessage());

		SimpleResultPart part1 = (SimpleResultPart) list.getParts().get(1);
		assertEquals(TestStatus.FAILED, (int) part1.getStatus());
		assertEquals(msgFailed, part1.getMessage());

		assertEquals(closeDate, treeTestResult2.getCloseDate());
	}

	@Override
	protected String getApplicationContextLocation() {
		return "org/argeo/slc/hibernate/applicationContext.xml";
	}

}
