package org.argeo.slc.hibernate.structure.tree;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.structure.tree.TreeSRegistry;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.structure.tree.TreeSPathDao;
import org.argeo.slc.dao.structure.tree.TreeSRegistryDao;

/**
 * The Hibernate implementation for tree-based result of the test result dao.
 * 
 * @see TreeTestResult
 */
public class TreeSRegistryDaoHibernate extends HibernateDaoSupport implements
		TreeSRegistryDao {

	//private static Log log = LogFactory.getLog(TreeSRegistryDaoHibernate.class);

	public void create(TreeSRegistry registry) {
		getHibernateTemplate().save(registry);
	}

	public TreeSRegistry getTreeSRegistry(TreeSPath treeSPath) {
		List<?> list = getHibernateTemplate().find(
				"from TreeSRegistry where root.asUniqueString=?",
				treeSPath.getRoot().getAsUniqueString());
		if (list.size() == 0) {
			return null;
		} else {
			return (TreeSRegistry) list.get(0);
		}
	}

}
