package org.argeo.slc.msg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ObjectList {
	private List<Serializable> objects = new ArrayList<Serializable>();

	public ObjectList() {
	}

	public ObjectList(Collection<? extends Serializable> objects) {
		this.objects.addAll(objects);
	}

	@SuppressWarnings(value = { "unchecked" })
	public <T extends Serializable> void fill(List<T> objects) {
		for (Serializable o : this.objects){
			objects.add((T) o);
		}
	}

	public List<Serializable> getObjects() {
		return objects;
	}

	public void setObjects(List<Serializable> objects) {
		this.objects = objects;
	}

}
