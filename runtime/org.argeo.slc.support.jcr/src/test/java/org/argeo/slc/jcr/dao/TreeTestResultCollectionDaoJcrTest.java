package org.argeo.slc.jcr.dao;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultCollection;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.argeo.slc.unit.test.tree.TreeTestResultTestUtils;
import org.argeo.slc.unit.test.tree.UnitTestTreeUtil;

public class TreeTestResultCollectionDaoJcrTest extends AbstractSpringTestCase {
	private final static Log log = LogFactory
			.getLog(TreeTestResultCollectionDaoJcrTest.class);

	private TreeTestResultCollectionDao ttrcDao;
	private TreeTestResultDao ttrDao;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ttrDao = getBean(TreeTestResultDao.class);
		ttrcDao = getBean(TreeTestResultCollectionDao.class);
		log.debug("Context Initialized");
	}

	@SuppressWarnings("restriction")
	public void testScenario() {
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

		assertEquals(1, ttrcPersist.getResults().size());
		TreeTestResult ttrFin = ttrcPersist.getResults().iterator().next();
		UnitTestTreeUtil.assertTreeTestResult(ttr2, ttrFin);
		
	}

	@SuppressWarnings("restriction")
	public void testResultsWithSameCloseDate() {
		String ttrcName = "testCollectionWithSameCloseDate";

		Date closeDate = new Date();

		// TTR1
		TreeTestResult ttr1 = TreeTestResultTestUtils
				.createCompleteTreeTestResult();
		ttr1.setCloseDate(closeDate);
		ttrDao.create(ttr1);

		// TTR2
		TreeTestResult ttr2 = TreeTestResultTestUtils
				.createCompleteTreeTestResult();
		ttr2.setCloseDate(closeDate);
		ttrDao.create(ttr2);

		// TTRC
		TreeTestResultCollection ttrc = new TreeTestResultCollection(ttrcName);
		ttrc.getResults().add(ttr1);
		ttrc.getResults().add(ttr2);
		ttrcDao.create(ttrc);

		final TreeTestResultCollection ttrcPersist = ttrcDao
				.getTestResultCollection(ttrcName);
	assertEquals(2, ttrcPersist.getResults().size());
	}
}
