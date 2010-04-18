package org.argeo.slc.jcr.dao;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.attachment.SimpleAttachment;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.unit.AbstractSpringTestCase;
import org.argeo.slc.unit.test.tree.TreeTestResultTestUtils;
import org.argeo.slc.unit.test.tree.UnitTestTreeUtil;

public class TreeTestResultDaoJcrTest extends AbstractSpringTestCase {
	private final static Log log = LogFactory
			.getLog(TreeTestResultDaoJcrTest.class);

	private TreeTestResultDao ttrDao;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ttrDao = getBean(TreeTestResultDao.class);
		log.debug("Context Initialized");
	}

	@SuppressWarnings("restriction")
	public void testCreate() {
		TreeTestResult ttr = TreeTestResultTestUtils
				.createComplexeTreeTestResult();
		ttrDao.create(ttr);
		TreeTestResult ttrPersisted = ttrDao.getTestResult(ttr.getUuid());
		compareTestResult(ttr, ttrPersisted);
		UnitTestTreeUtil.assertTreeTestResult(ttr, ttrPersisted);
	}

	@SuppressWarnings("restriction")
	public void testUpdate() {
		TreeTestResult ttr = TreeTestResultTestUtils
				.createCompleteTreeTestResult();
		ttrDao.create(ttr);
		SimpleAttachment sa = new SimpleAttachment(
				UUID.randomUUID().toString(), "A new Attachment", "UTF8");
		// test if we can attach a new doc
		ttrDao.addAttachment(ttr.getUuid(), sa);

		// test if an existing doc is not added 2 times.
		TreeTestResult ttr2 = ttrDao.getTestResult(ttr.getUuid());
		ttrDao.addAttachment(ttr.getUuid(), sa);
		TreeTestResult ttr3 = ttrDao.getTestResult(ttr.getUuid());
		assertEquals(ttr3.getAttachments().size(), ttr2.getAttachments().size());

		// Test Add ResultPart
		// Initializing context
		SimpleResultPart resultPart = TreeTestResultTestUtils
				.createSimpleResultPartFailed();
		TreeSPath path = new TreeSPath("/test1");
		ttrDao.addResultPart(ttr.getUuid(), path, resultPart, null);

		// TestAdd Attribute
		Map<String, String> newAtt = new TreeMap<String, String>();
		newAtt.put("NewTestCase", "NonSortedView");
		newAtt.put("NewTestCaseType", "csvdiff");
		ttrDao.updateAttributes(ttr.getUuid(), newAtt);

		ttr2 = ttrDao.getTestResult(ttr.getUuid());
		assertEquals(ttr.getAttributes().size() + 2, ttr2.getAttributes()
				.size());
		assertEquals("csvdiff", ttr2.getAttributes().get("NewTestCaseType"));

		// Test update existing Attribute
		ttrDao.updateAttributes(ttr.getUuid(), newAtt);
		assertEquals(ttr.getAttributes().size() + 2, ttr2.getAttributes()
				.size());

	}
	@SuppressWarnings("restriction")
	public void testResultPartOnly() {
		
		TreeTestResult ttr = TreeTestResultTestUtils
				.createComplexeTreeTestResult();

		SimpleResultPart resultPart = TreeTestResultTestUtils
		.createSimpleResultPartPassed();
		ttr.addResultPart(resultPart);
		ttrDao.create(ttr);
		TreeTestResult ttr2;
		ttr2 = ttrDao.getTestResult(ttr.getUuid());
		assertEquals(ttr.getResultParts().size(), ttr2.getResultParts()
				.size());
	}

	public static void compareTestResult(final TreeTestResult t1,
			final TreeTestResult t2) {

		assertEquals(t1.getUuid(), t2.getUuid());
		assertEquals(t1.getCloseDate(), t2.getCloseDate());
		assertEquals(t1.getAttachments().size(), t2.getAttachments().size());
		assertEquals(t1.getAttributes().size(), t2.getAttributes().size());
		assertEquals(t1.getElements().size(), t2.getElements().size());
		// resultParts 
		assertEquals(t1.getResultParts().size(), t2.getResultParts().size());
		
		
		// TODO Add more check.
	}

}