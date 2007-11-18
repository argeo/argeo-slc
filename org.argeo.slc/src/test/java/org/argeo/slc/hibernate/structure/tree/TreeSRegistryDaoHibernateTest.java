package org.argeo.slc.hibernate.structure.tree;

import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.structure.tree.TreeSRegistry;
import org.argeo.slc.dao.structure.tree.TreeSPathDao;
import org.argeo.slc.dao.structure.tree.TreeSRegistryDao;
import org.argeo.slc.unit.SpringBasedTestCase;

public class TreeSRegistryDaoHibernateTest extends SpringBasedTestCase {

	public void testCreate() {
		TreeSRegistryDao treeSRegistryDao = (TreeSRegistryDao) getContext()
		.getBean("treeSRegistryDao");
		
		TreeSPathDao treeSPathDao = (TreeSPathDao) getContext().getBean(
		"treeSPathDao");

		TreeSPath root = TreeSPath.parseToCreatePath("/root");
		TreeSPath path = root.createChild("test");
		
		treeSPathDao.create(root);
		treeSPathDao.create(path);
		
		TreeSRegistry registry = new TreeSRegistry();
		registry.setRoot(root);
		String desc= "desc";
		registry.register(path, new SimpleSElement(desc));
		
		treeSRegistryDao.create(registry);
		
		TreeSRegistry registry2 = treeSRegistryDao.getTreeSRegistry(path);
		SimpleSElement element = registry2.getElements().get(path);
		assertEquals(desc, element.getDescription());
	}

	@Override
	protected String getApplicationContextLocation() {
		return "org/argeo/slc/hibernate/applicationContext.xml";
	}

}
