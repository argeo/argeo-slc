package org.argeo.slc.msg.test.tree;

import java.util.Date;

public class CloseTreeTestResultRequest {
	private String resultUuid;
	private Date closeDate;

	public CloseTreeTestResultRequest() {

	}

	public CloseTreeTestResultRequest(String resultUuid, Date closeDate) {
		super();
		this.resultUuid = resultUuid;
		this.closeDate = closeDate;
	}

	public String getResultUuid() {
		return resultUuid;
	}

	public void setResultUuid(String id) {
		this.resultUuid = id;
	}

	public Date getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}

}
