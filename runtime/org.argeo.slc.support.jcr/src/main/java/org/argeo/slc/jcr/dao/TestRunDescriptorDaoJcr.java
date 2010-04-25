package org.argeo.slc.jcr.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.dao.test.TestRunDescriptorDao;
import org.argeo.slc.test.TestRunDescriptor;

public class TestRunDescriptorDaoJcr extends AbstractSlcJcrDao implements
		TestRunDescriptorDao {

	public TestRunDescriptor getTestRunDescriptor(String id) {
		// TODO: optimize query
		String queryString = "//testrun[@testRunUuid='" + id + "']";
		Query query = createQuery(queryString, Query.XPATH);
		Node node = JcrUtils.querySingleNode(query);
		if (node == null)
			return null;
		return (TestRunDescriptor) nodeMapper.load(node);
	}

	public void saveOrUpdate(TestRunDescriptor testRunDescriptor) {
		try {
			nodeMapper.save(getSession(), basePath(testRunDescriptor),
					testRunDescriptor);
			getSession().save();
		} catch (RepositoryException e) {
			throw new SlcException("Cannot import " + testRunDescriptor, e);
		}
	}

}
