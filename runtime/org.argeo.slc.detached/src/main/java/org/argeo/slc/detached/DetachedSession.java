package org.argeo.slc.detached;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/** A session within the detached context. */
public class DetachedSession {
	public final static String PROP_DO_IT_AGAIN_POLICY = "slc.detached.doItAgainPolicy";
	public final static String SKIP_UNTIL_ERROR = "skipUntilError";
	public final static String REPLAY = "replay";

	private String uuid = null;
	private List requests = new Vector();
	private List answers = new Vector();
	private String doItAgainPolicy = REPLAY;
	private List refreshedBundleNames = new ArrayList();

	public boolean isClosed() {
		if (answers.size() > 0) {
			DetachedAnswer answer = (DetachedAnswer) answers
					.get(answers.size() - 1);
			return answer.getStatus() == DetachedAnswer.CLOSED_SESSION;
		} else {
			return false;
		}
	}
	
	public boolean lastActionIsError() {
		if (answers.size() > 0) {
			DetachedAnswer answer = (DetachedAnswer) answers
					.get(answers.size() - 1);
			return answer.getStatus() == DetachedAnswer.ERROR;
		} else {
			return false;
		}
	}
	
	public int getExecutedStepCount() {
		if(requests.size() != answers.size()) {
			throw new DetachedException("requests.size() != answers.size() in DetachedSession");
		}
		return answers.size();
	}

	public String getDoItAgainPolicy() {
		return doItAgainPolicy;
	}

	public void setDoItAgainPolicy(String doItAgainPolicy) {
		this.doItAgainPolicy = doItAgainPolicy;
	}

	public List getRequests() {
		return requests;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public List getAnswers() {
		return answers;
	}

	public List getRefreshedBundleNames() {
		return refreshedBundleNames;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer(getClass().getName());
		buf.append("#").append(uuid);
		buf.append(" doItAgainPolicy=").append(doItAgainPolicy);
		return buf.toString();
	}
}
