package org.argeo.slc.web.mvc;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.msg.ExecutionAnswer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public abstract class AbstractServiceController extends AbstractController {
	public final static String KEY_ANSWER = "__answer";

	private String viewName = null;

	@Override
	protected final ModelAndView handleRequestInternal(
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(getViewName());
		try {
			handleServiceRequest(request, response, mv);
		} catch (Exception e) {
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			ExecutionAnswer answer = ExecutionAnswer.error(writer.toString());
			ModelAndView errorMv = new ModelAndView();
			errorMv.addObject(KEY_ANSWER, answer);
			errorMv.setViewName(KEY_ANSWER);
			return errorMv;
		}

		if (mv.getModel().size() == 0) {
			mv.addObject(KEY_ANSWER, ExecutionAnswer
					.ok("Execution completed properly"));
		}

		if (mv.getViewName() == null && mv.getModel().size() == 1)
			mv.setViewName(mv.getModel().keySet().iterator().next().toString());

		return mv;
	}

	protected abstract void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception;

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String getViewName() {
		return viewName;
	}

}
