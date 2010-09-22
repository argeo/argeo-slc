package org.argeo.slc.client.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.process.SlcExecution;

public class ProcessListTableContent implements TableContent {
	private static final Log log = LogFactory
			.getLog(ProcessListTableContent.class);

	private List<SlcExecution> slcExecutions = new ArrayList<SlcExecution>();

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
	public synchronized String getLabel(Object o, int i) {
		SlcExecution se = (SlcExecution) o;
		switch (i) {

		case 0:
			return "DATE";
		case 1:
			return se.getHost();
		case 2:
			return se.getUuid();
		case 3:
			return "TYPE";
		}
		return "test";
	}

	public synchronized List<SlcExecution> getContent() {
		return this.slcExecutions;
	}

	public synchronized void setContent() {
		// Thread.currentThread().setContextClassLoader(null);
		List<SlcExecution> lst = slcExecutionDao.listSlcExecutions();
		if (lst.get(0) != null) {
			// log.debug(lst.get(0).getStartDate());
			log.debug(lst.get(0).getHost());
			log.debug(lst.get(0).getStatus());
			log.debug(lst.get(0).getUuid());
		}

		slcExecutions.clear();
		Iterator<SlcExecution> it = lst.iterator();
		while (it.hasNext()) {
			slcExecutions.add(it.next());
		}
	}

	// IoC
	public void setSlcExecutionDao(SlcExecutionDao slcExecutionDao) {
		this.slcExecutionDao = slcExecutionDao;
	}
}
