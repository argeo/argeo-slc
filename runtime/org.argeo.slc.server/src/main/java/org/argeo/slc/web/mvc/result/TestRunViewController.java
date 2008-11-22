package org.argeo.slc.web.mvc.result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import org.argeo.slc.core.test.TestRunDescriptor;
import org.argeo.slc.dao.test.TestRunDescriptorDao;

public class TestRunViewController extends ParameterizableViewController {
	private final TestRunDescriptorDao testRunDescriptorDao;

	public TestRunViewController(TestRunDescriptorDao testRunDescriptorDao) {
		this.testRunDescriptorDao = testRunDescriptorDao;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String uuid = request.getParameter("uuid");
		TestRunDescriptor testRunDescriptor = testRunDescriptorDao
				.getTestRunDescriptor(uuid);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("testRunDescriptor", testRunDescriptor);
		modelAndView.setViewName(getViewName());
		return modelAndView;
	}

}
