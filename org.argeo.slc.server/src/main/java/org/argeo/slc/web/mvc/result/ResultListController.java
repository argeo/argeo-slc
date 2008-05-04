package org.argeo.slc.web.mvc.result;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;

public class ResultListController extends ParameterizableViewController {
	private final TreeTestResultDao testResultDao;

	public ResultListController(TreeTestResultDao testResultDao) {
		this.testResultDao = testResultDao;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ModelAndView modelAndView = new ModelAndView();

		List<TreeTestResult> results = testResultDao.listTestResults();
		modelAndView.addObject("results", results);
		modelAndView.setViewName(getViewName());
		return modelAndView;
	}
}
