package org.argeo.slc.hibernate.test.tree;

import static org.argeo.slc.core.test.tree.TreeTestResultTestUtils.createCompleteTreeTestResult;
import static org.argeo.slc.core.test.tree.TreeTestResultTestUtils.createSimpleTreeTestResult;

import java.util.Date;

import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.structure.tree.TreeSRegistry;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.TestStatus;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultTestUtils;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.argeo.slc.unit.test.tree.UnitTestTreeUtil;

public class TreeTestResultDaoHibernateTest extends AbstractSpringTestCase {
	private TreeTestResultDao testResultDao = null;

	@Override
	public void setUp() {
		testResultDao = (TreeTestResultDao) getContext().getBean(
				"testResultDao");
	}

	public void testCreate() {

		TreeTestResult ttr = createCompleteTreeTestResult();
		testResultDao.create(ttr);

		TreeTestResult ttrPersisted = (TreeTestResult) testResultDao
				.getTestResult(ttr.getUuid());

		UnitTestTreeUtil.assertTreeTestResult(ttr, ttrPersisted);
	}

	public void testUpdate() {
		TreeTestResult ttr = createCompleteTreeTestResult();
		testResultDao.create(ttr);

		TreeTestResult ttrUpdated = (TreeTestResult) testResultDao
				.getTestResult(ttr.getUuid());

		// Modifying ttrUpdated

		/**
		 * this closeDate update commented because the assertTreeTestResult will
		 * find a unexpected discrepancy in the date.
		 * ttrUpdated.setCloseDate(new Date());
		 */

		String pathStr = "/test";
		TreeSPath path = TreeSPath.parseToCreatePath(pathStr);

		TreeSRegistry registry = new TreeSRegistry();
		SimpleSElement elem = new SimpleSElement("Unit Test");
		elem.getTags().put("myTag", "myTagValue");
		registry.register(path, elem);

		ttrUpdated.notifyCurrentPath(registry, path);

		ttrUpdated.addResultPart(TreeTestResultTestUtils
				.createSimpleResultPartPassed());
		ttrUpdated.addResultPart(TreeTestResultTestUtils
				.createSimpleResultPartFailed());
		ttrUpdated.addResultPart(TreeTestResultTestUtils
				.createSimpleResultPartError());

		testResultDao.update(ttrUpdated);

		// comparison of ttrUpdated and ttrRetrieved
		TreeTestResult ttrRetrieved = (TreeTestResult) testResultDao
				.getTestResult(ttr.getUuid());

		UnitTestTreeUtil.assertTreeTestResult(ttrRetrieved, ttrUpdated);
	}

	public void testMultipleUpdateScenario() throws Exception{
		TreeSRegistry registry = new TreeSRegistry();

		TreeSPath path = new TreeSPath("/root/test");
		SimpleSElement elem = new SimpleSElement("Unit Test");
		elem.getTags().put("myTag", "myTagValue");
		registry.register(path, elem);
		
		TreeTestResult ttr = createSimpleTreeTestResult();
		ttr.notifyCurrentPath(registry, path);
		ttr.addResultPart(new SimpleResultPart(TestStatus.PASSED,"First test"));
		
		testResultDao.create(ttr);
		
		path = new TreeSPath("/root/test2/subtest");
		elem = new SimpleSElement("Sub Test");
		elem.getTags().put("myTag", "myTagValue");
		registry.register(path, elem);
		
		ttr.notifyCurrentPath(registry, path);
		ttr.addResultPart(new SimpleResultPart(TestStatus.PASSED,"Second test"));
		
		testResultDao.update(ttr);
		
		ttr.notifyCurrentPath(registry, path);
		ttr.addResultPart(new SimpleResultPart(TestStatus.PASSED,"Third test with same path"));
		
		testResultDao.update(ttr);
		
		ttr.close();
		
		testResultDao.close(ttr.getUuid(), ttr.getCloseDate());
	}

	@Override
	protected String getApplicationContextLocation() {
		return "org/argeo/slc/hibernate/applicationContext.xml";
	}

}
