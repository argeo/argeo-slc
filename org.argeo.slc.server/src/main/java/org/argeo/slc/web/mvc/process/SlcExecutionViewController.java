package org.argeo.slc.web.mvc.process;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.argeo.slc.core.SlcException;
import org.argeo.slc.core.process.SlcExecution;
import org.argeo.slc.core.process.SlcExecutionStep;
import org.argeo.slc.dao.process.SlcExecutionDao;

public class SlcExecutionViewController extends ParameterizableViewController {
	private Log log = LogFactory.getLog(getClass());

	private final SlcExecutionDao slcExecutionDao;

	public SlcExecutionViewController(SlcExecutionDao slcExecutionDao) {
		this.slcExecutionDao = slcExecutionDao;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = new ModelAndView();

		//List<SlcExecution> slcExecutions = slcExecutionDao.listSlcExecutions();
		String uuid = request.getParameter("uuid");
		if (uuid == null) 
			throw new SlcException("Parameter uuid required.");
		
		SlcExecution slcExecution = slcExecutionDao.getSlcExecution(uuid);

		if (slcExecution == null) 
			throw new SlcException("Could not find slc execution with uuid " + uuid +".");
		
		if (log.isDebugEnabled()) {
			log.debug("SlcExecution " + slcExecution.getUuid());
		}
		
		List<SlcExecutionStep> slcExecutionSteps = slcExecution.getSteps();
		
		modelAndView.addObject("slcExecutionSteps", slcExecutionSteps);
		modelAndView.addObject("slcExecution", slcExecution);
		modelAndView.setViewName(getViewName());
		return modelAndView;
	}

}
