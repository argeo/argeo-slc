package org.argeo.slc.msg.event;


public interface SlcEventListenerRegister {
	public void addEventListenerDescriptor(
			SlcEventListenerDescriptor eventListenerDescriptor);

	public void removeEventListenerDescriptor(
			SlcEventListenerDescriptor eventListenerDescriptor);

	public SlcEvent listen(SlcEventListener eventListener, Long timeout);
}
