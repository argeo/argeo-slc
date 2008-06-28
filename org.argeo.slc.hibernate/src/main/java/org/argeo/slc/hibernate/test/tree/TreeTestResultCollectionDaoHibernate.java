package org.argeo.slc.hibernate.test.tree;

import java.sql.SQLException;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultCollection;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.action.Executable;

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

}
