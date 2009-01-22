package org.argeo.slc.web.mvc.result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.core.test.tree.TreeTestResultCollection;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

public class GetResultCollectionController extends AbstractServiceController {
	private final TreeTestResultCollectionDao testResultCollectionDao;

	public GetResultCollectionController(
			TreeTestResultCollectionDao testResultCollectionDao) {
		this.testResultCollectionDao = testResultCollectionDao;
	}

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {

		String id = request.getParameter("collectionId");

		TreeTestResultCollection resultCollection = testResultCollectionDao
				.getTestResultCollection(id);

		modelAndView.addObject("resultCollection", resultCollection);
	}
}
