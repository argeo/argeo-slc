package org.argeo.slc.web.mvc.result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;
import org.argeo.slc.msg.ObjectList;
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
	@SuppressWarnings(value = { "unchecked" })
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {

		// log.debug("userPrincipal=" + request.getUserPrincipal());
		// log.debug("authType=" + request.getAuthType());
		// log.debug("remoteUser=" + request.getRemoteUser());
		// log.debug("cookies=" + request.getCookies());
		// log.debug("requestedSessionId=" + request.getRequestedSessionId());
		// log.debug("session.id=" + request.getSession().getId());

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

		modelAndView.addObject("resultList", new ObjectList(resultAttributes));
	}
}
