package org.argeo.slc.web.mvc.process;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.oxm.Unmarshaller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.xml.transform.StringSource;

/** Lists SLC executions possibly filtering them. */
public class GetSlcExecution extends AbstractServiceController {
	private SlcExecutionDao slcExecutionDao;
	private Unmarshaller unmarshaller;

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {
		String uuid = request.getParameter("uuid");
		SlcExecution slcExecution = slcExecutionDao.getSlcExecution(uuid);

		StringSource source = new StringSource(slcExecution
				.getRealizedFlowsXml());
		ObjectList ol2 = (ObjectList) unmarshaller.unmarshal(source);
		ol2.fill(slcExecution.getRealizedFlows());

		modelAndView.addObject(slcExecution);
	}

	public void setSlcExecutionDao(SlcExecutionDao slcExecutionDao) {
		this.slcExecutionDao = slcExecutionDao;
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}

}
