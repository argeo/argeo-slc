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

public class ResultCollectionViewController extends
		ParameterizableViewController {
	private final TreeTestResultCollectionDao testResultCollectionDao;

	public ResultCollectionViewController(
			TreeTestResultCollectionDao testResultCollectionDao) {
		this.testResultCollectionDao = testResultCollectionDao;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String id = request.getParameter("id");

		TreeTestResultCollection resultCollection = testResultCollectionDao
				.getTestResultCollection(id);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("resultCollection", resultCollection);
		modelAndView.setViewName(getViewName());
		return modelAndView;
	}
}
