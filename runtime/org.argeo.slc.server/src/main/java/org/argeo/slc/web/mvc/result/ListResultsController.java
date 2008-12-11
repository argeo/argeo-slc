package org.argeo.slc.web.mvc.result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.core.test.tree.TreeTestResultList;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

/** Lists results possibly filtering them. */
public class ListResultsController extends AbstractServiceController {
	private final TreeTestResultCollectionDao testResultCollectionDao;

	public ListResultsController(
			TreeTestResultCollectionDao testResultCollectionDao) {
		this.testResultCollectionDao = testResultCollectionDao;
	}

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {
		String collectionId = request.getParameter("collectionId");

		Map<String, String[]> parameterMap = request.getParameterMap();

		Map<String, String> attributes = new HashMap<String, String>();
		for (String parameter : parameterMap.keySet()) {
			if (parameter.startsWith("attr.")) {
				String key = parameter.substring("attr.".length());
				attributes.put(key, parameterMap.get(parameter)[0]);
			}
		}

		List<TreeTestResult> resultAttributes = testResultCollectionDao
				.listResults(collectionId, attributes);

		modelAndView.addObject("resultList", new TreeTestResultList(
				resultAttributes));
	}
}
