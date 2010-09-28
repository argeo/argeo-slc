package org.argeo.slc.client.core;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.process.SlcExecution;

public class ProcessListTableContent implements TableContent {
	private static final Log log = LogFactory
			.getLog(ProcessListTableContent.class);

	// IoC
	private SlcExecutionDao slcExecutionDao;

	@Override
	public SlcExecution getLine(int index) {
		return slcExecutionDao.listSlcExecutions().get(index);
		// return slcExecutions.get(index);
	}

	@Override
	// public synchronized String getLabel(Object o, int i) {
	public String getLabel(Object o, int i) {
		SlcExecution se = (SlcExecution) o;

		switch (i) {

		case 0:
			return se.getStartDate().toString();
			// Workaround to insure that we have no Lazy Init PB
			// return
			// slcExecutionDao.getSlcExecution(se.getUuid()).getStartDate()
			// .toString();
		case 1:
			return se.getHost();
		case 2:
			return se.getUuid();
		case 3:
			return se.getStatus();
		}
		return null;
	}

	public synchronized List<SlcExecution> getContent() {
		return slcExecutionDao.listSlcExecutions();
		// return this.slcExecutions;
	}

	// IoC
	public void setSlcExecutionDao(SlcExecutionDao slcExecutionDao) {
		this.slcExecutionDao = slcExecutionDao;
	}
}
