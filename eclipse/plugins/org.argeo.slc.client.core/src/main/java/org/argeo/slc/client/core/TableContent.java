package org.argeo.slc.client.core;

import java.util.List;

import org.argeo.slc.process.SlcExecution;

public interface TableContent {

	public Object getLine(int i);

	public String getLabel(Object o, int i);

	public void setContent();

	public List<SlcExecution> getContent();
}
