package org.argeo.slc.core.test.tree;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.dao.test.TestResultDao;

public class TreeTestResultPersister extends AsynchronousTreeTestResultListener {
	private static Log log = LogFactory.getLog(TreeTestResultPersister.class);

	private TestResultDao testResultDao;

	@Override
	protected void resultPartAdded(PartStruct partStruct) {
		try {
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
					persistedResult.getResultParts().put(partStruct.path,
							subList);
				}
				persistedResult.getResultParts().get(partStruct.path)
						.getParts().add(partStruct.part);

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

	public void setTestResultDao(TestResultDao testResultDao) {
		this.testResultDao = testResultDao;
	}

}
