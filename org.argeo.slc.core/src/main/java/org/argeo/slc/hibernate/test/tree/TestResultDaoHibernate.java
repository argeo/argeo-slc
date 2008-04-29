package org.argeo.slc.hibernate.test.tree;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.NumericTRId;
import org.argeo.slc.core.test.TestResult;
import org.argeo.slc.core.test.TestResultId;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;

/**
 * The Hibernate implementation for tree-based result of the test result dao.
 * 
 * @see TreeTestResult
 */
public class TestResultDaoHibernate extends HibernateDaoSupport implements
		TreeTestResultDao {

	public void create(TestResult testResult) {
		getHibernateTemplate().save(testResult);
	}

	public void update(TestResult testResult) {
		getHibernateTemplate().saveOrUpdate(testResult);
	}

	public TreeTestResult getTestResult(String uuid) {
		List<?> list = getHibernateTemplate().find(
				"from TreeTestResult where uuid=?", uuid);
		if (list.size() == 0) {
			return null;
		} else {
			return (TreeTestResult) list.get(0);
		}

	}

	public List<TreeTestResult> listTestResults() {
		return (List<TreeTestResult>) getHibernateTemplate().find(
				"from TreeTestResult");
	}

	public List<TreeTestResult> listResults(TreeSPath path) {
		List<TreeTestResult> list = getHibernateTemplate().find(
				"from TreeTestResult res where ? in indices(res.resultParts)",
				path.getAsUniqueString());
		return list;
	}

}
