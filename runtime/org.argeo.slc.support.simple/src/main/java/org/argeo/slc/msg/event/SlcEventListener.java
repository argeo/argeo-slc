package org.argeo.slc.msg.event;

import java.util.List;

public interface SlcEventListener {
	/**
	 * Blocks until an event is received or timeout is reached
	 * 
	 * @return the event received or null if timeout was reached before
	 *         receiving one
	 */
	public SlcEvent listen(String clientId,
			List<SlcEventListenerDescriptor> descriptors, Long timeout);

	public void close(String clientId);
}
