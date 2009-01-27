package org.argeo.slc.web.mvc.result;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultCollection;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
import org.argeo.slc.services.test.TestManagerService;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.servlet.ModelAndView;

public class RemoveResultFromCollectionController extends
		AbstractServiceController {
	private final TreeTestResultCollectionDao testResultCollectionDao;
	private final TestManagerService testManagerService;

	public RemoveResultFromCollectionController(
			TreeTestResultCollectionDao testResultCollectionDao,
			TestManagerService testManagerService) {
		this.testResultCollectionDao = testResultCollectionDao;
		this.testManagerService = testManagerService;
	}

	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {

		String collectionId = request.getParameter("collectionId");
		String[] resultUuids = request.getParameterValues("resultUuid");
		String[] attrNames = request.getParameterValues("attrName");
		String[] attrPatterns = request.getParameterValues("attrPattern");

		// Checks
		if (collectionId == null)
			throw new SlcException("A collection id must be specified");
		if (attrNames != null
				&& (attrPatterns == null || attrNames.length != attrPatterns.length))
			throw new SlcException(
					"There must be as many attrName as attrPatterns");

		// Remove specified results
		if (resultUuids != null)
			for (String resultUuid : resultUuids)
				testManagerService.removeResultFromCollection(collectionId,
						resultUuid);

		if (attrNames != null) {
			TreeTestResultCollection sourceCollection = testResultCollectionDao
					.getTestResultCollection(collectionId);

			int index = 0;
			for (String attrName : attrNames) {
				String attrPattern = attrPatterns[index];// safe: checked above

				List<TreeTestResult> results = new ArrayList<TreeTestResult>(
						sourceCollection.getResults());
				for (TreeTestResult treeTestResult : results) {
					if (PatternMatchUtils.simpleMatch(attrPattern,
							treeTestResult.getAttributes().get(attrName))) {
						testManagerService.removeResultFromCollection(
								collectionId, treeTestResult.getUuid());
					}
				}
				index++;
			}
		} else {
			if (resultUuids == null) {// no specs
				// remove all
				// TODO: optimize
				TreeTestResultCollection sourceCollection = testResultCollectionDao
						.getTestResultCollection(collectionId);
				List<TreeTestResult> results = new ArrayList<TreeTestResult>(
						sourceCollection.getResults());
				for (TreeTestResult treeTestResult : results) {
					testManagerService.removeResultFromCollection(collectionId,
							treeTestResult.getUuid());
				}

			}
		}

	}
}
