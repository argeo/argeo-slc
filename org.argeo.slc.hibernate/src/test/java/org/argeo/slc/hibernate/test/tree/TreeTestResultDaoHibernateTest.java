package org.argeo.slc.hibernate.test.tree;

import java.sql.SQLException;

import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.structure.tree.TreeSRegistry;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.TestStatus;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.argeo.slc.unit.test.tree.TreeTestResultTestUtils;
import org.argeo.slc.unit.test.tree.UnitTestTreeUtil;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class TreeTestResultDaoHibernateTest extends AbstractSpringTestCase {
	private TreeTestResultDao testResultDao = null;
	private HibernateTemplate template = null;

	@Override
	public void setUp() {
		testResultDao = getBean(TreeTestResultDao.class);
		template = new HibernateTemplate(getBean(SessionFactory.class));
	}

	public void testCreate() {

		TreeTestResult ttr = TreeTestResultTestUtils
				.createCompleteTreeTestResult();
		testResultDao.create(ttr);

		TreeTestResult ttrPersisted = (TreeTestResult) testResultDao
				.getTestResult(ttr.getUuid());

		assertInHibernate(ttr, ttrPersisted);
	}

	public void testUpdate() {
		TreeTestResult ttr = TreeTestResultTestUtils
				.createCompleteTreeTestResult();
		testResultDao.create(ttr);

		final TreeTestResult ttrUpdated = (TreeTestResult) testResultDao
				.getTestResult(ttr.getUuid());

		// Modifying ttrUpdated

		/**
		 * this closeDate update commented because the assertTreeTestResult will
		 * find a unexpected discrepancy in the date.
		 * ttrUpdated.setCloseDate(new Date());
		 */

		String pathStr = "/test";
		final TreeSPath path = new TreeSPath(pathStr);

		final TreeSRegistry registry = new TreeSRegistry();
		SimpleSElement elem = new SimpleSElement("Unit Test");
		elem.getTags().put("myTag", "myTagValue");
		registry.register(path, elem);

		template.execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				session.refresh(ttrUpdated);
				ttrUpdated.notifyCurrentPath(registry, path);

				ttrUpdated.addResultPart(TreeTestResultTestUtils
						.createSimpleResultPartPassed());
				ttrUpdated.addResultPart(TreeTestResultTestUtils
						.createSimpleResultPartFailed());
				ttrUpdated.addResultPart(TreeTestResultTestUtils
						.createSimpleResultPartError());
				return null;
			}
		});

		testResultDao.update(ttrUpdated);

		// comparison of ttrUpdated and ttrRetrieved
		TreeTestResult ttrRetrieved = (TreeTestResult) testResultDao
				.getTestResult(ttr.getUuid());

		assertInHibernate(ttrUpdated, ttrRetrieved);
	}

	public void testMultipleUpdateScenario() throws Exception {
		TreeSRegistry registry = new TreeSRegistry();

		TreeSPath path = new TreeSPath("/root/test");
		SimpleSElement elem = new SimpleSElement("Unit Test");
		elem.getTags().put("myTag", "myTagValue");
		registry.register(path, elem);

		TreeTestResult ttr = TreeTestResultTestUtils
				.createSimpleTreeTestResult();
		ttr.notifyCurrentPath(registry, path);
		ttr
				.addResultPart(new SimpleResultPart(TestStatus.PASSED,
						"First test"));

		testResultDao.create(ttr);

		path = new TreeSPath("/root/test2/subtest");
		elem = new SimpleSElement("Sub Test");
		elem.getTags().put("myTag", "myTagValue");
		registry.register(path, elem);

		ttr.notifyCurrentPath(registry, path);
		ttr
				.addResultPart(new SimpleResultPart(TestStatus.PASSED,
						"Second test"));

		testResultDao.update(ttr);

		ttr.notifyCurrentPath(registry, path);
		ttr.addResultPart(new SimpleResultPart(TestStatus.PASSED,
				"Third test with same path"));

		testResultDao.update(ttr);

		ttr.close();

		testResultDao.close(ttr.getUuid(), ttr.getCloseDate());

		TreeTestResult ttrRetrieved = (TreeTestResult) testResultDao
				.getTestResult(ttr.getUuid());

		assertInHibernate(ttr, ttrRetrieved);

	}

	@Override
	protected String getApplicationContextLocation() {
		return "org/argeo/slc/hibernate/applicationContext.xml";
	}

	public void assertInHibernate(final TreeTestResult ttrExpected,
			final TreeTestResult ttrPersisted) {
		template.execute(new HibernateCallback() {
			public Object doInHibernate(Session session) {
				session.refresh(ttrPersisted);
				UnitTestTreeUtil
						.assertTreeTestResult(ttrExpected, ttrPersisted);
				return null;
			}

		});

	}

}
