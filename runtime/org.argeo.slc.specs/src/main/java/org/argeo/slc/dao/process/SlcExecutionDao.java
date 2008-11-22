package org.argeo.slc.dao.process;

import java.util.List;

import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.process.SlcExecutionStep;

public interface SlcExecutionDao {
	public void create(SlcExecution slcExecution);

	public void update(SlcExecution slcExecution);

	public void merge(SlcExecution slcExecution);

	public SlcExecution getSlcExecution(String uuid);

	public List<SlcExecution> listSlcExecutions();

	public void addSteps(String slcExecutionId,
			List<SlcExecutionStep> additionalSteps);
}
