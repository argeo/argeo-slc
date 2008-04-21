package org.argeo.slc.dao.process;

import org.argeo.slc.core.process.SlcExecution;

public interface SlcExecutionDao {
	public void create(SlcExecution slcExecution);
	public SlcExecution getSlcExecution(String uuid);
}
