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
package org.argeo.slc.jcr;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.attachment.Attachment;
import org.argeo.slc.core.attachment.AttachmentsEnabled;
import org.argeo.slc.test.TestResult;
import org.argeo.slc.test.TestResultPart;
import org.argeo.slc.test.TestRun;
import org.argeo.slc.test.TestStatus;

/** {@link TestResult} wrapping a JCR node of type {@link SlcTypes#SLC_RESULT}. */
public class JcrTestResult implements TestResult, SlcNames, AttachmentsEnabled {
	/** Should only be set for an already existing result. */
	private String uuid;
	private Session session;
	private String resultType = SlcTypes.SLC_RESULT;

	/** cached for performance purposes */
	private String nodeIdentifier = null;

	private Boolean logoutWhenDestroyed = true;

	private Map<String, String> attributes = new HashMap<String, String>();

	public void init() {
		try {
			if (uuid == null) {
				// create new result
				uuid = UUID.randomUUID().toString();
				String path = SlcJcrUtils.createResultPath(uuid);
				Node resultNode = JcrUtils.mkdirs(session, path, resultType);
				resultNode.setProperty(SLC_UUID, uuid);
				for (String attr : attributes.keySet()) {
					String property = attr;
					// compatibility with legacy applications
					if ("testCase".equals(attr))
						property = SLC_TEST_CASE;
					else if ("testCaseType".equals(attr))
						property = SLC_TEST_CASE_TYPE;
					resultNode.setProperty(property, attributes.get(attr));
				}
				session.save();
			}
		} catch (Exception e) {
			JcrUtils.discardQuietly(session);
			throw new SlcException("Cannot initialize JCR result", e);
		}
	}

	public void destroy() {
		if (logoutWhenDestroyed)
			JcrUtils.logoutQuietly(session);
	}

	public Node getNode() {
		try {
			Node resultNode;
			if (nodeIdentifier != null) {
				return session.getNodeByIdentifier(nodeIdentifier);
			} else {
				QueryManager qm = session.getWorkspace().getQueryManager();
				Query q = qm.createQuery(
						"select * from [slc:result] where [slc:uuid]='" + uuid
								+ "'", Query.JCR_SQL2);
				resultNode = JcrUtils.querySingleNode(q);
				if (resultNode != null)
					nodeIdentifier = resultNode.getIdentifier();
			}
			return resultNode;
		} catch (Exception e) {
			throw new SlcException("Cannot get result node", e);
		}
	}

	public void notifyTestRun(TestRun testRun) {
	}

	public void addResultPart(TestResultPart testResultPart) {
		Node node = getNode();
		try {
			// TODO: find a better way to name it by default
			String partName = Long.toString(System.currentTimeMillis());
			Node resultPartNode = node.addNode(partName, SlcTypes.SLC_CHECK);
			resultPartNode.setProperty(SLC_SUCCESS,
					testResultPart.getStatus() == TestStatus.PASSED);
			if (testResultPart.getMessage() != null)
				resultPartNode.setProperty(SLC_MESSAGE,
						testResultPart.getMessage());
			if (testResultPart.getExceptionMessage() != null)
				resultPartNode.setProperty(SLC_ERROR_MESSAGE,
						testResultPart.getExceptionMessage());
			JcrUtils.updateLastModified(node);
			node.getSession().save();
		} catch (Exception e) {
			JcrUtils.discardUnderlyingSessionQuietly(node);
			throw new SlcException("Cannot get UUID from " + node, e);
		}

	}

	public String getUuid() {
		Node node = getNode();
		try {
			return node.getProperty(SLC_UUID).getString();
		} catch (Exception e) {
			throw new SlcException("Cannot get UUID from " + node, e);
		}
	}

	public void close() {
		Node node = getNode();
		try {
			if (node.hasNode(SLC_COMPLETED))
				return;
			node.setProperty(SLC_COMPLETED, new GregorianCalendar());
			JcrUtils.updateLastModified(node);
			node.getSession().save();
		} catch (Exception e) {
			JcrUtils.discardUnderlyingSessionQuietly(node);
			throw new SlcException("Cannot get close date from " + node, e);
		}
		if (logoutWhenDestroyed)
			JcrUtils.logoutQuietly(session);
	}

	public Date getCloseDate() {
		Node node = getNode();
		try {
			if (!node.hasNode(SLC_COMPLETED))
				return null;
			return node.getProperty(SLC_COMPLETED).getDate().getTime();
		} catch (Exception e) {
			throw new SlcException("Cannot get close date from " + node, e);
		}
	}

	public Map<String, String> getAttributes() {
		Node node = getNode();
		try {
			Map<String, String> map = new HashMap<String, String>();
			PropertyIterator pit = node.getProperties();
			while (pit.hasNext()) {
				Property p = pit.nextProperty();
				if (!p.isMultiple())
					map.put(p.getName(), p.getValue().getString());
			}
			return map;
		} catch (Exception e) {
			throw new SlcException("Cannot get close date from " + node, e);
		}
	}

	public void addAttachment(Attachment attachment) {
		// TODO implement it
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	public void setAttributes(Map<String, String> attributes) {
		if (uuid != null)
			throw new SlcException(
					"Attributes cannot be set on an already initialized test result."
							+ " Update the related JCR node directly instead.");
		this.attributes = attributes;
	}

	public void setLogoutWhenDestroyed(Boolean logoutWhenDestroyed) {
		this.logoutWhenDestroyed = logoutWhenDestroyed;
	}

}
