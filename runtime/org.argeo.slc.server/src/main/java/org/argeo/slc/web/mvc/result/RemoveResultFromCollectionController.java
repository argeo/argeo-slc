package org.argeo.slc.web.mvc.result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.services.test.TestManagerService;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

public class RemoveResultFromCollectionController extends
		AbstractServiceController {
	private final TestManagerService testManagerService;

	public RemoveResultFromCollectionController(
			TestManagerService testManagerService) {
		this.testManagerService = testManagerService;
	}

	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {

		String collectionId = request.getParameter("collectionId");
		String resultUuid = request.getParameter("resultUuid");

		testManagerService.removeResultFromCollection(collectionId, resultUuid);
	}
}
