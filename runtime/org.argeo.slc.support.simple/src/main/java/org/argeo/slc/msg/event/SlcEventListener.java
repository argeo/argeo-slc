package org.argeo.slc.msg.event;

public interface SlcEventListener {
	/**
	 * Blocks until an event is received or timeout is reached
	 * 
	 * @return the event received or null if timeout was reached before
	 *         receiving one
	 */
	public SlcEvent listen(SlcEventListenerRegister register, Long timeout);
}
