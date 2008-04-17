package org.argeo.slc.core.process;

import java.util.List;
import java.util.Vector;

public class Message {
	private List<Object> parts = new Vector<Object>();
	
	public void addPart(Object obj){
		parts.add(obj);
	}

	public List<Object> getParts() {
		return parts;
	}

	public void setParts(List<Object> parts) {
		this.parts = parts;
	}
	
	
}
