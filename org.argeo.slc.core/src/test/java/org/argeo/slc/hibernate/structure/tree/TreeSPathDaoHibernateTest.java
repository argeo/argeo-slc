package org.argeo.slc.hibernate.structure.tree;

import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.dao.structure.tree.TreeSPathDao;
import org.argeo.slc.unit.AbstractSpringTestCase;

public class TreeSPathDaoHibernateTest extends AbstractSpringTestCase {

	public void testCreate() {
		TreeSPathDao treeSPathDao = getBean(TreeSPathDao.class);

		String pathParentStr = "/root/testParent";
		String pathStr = pathParentStr + "/test";
		TreeSPath path = TreeSPath.parseToCreatePath(pathStr);
		treeSPathDao.create(path);

		TreeSPath pathChild = treeSPathDao.getTreeSPath(pathStr);
		assertEquals(path, pathChild);

		TreeSPath pathParent = treeSPathDao.getTreeSPath(pathParentStr);
		assertEquals(path.getParent(), pathParent);
	}

	@Override
	protected String getApplicationContextLocation() {
		return "org/argeo/slc/hibernate/applicationContext.xml";
	}

}
