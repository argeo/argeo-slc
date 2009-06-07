package org.argeo.slc.msg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ObjectList {
	private List<Serializable> objects = new ArrayList<Serializable>();

	public ObjectList() {
	}

	public ObjectList(List<? extends Serializable> objects) {
		this.objects.addAll(objects);
	}

	public List<Serializable> getObjects() {
		return objects;
	}

	public void setObjects(List<Serializable> objects) {
		this.objects = objects;
	}

}
