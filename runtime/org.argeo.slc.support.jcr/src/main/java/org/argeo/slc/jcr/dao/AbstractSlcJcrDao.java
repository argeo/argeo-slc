package org.argeo.slc.jcr.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.jcr.Session;

import org.argeo.jcr.JcrUtils;
import org.argeo.jcr.NodeMapperProvider;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.argeo.slc.test.TestResult;


public abstract class AbstractSlcJcrDao {

	private Session session;

	//We inject the nodeMapperProvider that define a default node mapper as an
	// entry point of the NodeMapper
	private NodeMapperProvider nodeMapperProvider;

	
	
	public void setSession(Session session) {
		this.session = session;
	}

	protected Session getSession() {
		return session;
	}
	
	// IoC
	public void setNodeMapperProvider(NodeMapperProvider nodeMapperProvider) {
		this.nodeMapperProvider = nodeMapperProvider;
	}
	
	// TODO : define a strategy to define basePathes
	protected String basePath(TestResult testResult) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		// cal.setTime(slcExecution.getStartDate());
		return "/slc/testresult/" + JcrUtils.dateAsPath(cal) + "testresult";
	}
	
	protected String basePath(SlcAgentDescriptor slcAgentDescriptor) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		// cal.setTime(slcExecution.getStartDate());
		return "/slc/agents/"
				+ JcrUtils.hostAsPath(slcAgentDescriptor.getHost() + "/agent");
	}

	protected NodeMapperProvider getNodeMapperProvider(){
		return this.nodeMapperProvider;
	}

	
}
