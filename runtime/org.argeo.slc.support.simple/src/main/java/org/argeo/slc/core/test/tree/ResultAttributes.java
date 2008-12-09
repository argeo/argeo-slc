package org.argeo.slc.core.test.tree;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

public class ResultAttributes {
	private String uuid = null;
	private Date closeDate = null;
	private Map<String, String> attributes = new Hashtable<String, String>();

	public ResultAttributes() {
		super();
	}

	public ResultAttributes(TreeTestResult ttr) {
		super();
		this.uuid = ttr.getUuid();
		this.attributes = ttr.getAttributes();
		this.closeDate = ttr.getCloseDate();
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

}
