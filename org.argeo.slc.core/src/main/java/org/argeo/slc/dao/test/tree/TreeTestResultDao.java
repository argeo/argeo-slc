package org.argeo.slc.dao.test.tree;

import java.util.List;
import java.util.Map;

import org.argeo.slc.core.structure.StructureElement;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.test.SimpleResultPart;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.TestResultDao;

public interface TreeTestResultDao extends TestResultDao<TreeTestResult> {
	/** Lists results containing this path */
	public List<TreeTestResult> listResults(TreeSPath path);

	public void addResultPart(String testResultId, TreeSPath path,
			SimpleResultPart resultPart,
			Map<TreeSPath, StructureElement> relatedElements);
}
