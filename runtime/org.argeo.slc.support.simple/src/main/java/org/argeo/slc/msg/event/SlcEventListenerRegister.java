package org.argeo.slc.msg.event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SlcEventListenerRegister implements Serializable {
	static final long serialVersionUID = 1l;

	/** Synchronized */
	private List<SlcEventListenerDescriptor> descriptors = new Vector<SlcEventListenerDescriptor>();

	public synchronized void addEventListenerDescriptor(
			SlcEventListenerDescriptor eventListenerDescriptor) {
		if (descriptors.contains(eventListenerDescriptor))
			descriptors.remove(eventListenerDescriptor);
		descriptors.add(eventListenerDescriptor);
	}

	public synchronized void removeEventListenerDescriptor(
			SlcEventListenerDescriptor eventListenerDescriptor) {
		descriptors.remove(eventListenerDescriptor);
	}

	public synchronized List<SlcEventListenerDescriptor> getDescriptorsCopy() {
		return new ArrayList<SlcEventListenerDescriptor>(descriptors);
	}
}
