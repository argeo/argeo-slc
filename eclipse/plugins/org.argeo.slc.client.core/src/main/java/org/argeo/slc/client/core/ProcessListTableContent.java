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
	public SlcExecution getLine(int index) {
		return slcExecutions.get(index);
	}

	@Override
	public synchronized String getLabel(Object o, int i) {
		SlcExecution se = (SlcExecution) o;
		switch (i) {

		case 0:
			return se.getStartDate().toString();
		case 1:
			return se.getHost();
		case 2:
			return se.getUuid();
		case 3:
			return se.getStatus();
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
			log.debug("======================== set CONTENT =====================");
			log.debug("Lst Size : " + lst.size());
			log.debug("Date : " + lst.get(0).getStartDate());
			log.debug("Host : " + lst.get(0).getHost());
			log.debug("Status : " + lst.get(0).getStatus());
			log.debug("UUID : " + lst.get(0).getUuid());
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
