/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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
package org.argeo.slc.msg.test.tree;

import java.io.Serializable;
import java.util.Date;

import org.argeo.slc.core.test.tree.TreeTestResult;

public class CloseTreeTestResultRequest implements Serializable {
	private static final long serialVersionUID = 7384136025920047977L;
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
