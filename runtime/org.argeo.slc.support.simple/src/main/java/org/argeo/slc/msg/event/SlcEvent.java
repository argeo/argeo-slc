package org.argeo.slc.msg.event;

import java.util.HashMap;
import java.util.Map;

public class SlcEvent {
	public final static String EVENT_TYPE = "slc_eventType";
	public final static String EVENT_FILTER = "slc_eventFilter";

	private Map<String, String> headers = new HashMap<String, String>();

	public SlcEvent() {
	}

	public SlcEvent(String eventType) {
		headers.put(EVENT_TYPE, eventType);
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

}
