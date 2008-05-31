package org.argeo.slc.msg.process;

public class SlcExecutionStatusRequest {
	private String slcExecutionUuid;
	private String newStatus;

	public SlcExecutionStatusRequest() {
	}

	public SlcExecutionStatusRequest(String slcExecutionUuid, String newStatus) {
		this.slcExecutionUuid = slcExecutionUuid;
		this.newStatus = newStatus;
	}

	public String getSlcExecutionUuid() {
		return slcExecutionUuid;
	}

	public void setSlcExecutionUuid(String slcExecutionUuid) {
		this.slcExecutionUuid = slcExecutionUuid;
	}

	public String getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(String newStatus) {
		this.newStatus = newStatus;
	}
}
