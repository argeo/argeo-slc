package org.argeo.slc.msg.event;

import java.util.List;

public interface SlcEventListenerRegister {
	public void addEventListenerDescriptor(
			SlcEventListenerDescriptor eventListenerDescriptor);

	public void removeEventListenerDescriptor(
			SlcEventListenerDescriptor eventListenerDescriptor);

	public List<SlcEventListenerDescriptor> getDescriptorsCopy();

	public String getId();

	// public SlcEvent listen(SlcEventListener eventListener, Long timeout);
}
