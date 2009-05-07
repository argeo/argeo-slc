package org.argeo.slc.hibernate.test.tree;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.attachment.SimpleAttachment;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.tree.PartSubList;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.structure.StructureElement;
import org.argeo.slc.test.TestResult;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.Assert;

/**
 * The Hibernate implementation for tree-based result of the test result dao.
 * 
 * @see TreeTestResult
 */
public class TreeTestResultDaoHibernate extends HibernateDaoSupport implements
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

	public void close(final String testResultId, final Date closeDate) {
		getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				TreeTestResult treeTestResult = getTreeTestResult(session,
						testResultId);
				treeTestResult.setCloseDate(closeDate);
				session.update(treeTestResult);
				return treeTestResult;
			}
		});
	}

	public void addResultPart(final String testResultId, final TreeSPath path,
			final SimpleResultPart resultPart,
			final Map<TreeSPath, StructureElement> relatedElements) {

		getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				TreeTestResult treeTestResult = getTreeTestResult(session,
						testResultId);
				PartSubList lst = treeTestResult.getResultParts().get(path);
				if (lst == null) {
					lst = new PartSubList();
					treeTestResult.getResultParts().put(path, lst);
				}
				lst.getParts().add(resultPart);
				if (relatedElements != null)
					treeTestResult.getElements().putAll(relatedElements);

				session.update(treeTestResult);
				return treeTestResult;
			}
		});

	}

	public void addAttachment(final String testResultId,
			final SimpleAttachment attachment) {
		getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				TreeTestResult treeTestResult = getTreeTestResult(session,
						testResultId);

				// Check is there is already such an attachment
				Assert.notNull(attachment, "attachment is null");
				Assert.notNull(session, "session is null");
				SimpleAttachment att = (SimpleAttachment) session.get(
						attachment.getClass(), attachment.getUuid());
				if (att != null)
					throw new SlcException(
							"There is already an attachement with id "
									+ attachment.getUuid());

				treeTestResult.getAttachments().add(attachment);
				session.update(treeTestResult);
				return treeTestResult;
			}
		});

	}

	protected TreeTestResult getTreeTestResult(Session session,
			String testResultId) {
		TreeTestResult treeTestResult = (TreeTestResult) session.get(
				TreeTestResult.class, testResultId);
		if (treeTestResult == null)
			throw new SlcException("No result with id " + testResultId);
		return treeTestResult;
	}

	public void updateAttributes(final String testResultId,
			final Map<String, String> attributes) {
		getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				TreeTestResult treeTestResult = getTreeTestResult(session,
						testResultId);
				treeTestResult.setAttributes(attributes);

				session.update(treeTestResult);
				return treeTestResult;
			}
		});
	}

}
