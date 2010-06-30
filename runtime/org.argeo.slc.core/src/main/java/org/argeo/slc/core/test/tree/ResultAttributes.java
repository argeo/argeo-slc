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

package org.argeo.slc.core.test.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.argeo.slc.core.attachment.SimpleAttachment;

public class ResultAttributes implements Serializable {
	private static final long serialVersionUID = 1L;

	private String uuid = null;
	private Date closeDate = null;
	private Map<String, String> attributes = new Hashtable<String, String>();
	private List<SimpleAttachment> attachments = new ArrayList<SimpleAttachment>();

	public ResultAttributes() {
		super();
	}

	public ResultAttributes(TreeTestResult ttr) {
		super();
		this.uuid = ttr.getUuid();
		this.attributes = ttr.getAttributes();
		this.closeDate = ttr.getCloseDate();
		this.attachments = ttr.getAttachments();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public Date getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}

	public List<SimpleAttachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<SimpleAttachment> attachments) {
		this.attachments = attachments;
	}

}
