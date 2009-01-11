package org.argeo.slc.web.mvc.runtime;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.argeo.slc.dao.runtime.SlcAgentDescriptorDao;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.argeo.slc.web.mvc.AbstractServiceController;
import org.springframework.web.servlet.ModelAndView;

/** Lists results possibly filtering them. */
public class ListAgentsController extends AbstractServiceController {
	private final SlcAgentDescriptorDao slcAgentDescriptorDao;

	public ListAgentsController(SlcAgentDescriptorDao slcAgentDescriptorDao) {
		this.slcAgentDescriptorDao = slcAgentDescriptorDao;
	}

	@Override
	protected void handleServiceRequest(HttpServletRequest request,
			HttpServletResponse response, ModelAndView modelAndView)
			throws Exception {
		List<SlcAgentDescriptor> list = slcAgentDescriptorDao
				.listSlcAgentDescriptors();
		modelAndView.addObject("list", new ObjectList(list));
	}
}
