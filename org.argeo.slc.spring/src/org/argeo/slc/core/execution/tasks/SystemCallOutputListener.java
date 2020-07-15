package org.argeo.slc.core.execution.tasks;

public interface SystemCallOutputListener {
	public void newLine(SystemCall systemCall, String line, Boolean isError);
}
