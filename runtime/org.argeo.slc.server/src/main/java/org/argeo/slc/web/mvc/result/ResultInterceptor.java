package org.argeo.slc.web.mvc.result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import org.argeo.slc.dao.test.tree.TreeTestResultCollectionDao;

public class ResultInterceptor extends HandlerInterceptorAdapter {
	private final TreeTestResultCollectionDao testResultCollectionDao;

	public ResultInterceptor(TreeTestResultCollectionDao testResultCollectionDao) {
		this.testResultCollectionDao = testResultCollectionDao;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		request.setAttribute("resultCollections", testResultCollectionDao
				.listCollections());
		super.postHandle(request, response, handler, modelAndView);
	}

}
