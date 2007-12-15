package org.argeo.slc.hibernate.structure.tree;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.structure.tree.TreeSRegistry;
import org.argeo.slc.dao.structure.tree.TreeSRegistryDao;

/**
 * The Hibernate implementation for tree-based structure registry.
 * 
 * @see TreeSRegistry
 */
public class TreeSRegistryDaoHibernate extends HibernateDaoSupport implements
		TreeSRegistryDao {

	// private static Log log =
	// LogFactory.getLog(TreeSRegistryDaoHibernate.class);

	public void create(TreeSRegistry registry) {
		getHibernateTemplate().save(registry);
	}

	public void update(TreeSRegistry registry) {
		getHibernateTemplate().update(registry);
	}

	public TreeSRegistry getActiveTreeSRegistry() {
		List<?> list = getHibernateTemplate().find(
				"from TreeSRegistry where status=?", TreeSRegistry.STATUS_ACTIVE);
		if (list.size() == 0) {
			return null;
		} else {
			return (TreeSRegistry) list.get(0);
		}
	}

}
