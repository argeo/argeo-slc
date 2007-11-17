package org.argeo.slc.hibernate.structure.tree;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.structure.tree.TreeSPathDao;

/**
 * The Hibernate implementation for tree-based result of the test result dao.
 * 
 * @see TreeTestResult
 */
public class TreeSPathDaoHibernate extends HibernateDaoSupport implements
		TreeSPathDao {

	private static Log log = LogFactory.getLog(TreeSPathDaoHibernate.class);

	public void create(TreeSPath path) {
		createImpl(path);
	}

	
	private void createImpl(TreeSPath path) {
		if (path.getParent() != null) {
			TreeSPath parent = getTreeSPath(path.getParent()
					.getAsUniqueString());
			if (parent == null) {
				createImpl(path.getParent());
			}
		}
		getHibernateTemplate().save(path);
		if (log.isTraceEnabled())
			log.trace("Saved TreeSPath " + path.getAsUniqueString());
	}

	public TreeSPath getTreeSPath(String pathString) {
		if (log.isTraceEnabled())
			log.trace("Query path: " + pathString);
		List<?> list = getHibernateTemplate().find(
				"from TreeSPath where asUniqueString=?", pathString);
		if (list.size() == 0) {
			return null;
		} else {
			return (TreeSPath) list.get(0);
		}
	}

	public TreeSPath getOrCreate(TreeSPath pathTransient) {
		TreeSPath path = getTreeSPath(pathTransient
				.getAsUniqueString());
		if (path != null) {
			return path;
		} else {
			create(pathTransient);
			// TODO: optimize
			return getTreeSPath(pathTransient.getAsUniqueString());
		}

	}

}
