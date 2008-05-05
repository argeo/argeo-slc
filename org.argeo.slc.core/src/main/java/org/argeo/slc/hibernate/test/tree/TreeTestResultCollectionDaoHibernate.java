package org.argeo.slc.hibernate.test.tree;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import org.hibernate.SessionFactory;

import org.argeo.slc.core.test.tree.TreeTestResultCollection;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;

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

}
