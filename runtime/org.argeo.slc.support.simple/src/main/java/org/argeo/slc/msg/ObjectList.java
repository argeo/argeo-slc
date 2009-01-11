package org.argeo.slc.msg;

import java.util.ArrayList;
import java.util.List;

public class ObjectList {
	private List<?> objects = new ArrayList<Object>();

	public ObjectList() {
	}

	public ObjectList(List<?> objects) {
		this.objects = objects;
	}

	public List<?> getObjects() {
		return objects;
	}

	public void setObjects(List<?> objects) {
		this.objects = objects;
	}

}
