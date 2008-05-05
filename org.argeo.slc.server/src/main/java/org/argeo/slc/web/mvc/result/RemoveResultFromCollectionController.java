package org.argeo.slc.web.mvc.result;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultCollection;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.services.test.TestManagerService;

public class RemoveResultFromCollectionController extends
		ParameterizableViewController {
	private final TestManagerService testManagerService;

	public RemoveResultFromCollectionController(
			TestManagerService testManagerService) {
		this.testManagerService = testManagerService;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String collectionId = request.getParameter("collectionId");
		String resultUuid = request.getParameter("resultUuid");

		testManagerService.removeResultFromCollection(collectionId, resultUuid);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(getViewName());
		return modelAndView;
	}
}
