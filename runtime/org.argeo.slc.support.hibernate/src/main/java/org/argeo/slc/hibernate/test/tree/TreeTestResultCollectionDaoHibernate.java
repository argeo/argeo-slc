package org.argeo.slc.hibernate.test.tree;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.test.tree.ResultAttributes;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultCollection;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/** Hibernate implementation of collections DAO. */
public class TreeTestResultCollectionDaoHibernate extends HibernateDaoSupport
		implements TreeTestResultCollectionDao {

	public void create(TreeTestResultCollection ttrCollection) {
		getHibernateTemplate().save(ttrCollection);
	}

	public TreeTestResultCollection getTestResultCollection(String id) {
		return (TreeTestResultCollection) getHibernateTemplate().get(
				TreeTestResultCollection.class, id);
	}

	public void update(TreeTestResultCollection ttrCollection) {
		getHibernateTemplate().update(ttrCollection);
	}

	public void delete(TreeTestResultCollection ttrCollection) {
		getHibernateTemplate().delete(ttrCollection);
	}

	@SuppressWarnings("unchecked")
	public SortedSet<TreeTestResultCollection> listCollections() {
		return new TreeSet<TreeTestResultCollection>(getHibernateTemplate()
				.find("from TreeTestResultCollection"));
	}

	public void addResultToCollection(final TreeTestResultCollection ttrc,
			final String resultUuid) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				session.lock(ttrc, LockMode.NONE);
				TreeTestResult ttr = (TreeTestResult) session.get(
						TreeTestResult.class, resultUuid);
				ttrc.getResults().add(ttr);
				session.update(ttrc);
				return ttrc;
			}
		});
	}

	public void removeResultFromCollection(final TreeTestResultCollection ttrc,
			final String resultUuid) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				session.lock(ttrc, LockMode.NONE);
				TreeTestResult ttr = (TreeTestResult) session.get(
						TreeTestResult.class, resultUuid);
				if (ttrc.getResults().remove(ttr))
					session.update(ttrc);
				return ttrc;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<ResultAttributes> listResultAttributes(String collectionId) {
		List<ResultAttributes> list;
		if (collectionId == null)
			list = getHibernateTemplate().find(
					"select new org.argeo.slc.core.test.tree.ResultAttributes(ttr)"
							+ " from TreeTestResult ttr");
		else
			list = getHibernateTemplate()
					.find(
							"select new org.argeo.slc.core.test.tree.ResultAttributes(ttr) "
									+ " from TreeTestResult ttr, TreeTestResultCollection ttrc "
									+ " where ttr in elements(ttrc.results) and ttrc.id=?",
							collectionId);

		return list;
	}

	@SuppressWarnings("unchecked")
	public List<TreeTestResult> listResults(String collectionId,
			Map<String, String> attributes) {
		List<TreeTestResult> list;

		if (collectionId == null) {
			if (attributes == null || attributes.size() == 0)
				list = getHibernateTemplate().find("from TreeTestResult");
			else if (attributes.size() == 1) {
				Map.Entry<String, String> entry = attributes.entrySet()
						.iterator().next();
				Object[] args = { entry.getKey(), entry.getValue() };
				list = getHibernateTemplate().find(
						"select ttr from TreeTestResult ttr"
								+ " where attributes[?]=?", args);
			} else {
				throw new SlcException(
						"Multiple attributes filter are currently not supported.");
			}
		} else {
			if (attributes == null || attributes.size() == 0)
				list = getHibernateTemplate()
						.find(
								"select ttr "
										+ " from TreeTestResult ttr, TreeTestResultCollection ttrc "
										+ " where ttr in elements(ttrc.results) and ttrc.id=?",
								collectionId);
			else if (attributes.size() == 1) {
				Map.Entry<String, String> entry = attributes.entrySet()
						.iterator().next();
				Object[] args = { collectionId, entry.getKey(),
						entry.getValue() };
				list = getHibernateTemplate()
						.find(
								"select ttr from TreeTestResult ttr, TreeTestResultCollection ttrc "
										+ " where ttr in elements(ttrc.results) and ttrc.id=?"
										+ " and attributes[?]=?", args);
			} else {
				throw new SlcException(
						"Multiple attributes filter are currently not supported.");
			}
		}
		return list;
	}
}
