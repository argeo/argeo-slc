package org.argeo.slc.dao.test.tree;

import java.util.List;

import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.TestResultDao;

public interface TreeTestResultDao extends TestResultDao {
	/** Lists results containing this path*/
	public List<TreeTestResult> listResults(TreeSPath path);
}
