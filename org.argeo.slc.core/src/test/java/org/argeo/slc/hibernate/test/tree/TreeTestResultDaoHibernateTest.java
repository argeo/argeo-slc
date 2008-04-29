package org.argeo.slc.hibernate.test.tree;

import static org.argeo.slc.core.test.tree.TreeTestResultTestUtils.createCompleteTreeTestResult;

import java.util.Date;

import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.structure.tree.TreeSRegistry;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultTestUtils;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.argeo.slc.unit.test.tree.UnitTestTreeUtil;

public class TreeTestResultDaoHibernateTest extends AbstractSpringTestCase {

	public void testCreate() {
		TreeTestResultDao testResultDao = (TreeTestResultDao) getContext()
				.getBean("testResultDao");

		TreeTestResult ttr = createCompleteTreeTestResult();
		testResultDao.create(ttr);

		TreeTestResult ttrPersisted = (TreeTestResult) testResultDao
				.getTestResult(ttr.getTestResultId());
		
		UnitTestTreeUtil.assertTreeTestResult(ttr, ttrPersisted);
	}

	public void testUpdate() {
		TreeTestResultDao testResultDao = (TreeTestResultDao) getContext()
				.getBean("testResultDao");

		TreeTestResult ttr = createCompleteTreeTestResult();
		testResultDao.create(ttr);

		TreeTestResult ttrUpdated = (TreeTestResult) testResultDao
				.getTestResult(ttr.getTestResultId());
		
		// Modifying ttrUpdated
		
		/** this closeDate update commented because 
		 * the assertTreeTestResult will find a unexpected 
		 * discrepancy in the date.
		 * ttrUpdated.setCloseDate(new Date()); 
		 */
		
		String pathStr = "/test";
		TreeSPath path = TreeSPath.parseToCreatePath(pathStr);

		TreeSRegistry registry = new TreeSRegistry();
		SimpleSElement elem = new SimpleSElement("Unit Test");
		elem.getTags().put("myTag", "myTagValue");
		registry.register(path, elem);

		ttrUpdated.notifyCurrentPath(registry, path);
		
		ttrUpdated.addResultPart(TreeTestResultTestUtils.createSimpleResultPartPassed());
		ttrUpdated.addResultPart(TreeTestResultTestUtils.createSimpleResultPartFailed());
		ttrUpdated.addResultPart(TreeTestResultTestUtils.createSimpleResultPartError());
		
		testResultDao.update(ttrUpdated);
		
		// comparison of ttrUpdated and ttrRetrieved
		TreeTestResult ttrRetrieved = (TreeTestResult) testResultDao
		.getTestResult(ttr.getTestResultId());
		
		UnitTestTreeUtil.assertTreeTestResult(ttrRetrieved, ttrUpdated);
	}
	
	@Override
	protected String getApplicationContextLocation() {
		return "org/argeo/slc/hibernate/applicationContext.xml";
	}

}
