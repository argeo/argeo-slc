package org.argeo.slc.hibernate.test.tree;

import static org.argeo.slc.core.test.tree.TreeTestResultTestUtils.createSimpleResultPartFailed;
import static org.argeo.slc.core.test.tree.TreeTestResultTestUtils.createSimpleResultPartPassed;
import static org.argeo.slc.core.test.tree.TreeTestResultTestUtils.createSimpleTreeTestResult;

import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.core.process.SlcExecutionStep;
import org.argeo.slc.core.process.SlcExecutionTestUtils;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.argeo.slc.unit.test.tree.UnitTestTreeUtil;

public class TreeTestResultDaoHibernateTest extends AbstractSpringTestCase {

	public void testCreate() {
		TreeTestResultDao testResultDao = (TreeTestResultDao) getContext()
				.getBean("testResultDao");

		// SLC Execution
		SlcExecution slcExecution = SlcExecutionTestUtils
				.createSimpleSlcExecution();
		SlcExecutionStep step = new SlcExecutionStep("LOG", "JUnit step");
		slcExecution.getSteps().add(step);

		String pathParentStr = "/root/testParent";
		String pathStr = pathParentStr + "/test";
		TreeSPath path = TreeSPath.parseToCreatePath(pathStr);
		// treeSPathDao.create(path);

		TreeTestResult ttr = createSimpleTreeTestResult();
		ttr.notifySlcExecution(slcExecution);
		ttr.notifyCurrentPath(null, path);

		ttr.addResultPart(createSimpleResultPartPassed());
		ttr.addResultPart(createSimpleResultPartFailed());

		testResultDao.create(ttr);

		TreeTestResult ttrPersisted = (TreeTestResult) testResultDao
				.getTestResult(ttr.getTestResultId());
		UnitTestTreeUtil.assertTreeTestResult(ttr, ttrPersisted);
	}

	@Override
	protected String getApplicationContextLocation() {
		return "org/argeo/slc/hibernate/applicationContext.xml";
	}

}
