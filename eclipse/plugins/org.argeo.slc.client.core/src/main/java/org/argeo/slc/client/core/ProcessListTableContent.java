package org.argeo.slc.client.core;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.process.SlcExecution;

public class ProcessListTableContent implements TableContent {
	private static final Log log = LogFactory
			.getLog(ProcessListTableContent.class);

	private List<SlcExecution> slcExecutions;

	// IoC
	private SlcExecutionDao slcExecutionDao;

	@Override
	public SlcExecution getLine(int i) {

		// SlcExecution se = new SlcExecution();
		// se.setHost("Marshall");
		// se.setUuid("a very long uuid");
		// se.setStatus(SlcExecution.STATUS_RUNNING);
		// return se;
		return null;
	}

	@Override
	public String getLabel(Object o, int i) {
		// TODO Auto-generated method stub
		return "test";
	}

	public List<SlcExecution> getContent() {
		return this.slcExecutions;
	}

	public void setContent() {
		List<SlcExecution> lst = slcExecutionDao.listSlcExecutions();
		if (lst.get(0) != null) {
			log.debug(lst.get(0).getStartDate());
			log.debug(lst.get(0).getHost());
			log.debug(lst.get(0).getStatus());
			log.debug(lst.get(0).getUuid());
		}
		Iterator<SlcExecution> it = slcExecutions.iterator();
		while (it.hasNext()) {
			slcExecutions.add(it.next());
		}
	}

	// IoC
	public void setSlcExecutionDao(SlcExecutionDao slcExecutionDao) {
		this.slcExecutionDao = slcExecutionDao;
	}
}
