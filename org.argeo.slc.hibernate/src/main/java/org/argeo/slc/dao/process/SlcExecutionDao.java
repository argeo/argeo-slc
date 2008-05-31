package org.argeo.slc.dao.process;

import java.util.List;

import org.argeo.slc.core.process.SlcExecution;

public interface SlcExecutionDao {
	public void create(SlcExecution slcExecution);
	public void update(SlcExecution slcExecution);
	public SlcExecution getSlcExecution(String uuid);
	public List<SlcExecution> listSlcExecutions();
}
