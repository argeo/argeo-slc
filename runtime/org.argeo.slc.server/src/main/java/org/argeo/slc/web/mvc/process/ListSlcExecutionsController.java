package org.argeo.slc.web.mvc.process;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

/** Lists SLC executions possibly filtering them. */
public class ListSlcExecutionsController extends AbstractServiceController {
	private final SlcExecutionDao slcExecutionDao;

	public ListSlcExecutionsController(SlcExecutionDao slcExecutionDao) {
		this.slcExecutionDao = slcExecutionDao;
	}

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {
		List<SlcExecution> list = slcExecutionDao.listSlcExecutions();
		modelAndView.addObject("list", new ObjectList(list));
	}
}
