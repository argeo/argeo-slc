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
package org.argeo.slc.core.structure;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import org.argeo.slc.core.structure.tree.TreeSPath;
import org.argeo.slc.structure.StructureElement;

/**
 * Basic implementation of <code>StructureElement</code>.
 * 
 * @see TreeSPath
 */
public class SimpleSElement implements StructureElement, Serializable {
	private static final long serialVersionUID = -7012193125005818900L;
	/** For ORM */
	private Long tid;
	private String label;
	private Map<String, String> tags = new TreeMap<String, String>();

	/** For ORM */
	public SimpleSElement() {
	}

	/** Constructor */
	public SimpleSElement(String label) {
		this.label = label;
	}

	/** Constructor */
	public SimpleSElement(String label, String defaultLabel) {
		this(label != null ? label : defaultLabel);
	}

	/** Constructor */
	public SimpleSElement(SimpleSElement sElement) {
		setLabel(sElement.getLabel());
		setTags(new TreeMap<String, String>(sElement.getTags()));
	}

	public String getLabel() {
		return label;
	}

	/** Sets the label. */
	public void setLabel(String label) {
		this.label = label;
	}

	public Long getTid() {
		return tid;
	}

	void setTid(Long tid) {
		this.tid = tid;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	@Override
	public SimpleSElement clone() {
		return new SimpleSElement(this);
	}

}
