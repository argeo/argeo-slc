package org.argeo.slc.hibernate.test.tree;

import java.sql.SQLException;

import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultCollection;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.hibernate.unit.HibernateTestCase;
import org.argeo.slc.unit.test.tree.TreeTestResultTestUtils;
import org.argeo.slc.unit.test.tree.UnitTestTreeUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

public class TreeTestResultCollectionDaoHibernateTest extends HibernateTestCase {

	public void testScenario() {
		TreeTestResultDao ttrDao = getBean(TreeTestResultDao.class);
		TreeTestResultCollectionDao ttrcDao = getBean(TreeTestResultCollectionDao.class);

		String ttrcName = "testCollection";

		TreeTestResult ttr1 = TreeTestResultTestUtils
				.createCompleteTreeTestResult();
		ttrDao.create(ttr1);

		TreeTestResultCollection ttrc = new TreeTestResultCollection(ttrcName);
		ttrcDao.create(ttrc);

		ttrc.getResults().add(ttr1);
		ttrcDao.update(ttrc);

		final TreeTestResult ttr2 = TreeTestResultTestUtils
				.createCompleteTreeTestResult();
		ttrDao.create(ttr2);

		ttrc.getResults().add(ttr2);
		ttrcDao.update(ttrc);

		ttrc.getResults().remove(ttr1);
		ttrcDao.update(ttrc);

		final TreeTestResultCollection ttrcPersist = ttrcDao
				.getTestResultCollection(ttrcName);

		getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				session.refresh(ttrcPersist);
				assertEquals(1, ttrcPersist.getResults().size());
				UnitTestTreeUtil.assertTreeTestResult(ttr2, ttrcPersist
						.getResults().iterator().next());
				return null;
			}
		});
	}

	@Override
	protected String getApplicationContextLocation() {
		return "org/argeo/slc/hibernate/applicationContext.xml";
	}

}
