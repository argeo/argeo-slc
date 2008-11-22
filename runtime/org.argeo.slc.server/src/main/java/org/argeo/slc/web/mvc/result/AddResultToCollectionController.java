package org.argeo.slc.web.mvc.result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.services.test.TestManagerService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

public class AddResultToCollectionController extends
		ParameterizableViewController {
	private final TestManagerService testManagerService;

	public AddResultToCollectionController(TestManagerService testManagerService) {
		this.testManagerService = testManagerService;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String collectionId = request.getParameter("collectionId");
		String resultUuid = request.getParameter("resultUuid");

		testManagerService.addResultToCollection(collectionId, resultUuid);

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName(getViewName());
		return modelAndView;
	}
}
