package org.argeo.slc.hibernate.structure.tree;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.structure.tree.TreeSRegistry;
import org.argeo.slc.dao.structure.tree.TreeSPathDao;
import org.argeo.slc.dao.structure.tree.TreeSRegistryDao;

/**
 * The Hibernate implementation for tree-based structure registry.
 * 
 * @see TreeSRegistry
 */
public class TreeSRegistryDaoHibernate extends HibernateDaoSupport implements
		TreeSRegistryDao {

	private TreeSPathDao treeSPathDao;

	private static Log log = LogFactory.getLog(TreeSRegistryDaoHibernate.class);

	public void create(TreeSRegistry registry) {
		getHibernateTemplate().save(registry);
	}

	public void update(TreeSRegistry registry) {
		getHibernateTemplate().update(registry);
	}

	public TreeSRegistry getActiveTreeSRegistry() {
		List<?> list = getHibernateTemplate().find(
				"from TreeSRegistry where status=?",
				TreeSRegistry.STATUS_ACTIVE);
		if (list.size() == 0) {
			return null;
		} else {
			return (TreeSRegistry) list.get(0);
		}
	}

	public void syncPath(TreeSRegistry registry,
			StructureRegistry<TreeSPath> localRegistry, TreeSPath path) {
		if (path.getParent() != null) {
			TreeSPath parent = treeSPathDao.getOrCreate(path.getParent());
			syncPath(registry, localRegistry, parent);
		}

		if (log.isTraceEnabled())
			log.trace("Synchronize path " + path);
		
		if (registry.getElement(path) == null) {
			if (localRegistry != null) {
				registry.register(path, getElement(registry, localRegistry, path));
			} else {
				registry.register(path, new SimpleSElement(path.getName()));
			}
			update(registry);
		} else {
			if (localRegistry != null) {
				StructureElement sElement = getElement(registry, localRegistry, path);
				if (sElement != null) {
					registry.register(path, sElement);
					update(registry);
				}
			}
		}

	}

	public void setTreeSPathDao(TreeSPathDao treeSPathDao) {
		this.treeSPathDao = treeSPathDao;
	}

	protected StructureElement getElement(TreeSRegistry registry,
			StructureRegistry<TreeSPath> localRegistry, TreeSPath path){
		return localRegistry.getElement(path);
	}
}
