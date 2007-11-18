package org.argeo.slc.core.test.tree;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.structure.SimpleSElement;
import org.argeo.slc.core.structure.StructureRegistry;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.core.structure.tree.TreeSRegistry;
import org.argeo.slc.dao.structure.tree.TreeSPathDao;
import org.argeo.slc.dao.structure.tree.TreeSRegistryDao;
import org.argeo.slc.dao.test.TestResultDao;

/**
 * Listener persisting tree-based results.
 * 
 * @see TreeTestResult
 */
public class TreeTestResultPersister extends AsynchronousTreeTestResultListener {
	private static Log log = LogFactory.getLog(TreeTestResultPersister.class);

	private TestResultDao testResultDao;
	private TreeSPathDao treeSPathDao;
	private TreeSRegistryDao treeSRegistryDao;

	@Override
	protected void resultPartAdded(PartStruct partStruct) {
		try {
			TreeTestResult persistedResult = (TreeTestResult) testResultDao
					.getTestResult(partStruct.resultId);

			TreeSPath path = treeSPathDao.getOrCreate(partStruct.path);

			TreeSRegistry registry = getOrCreateTreeSRegistry(path);
			if (registry.getElement(path) == null) {
				StructureRegistry localRegistry = partStruct.result
						.getRegistry();
				if (localRegistry != null) {
					registry.register(path, localRegistry.getElement(path));
				} else {
					registry.register(path, new SimpleSElement(path.getName()));
				}
				treeSRegistryDao.update(registry);
			}

			if (persistedResult == null) {
				persistedResult = new TreeTestResult();
				persistedResult.setNumericResultId(partStruct.resultId);
				PartSubList subList = new PartSubList();
				subList.getParts().add(partStruct.part);
				persistedResult.getResultParts().put(path, subList);

				testResultDao.create(persistedResult);
			} else {
				PartSubList subList = persistedResult.getResultParts()
						.get(path);
				if (subList == null) {
					subList = new PartSubList();
					persistedResult.getResultParts().put(path, subList);
				}
				persistedResult.getResultParts().get(path).getParts().add(
						partStruct.part);

				if (log.isTraceEnabled()) {
					log.trace("ResultId:" + persistedResult.getTestResultId());
					log.trace("ResultParts size:"
							+ persistedResult.getResultParts().size());
					log.trace("Sublist size:" + subList.getParts().size());
					log.trace("Part: " + partStruct.part);
				}
				testResultDao.update(persistedResult);
			}
		} catch (Exception e) {
			log.error("Could not persist part for result #"
					+ partStruct.resultId, e);
		}
	}

	@Override
	protected void postClose(TreeTestResult testResult) {
		TreeTestResult persistedResult = (TreeTestResult) testResultDao
				.getTestResult(testResult.getTestResultId());

		if (persistedResult != null) {
			persistedResult.setCloseDate(testResult.getCloseDate());
			testResultDao.update(persistedResult);
		}
		if (log.isDebugEnabled())
			log.debug("Closed result persister for result "
					+ testResult.getNumericResultId());
	}

	private TreeSRegistry getOrCreateTreeSRegistry(TreeSPath path) {
		TreeSRegistry registry = treeSRegistryDao.getTreeSRegistry(path);
		if (registry == null) {
			registry = new TreeSRegistry();
			TreeSPath root = treeSPathDao.getOrCreate(path.getRoot());
			registry.setRoot(root);
			treeSRegistryDao.create(registry);
			return treeSRegistryDao.getTreeSRegistry(path);
		} else {
			return registry;
		}
	}

	/** Sets the DAO to use in order to persist the results. */
	public void setTestResultDao(TestResultDao testResultDao) {
		this.testResultDao = testResultDao;
	}

	/** Sets the tree structure path DAO. */
	public void setTreeSPathDao(TreeSPathDao treeSPathDao) {
		this.treeSPathDao = treeSPathDao;
	}

	/** Sets the tree structure registry DAO. */
	public void setTreeSRegistryDao(TreeSRegistryDao treeSRegistryDao) {
		this.treeSRegistryDao = treeSRegistryDao;
	}

}
