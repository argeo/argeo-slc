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
