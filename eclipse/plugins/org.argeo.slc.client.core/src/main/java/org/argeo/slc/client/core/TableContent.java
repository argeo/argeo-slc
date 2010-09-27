package org.argeo.slc.client.core;

import java.util.List;

import org.argeo.slc.process.SlcExecution;

public interface TableContent {

	public Object getLine(int index);

	public String getLabel(Object o, int index);

	public List<SlcExecution> getContent();
}
