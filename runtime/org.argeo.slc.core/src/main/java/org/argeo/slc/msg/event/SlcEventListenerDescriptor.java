package org.argeo.slc.msg.event;

import java.io.Serializable;

public class SlcEventListenerDescriptor implements Serializable {
	static final long serialVersionUID = 1l;

	private final String eventType;
	private final String filter;

	public SlcEventListenerDescriptor(String eventType, String filter) {
		super();
		this.eventType = eventType;
		this.filter = filter;
	}

	public String getEventType() {
		return eventType;
	}

	public String getFilter() {
		return filter;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SlcEventListenerDescriptor) {
			SlcEventListenerDescriptor eventListenerDescriptor = (SlcEventListenerDescriptor) obj;
			return eventListenerDescriptor.getEventType()
					.equals(getEventType());
		}
		return false;
	}

}
