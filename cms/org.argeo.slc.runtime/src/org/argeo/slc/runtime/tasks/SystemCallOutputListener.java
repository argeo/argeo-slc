package org.argeo.slc.runtime.tasks;

public interface SystemCallOutputListener {
	public void newLine(SystemCall systemCall, String line, Boolean isError);
}
