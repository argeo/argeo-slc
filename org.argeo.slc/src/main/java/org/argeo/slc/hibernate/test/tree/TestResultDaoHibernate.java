package org.argeo.slc.hibernate.test.tree;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import org.argeo.slc.core.test.NumericTRId;
import org.argeo.slc.core.test.TestResult;
import org.argeo.slc.core.test.TestResultId;
import org.argeo.slc.dao.test.TestResultDao;

/**
 * The Hibernate implementation for tree-based result of the test result dao.
 * 
 * @see TreeTestResult
 */
public class TestResultDaoHibernate extends HibernateDaoSupport implements
		TestResultDao {

	public void create(TestResult testResult) {
		getHibernateTemplate().save(testResult);
	}

	public void update(TestResult testResult) {
		getHibernateTemplate().saveOrUpdate(testResult);
	}

	public TestResult getTestResult(TestResultId id) {
		NumericTRId ntrid = (NumericTRId) id;
		List<?> list = getHibernateTemplate().find(
				"from TreeTestResult where numericResultId.value=?",
				ntrid.getValue());
		if (list.size() == 0) {
			return null;
		} else {
			return (TestResult) list.get(0);
		}

	}

	public List<TestResult> listTestResults() {
		return (List<TestResult>) getHibernateTemplate().find(
				"from TreeTestResult");
	}

}
