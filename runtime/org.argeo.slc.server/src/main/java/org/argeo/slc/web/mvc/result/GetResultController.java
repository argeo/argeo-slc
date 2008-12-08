package org.argeo.slc.web.mvc.result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.core.test.tree.TreeTestResult;
import org.argeo.slc.dao.test.tree.TreeTestResultDao;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

/** Returns one single result. */
public class GetResultController extends AbstractServiceController {
	public final static String MODELKEY_RESULT = "result";

	private final TreeTestResultDao testResultDao;

	public GetResultController(TreeTestResultDao testResultDao) {
		this.testResultDao = testResultDao;
	}

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {

		String uuid = request.getParameter("uuid");
		TreeTestResult result = testResultDao.getTestResult(uuid);

		modelAndView.addObject(MODELKEY_RESULT, result);
	}

}
