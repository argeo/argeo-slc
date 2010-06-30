/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
