package org.argeo.slc.web.mvc.result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;

public class ResultCollectionListController extends
		ParameterizableViewController {
	private final TreeTestResultCollectionDao testResultCollectionDao;

	public ResultCollectionListController(
			TreeTestResultCollectionDao testResultCollectionDao) {
		this.testResultCollectionDao = testResultCollectionDao;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView();

		// no need to retrieve since collection list is always in session

		// SortedSet<TreeTestResultCollection> results = testResultCollectionDao
		// .listCollections();
		// modelAndView.addObject("resultCollections", results);

		modelAndView.setViewName(getViewName());
		return modelAndView;
	}
}
