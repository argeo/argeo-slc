/*
 * Copyright (C) 2007-2012 Argeo GmbH
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
package org.argeo.slc.core.attachment;

import java.io.Serializable;
import java.util.UUID;

import org.argeo.slc.attachment.Attachment;

public class SimpleAttachment implements Attachment, Serializable {
	private static final long serialVersionUID = 6615155908800610606L;
	private String uuid = UUID.randomUUID().toString();
	private String name;
	private String contentType = "";

	public SimpleAttachment() {
	}

	public SimpleAttachment(String uuid, String name, String contentType) {
		super();
		this.uuid = uuid;
		this.name = name;
		this.contentType = contentType;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String toString() {
		return "Attachment #" + uuid + "(" + name + ", " + contentType + ")";
	}

	public boolean equals(Object obj) {
		if (obj instanceof Attachment) {
			Attachment attachment = (Attachment) obj;
			if (uuid != null && attachment.getUuid() != null)
				return uuid.equals(attachment.getUuid());

			if (name != null && attachment.getName() != null)
				return name.equals(attachment.getName());

			return hashCode() == attachment.hashCode();
		}
		return false;
	}
}
