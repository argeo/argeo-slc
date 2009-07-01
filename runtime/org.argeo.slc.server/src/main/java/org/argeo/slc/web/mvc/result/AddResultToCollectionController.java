package org.argeo.slc.web.mvc.result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.services.TestManagerService;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

public class AddResultToCollectionController extends AbstractServiceController {
	private final TestManagerService testManagerService;

	public AddResultToCollectionController(TestManagerService testManagerService) {
		this.testManagerService = testManagerService;
	}

	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {

		String collectionId = request.getParameter("collectionId");
		String resultUuid = request.getParameter("resultUuid");

		testManagerService.addResultToCollection(collectionId, resultUuid);
	}
}
