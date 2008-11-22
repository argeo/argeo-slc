package org.argeo.slc.hibernate.test;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import org.argeo.slc.dao.test.TestRunDescriptorDao;
import org.argeo.slc.test.TestRunDescriptor;

public class TestRunDescriptorDaoHibernate extends HibernateDaoSupport
		implements TestRunDescriptorDao {

	public TestRunDescriptor getTestRunDescriptor(String id) {
		return (TestRunDescriptor) getHibernateTemplate().get(
				TestRunDescriptor.class, id);
	}

	public void saveOrUpdate(TestRunDescriptor testRunDescriptor) {
		getHibernateTemplate().saveOrUpdate(testRunDescriptor);
	}

}
