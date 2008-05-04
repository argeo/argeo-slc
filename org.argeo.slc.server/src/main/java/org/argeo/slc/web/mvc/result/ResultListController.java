package org.argeo.slc.web.mvc.result;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

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

		Comparator<TreeTestResult> comparator = new Comparator<TreeTestResult>() {

			public int compare(TreeTestResult arg0, TreeTestResult arg1) {
				if (arg0.getCloseDate() != null && arg1.getCloseDate() != null) {
					return -arg0.getCloseDate().compareTo(arg1.getCloseDate());
				} else if (arg0.getCloseDate() != null
						&& arg1.getCloseDate() == null) {
					return 1;
				} else if (arg0.getCloseDate() == null
						&& arg1.getCloseDate() != null) {
					return -1;
				} else {
					return arg0.getUuid().compareTo(arg1.getUuid());
				}
			}
		};
		SortedSet<TreeTestResult> results = new TreeSet<TreeTestResult>(
				comparator);
		results.addAll(testResultDao.listTestResults());
		modelAndView.addObject("results", results);
		modelAndView.setViewName(getViewName());
		return modelAndView;
	}
}
