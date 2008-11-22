package org.argeo.slc.hibernate.test.tree;

import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.hibernate.unit.HibernateTestCase;
import org.argeo.slc.unit.test.tree.TreeTestResultTestUtils;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

public class CastorTestResultDaoHibernateTest extends HibernateTestCase {

	public void testUnmarshallAndCreate() throws Exception {
		TreeTestResult ttr = TreeTestResultTestUtils
				.createCompleteTreeTestResult();

		StringResult result = new StringResult();
		getBean(Marshaller.class).marshal(ttr, result);

		StringSource source = new StringSource(result.toString());
		TreeTestResult ttrUnm = (TreeTestResult) getBean(Unmarshaller.class)
				.unmarshal(source);

		for (TreeSPath path : ttrUnm.getResultParts().keySet()) {
			log.debug("Path: " + path.getClass() + ": " + path);
		}

		TreeTestResultDao ttrDao = getBean(TreeTestResultDao.class);
		ttrDao.create(ttrUnm);
		TreeTestResult ttrPersist = ttrDao.getTestResult(ttr.getUuid());

		TreeTestResultDaoHibernateTest.assertInHibernate(
				getHibernateTemplate(), ttr, ttrPersist);
	}

	@Override
	protected String getApplicationContextLocation() {
		return "org/argeo/slc/hibernate/withCastor.xml";
	}

}
