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
import org.hibernate.Session;

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
		Session session = getSession();
		if (log.isTraceEnabled())
			log.trace("Session#" + session.hashCode() + " " + session);
		syncPathImpl(registry, localRegistry, path, session);
		session.update(registry);
		// update(registry);
	}

	private void syncPathImpl(TreeSRegistry registry,
			StructureRegistry<TreeSPath> localRegistry, TreeSPath path,
			Session session) {
		if (path.getParent() != null) {
			TreeSPath parent = treeSPathDao.getOrCreate(path.getParent());
			syncPathImpl(registry, localRegistry, parent, session);
		}

		if (log.isTraceEnabled())
			log.trace("Synchronize path " + path);

		if (registry.getElement(path) == null) {
			final StructureElement element = getElement(registry,
					localRegistry, path);
			StructureElement elementPersisted = (StructureElement) session
					.merge(element);
			registry.register(path, elementPersisted);
			// update(registry);

			if (log.isTraceEnabled())
				log.trace("No element in persisted structure for " + path
						+ ", merged to " + elementPersisted);
		} else {
			if (localRegistry != null) {
				StructureElement element = getElement(registry, localRegistry,
						path);

				if (element != null) {
					StructureElement elementPersisted = (StructureElement) session
							.merge(element);
					registry.register(path, elementPersisted);
					// update(registry);
					if (log.isTraceEnabled())
						log
								.trace("Update existing element in persisted structure for "
										+ path
										+ ", merged to "
										+ elementPersisted);
				}
			}
		}

	}

	public void setTreeSPathDao(TreeSPathDao treeSPathDao) {
		this.treeSPathDao = treeSPathDao;
	}

	protected StructureElement getElement(TreeSRegistry registry,
			StructureRegistry<TreeSPath> localRegistry, TreeSPath path) {
		StructureElement element;
		if (localRegistry != null) {
			element = localRegistry.getElement(path);
			if (getSession().getSessionFactory().getClassMetadata(
					element.getClass()) == null) {
				if (log.isTraceEnabled())
					log.trace("Replace non-hibernate element " + element
							+ " by a simple element.");
				element = new SimpleSElement(element.getLabel());
			}
		} else {
			element = new SimpleSElement(path.getName());
		}
		return element;
	}
}
