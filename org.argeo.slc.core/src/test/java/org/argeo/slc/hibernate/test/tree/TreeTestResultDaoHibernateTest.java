package org.argeo.slc.hibernate.test.tree;

import static org.argeo.slc.core.test.tree.TreeTestResultTestUtils.createCompleteTreeTestResult;

import org.argeo.slc.core.test.tree.TreeTestResult;
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

	@Override
	protected String getApplicationContextLocation() {
		return "org/argeo/slc/hibernate/applicationContext.xml";
	}

}
