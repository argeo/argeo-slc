package org.argeo.slc.web.mvc.result;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.core.test.tree.ResultAttributes;
import org.argeo.slc.core.test.tree.ResultAttributesList;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

public class ListResultAttributesController extends AbstractServiceController {
	private final TreeTestResultCollectionDao testResultCollectionDao;

	public ListResultAttributesController(
			TreeTestResultCollectionDao testResultCollectionDao) {
		this.testResultCollectionDao = testResultCollectionDao;
	}

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {
		String collectionId = request.getParameter("id");

		List<ResultAttributes> resultAttributes = testResultCollectionDao
				.listResultAttributes(collectionId);

		modelAndView.addObject("resultAttributesList",
				new ResultAttributesList(resultAttributes));
	}
}
