package org.argeo.slc.core.test.tree;

import org.argeo.slc.dao.test.TestResultDao;

public class TreeTestResultPersister extends AsynchronousTreeTestResultListener {
	private TestResultDao testResultDao;

	@Override
	protected void resultPartAdded(PartStruct partStruct) {
		TreeTestResult persistedResult = (TreeTestResult) testResultDao
				.getTestResult(partStruct.resultId);
		if (persistedResult == null) {
			persistedResult = new TreeTestResult();
			persistedResult.setNumericResultId(partStruct.resultId);
			PartSubList subList = new PartSubList();
			subList.getParts().add(partStruct.part);
			persistedResult.getResultParts().put(partStruct.path, subList);

			testResultDao.create(persistedResult);
		} else {
			PartSubList subList = persistedResult.getResultParts().get(
					partStruct.path);
			if (subList == null) {
				subList = new PartSubList();
				persistedResult.getResultParts().put(partStruct.path, subList);
			}
			subList.getParts().add(partStruct.part);

			testResultDao.update(persistedResult);
		}
	}

	public void setTestResultDao(TestResultDao testResultDao) {
		this.testResultDao = testResultDao;
	}

}
