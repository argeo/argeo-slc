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
package org.argeo.slc.core.test.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.attachment.Attachment;
import org.argeo.slc.core.attachment.AttachmentsEnabled;
import org.argeo.slc.core.attachment.SimpleAttachment;
import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.structure.StructureAware;
import org.argeo.slc.structure.StructureElement;
import org.argeo.slc.structure.StructureRegistry;
import org.argeo.slc.test.TestResult;
import org.argeo.slc.test.TestResultListener;
import org.argeo.slc.test.TestResultPart;
import org.argeo.slc.test.TestRun;
import org.argeo.slc.test.TestRunAware;

/**
 * Complex implementation of a test result compatible with a tree based
 * structure.
 */
public class TreeTestResult implements TestResult, StructureAware<TreeSPath>,
		Comparable<TreeTestResult>, AttachmentsEnabled, Serializable {

	private static final long serialVersionUID = 1L;
	private final static Log log = LogFactory.getLog(TreeTestResult.class);

	// Persistence data
	private String uuid = UUID.randomUUID().toString();
	private Date closeDate;

	private SortedMap<TreeSPath, PartSubList> resultParts = new TreeMap<TreeSPath, PartSubList>();
	private SortedMap<TreeSPath, StructureElement> elements = new TreeMap<TreeSPath, StructureElement>();
	private List<SimpleAttachment> attachments = new ArrayList<SimpleAttachment>();

	// Headers. Used to accelerate request on a specific test result.
	private SortedMap<String, String> attributes = new TreeMap<String, String>();

	// Runtime Data
	private TreeSPath currentPath;
	private transient TestRun currentTestRun;
	private Boolean warnIfAlreadyClosed = true;
	private Boolean strictChecks = false;
	// TODO is it really necessary closeDate == null ?
	private Boolean isClosed = false;

	private Boolean cache = true;

	private transient List<TestResultListener<TreeTestResult>> listeners = new Vector<TestResultListener<TreeTestResult>>();

	/** Sets the list of listeners. */
	public void setListeners(List<TestResultListener<TreeTestResult>> listeners) {
		this.listeners = listeners;
	}

	public void addResultPart(TestResultPart part) {
		if (isClosed)
			notifyIssue(
					"Trying to add result parts to an already closed result,"
							+ " consider changing the scope of this test result:"
							+ " you are referencing the same stored data with each new call.",
					null);

		if (currentPath == null)
			throw new SlcException("No current path set.");

		if (cache) {
			PartSubList subList = resultParts.get(currentPath);
			if (subList == null) {
				subList = new PartSubList();
				resultParts.put(currentPath, subList);
			}
			subList.getParts().add(part);
		}

		if (part instanceof TestRunAware && currentTestRun != null) {
			((TestRunAware) part).notifyTestRun(currentTestRun);
		}

		// notify listeners
		synchronized (listeners) {
			for (TestResultListener<TreeTestResult> listener : listeners) {
				listener.resultPartAdded(this, part);
			}
		}
	}

	protected void notifyIssue(String msg, Exception e) {
		if (strictChecks)
			throw new SlcException(msg, e);
		else
			log.error(msg, e);
	}

	public void notifyCurrentPath(StructureRegistry<TreeSPath> registry,
			TreeSPath path) {
		if (!cache)
			return;

		if (registry != null) {
			for (TreeSPath p : path.getHierarchyAsList()) {
				if (!elements.containsKey(p)) {
					StructureElement elem = registry.getElement(p);
					if (elem != null) {
						elements.put(p, elem);
					}
				} else {
					if (log.isTraceEnabled())
						log.trace("An element is already registered for path "
								+ p + " and was not updated");
				}

			}
		}

		currentPath = path;
	}

	/** Gets the current path. */
	public TreeSPath getCurrentPath() {
		return currentPath;
	}

	/** Gets all the results structured as a map of <code>PartSubList<code>s. */
	public SortedMap<TreeSPath, PartSubList> getResultParts() {
		return resultParts;
	}

	/**
	 * Used by ORM systems. Changed to public in order to enable jcr persistence
	 */
	public void setResultParts(SortedMap<TreeSPath, PartSubList> resultParts) {
		this.resultParts = resultParts;
	}

	public void close() {
		if (resultParts.size() == 0) {
			if (log.isTraceEnabled())
				log.trace("Test Result #" + getUuid()
						+ " contains no results, no need to close it.");
			return;
		}

		if (isClosed) {
			if (warnIfAlreadyClosed)
				log.warn("Test Result #" + getUuid()
						+ " already closed. Doing nothing.");
			return;
		}

		closeDate = new Date();

		synchronized (listeners) {
			for (TestResultListener<TreeTestResult> listener : listeners) {
				listener.close(this);
			}
		}
		isClosed = true;

		if (log.isTraceEnabled())
			log.trace("Test Result " + getUuid() + " closed.");
	}

	public Date getCloseDate() {
		return closeDate;
	}

	/** Sets the close date (for ORM) */
	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}

	public void notifyTestRun(TestRun testRun) {
		currentTestRun = testRun;
	}

	public SortedMap<TreeSPath, StructureElement> getElements() {
		return elements;
	}

	public void setElements(SortedMap<TreeSPath, StructureElement> pathNames) {
		this.elements = pathNames;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public SortedMap<TreeSPath, StructureElement> getRelatedElements(
			TreeSPath path) {
		if (path == null)
			throw new SlcException(
					"Cannot retrieve element for a null path in result #"
							+ uuid);

		SortedMap<TreeSPath, StructureElement> relatedElements = new TreeMap<TreeSPath, StructureElement>();
		List<TreeSPath> hierarchy = path.getHierarchyAsList();
		for (TreeSPath currPath : elements.keySet()) {
			if (hierarchy.contains(currPath)) {
				relatedElements.put(currPath, elements.get(currPath));
			}
		}
		return relatedElements;
	}

	public TestRun getCurrentTestRun() {
		return currentTestRun;
	}

	public int compareTo(TreeTestResult ttr2) {
		TreeTestResult ttr1 = this;
		if (ttr1.getCloseDate() != null && ttr2.getCloseDate() != null) {
			if (ttr1.getCloseDate().equals(ttr2.getCloseDate()))
				return compareUuid(ttr1, ttr2);
			else
				return -ttr1.getCloseDate().compareTo(ttr2.getCloseDate());
		} else if (ttr1.getCloseDate() != null && ttr2.getCloseDate() == null) {
			return 1;
		} else if (ttr1.getCloseDate() == null && ttr2.getCloseDate() != null) {
			return -1;
		} else {
			return compareUuid(ttr1, ttr2);
		}
	}

	protected int compareUuid(TestResult ttr1, TestResult ttr2) {
		if (ttr1.getUuid() == null || ttr2.getUuid() == null)
			throw new SlcException(
					"Cannot compare tree test result with null uuid");
		else {
			if (ttr1.getUuid().equals(ttr2.getUuid()))
				return 0;
			return ttr1.getUuid().compareTo(ttr2.getUuid());
		}
	}

	public boolean equals(Object obj) {
		if (obj instanceof TestResult)
			return compareUuid(this, ((TestResult) obj)) == 0;
		else
			return false;
	}

	public int hashCode() {
		if (uuid != null)
			return uuid.hashCode();
		else
			return super.hashCode();
	}

	public SortedMap<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(SortedMap<String, String> attributes) {
		this.attributes = attributes;
	}

	public void setWarnIfAlreadyClosed(Boolean warnIfAlreadyClosed) {
		this.warnIfAlreadyClosed = warnIfAlreadyClosed;
	}

	public List<SimpleAttachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<SimpleAttachment> attachments) {
		this.attachments = attachments;
	}

	public void addAttachment(Attachment attachment) {
		attachments.add((SimpleAttachment) attachment);
		synchronized (listeners) {
			for (TestResultListener<TreeTestResult> listener : listeners) {
				if (listener instanceof TreeTestResultListener)
					((TreeTestResultListener) listener).addAttachment(this,
							attachment);
			}
		}
	}

	public void setStrictChecks(Boolean strictChecks) {
		this.strictChecks = strictChecks;
	}

	/**
	 * Whether information should be stored in thsi object or simply forwarded
	 * to teh listeners.
	 */
	public void setCache(Boolean cache) {
		this.cache = cache;
	}

}
