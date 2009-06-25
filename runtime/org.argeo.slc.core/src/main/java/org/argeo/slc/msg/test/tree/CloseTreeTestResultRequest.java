package org.argeo.slc.msg.test.tree;

import java.util.Date;

import org.argeo.slc.core.test.tree.TreeTestResult;

public class CloseTreeTestResultRequest {
	private String resultUuid;
	private Date closeDate;

	public CloseTreeTestResultRequest() {

	}

	public CloseTreeTestResultRequest(String resultUuid, Date closeDate) {
		this.resultUuid = resultUuid;
		this.closeDate = closeDate;
	}

	public CloseTreeTestResultRequest(TreeTestResult ttr) {
		this.resultUuid = ttr.getUuid();
		this.closeDate = ttr.getCloseDate();
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

	@Override
	public String toString() {
		return getClass().getSimpleName() + "#" + resultUuid;
	}

}
