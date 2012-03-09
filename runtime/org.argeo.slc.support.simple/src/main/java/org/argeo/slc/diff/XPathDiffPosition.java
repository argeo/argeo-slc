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
package org.argeo.slc.diff;

import org.argeo.slc.UnsupportedException;

/** A diff position within an Xml file. <b>NOT YET IMPLEMENTED</b>. */
public class XPathDiffPosition extends DiffPosition {

	private String xPath;

	public XPathDiffPosition(RelatedFile relatedFile, String path) {
		super(relatedFile);
		xPath = path;
	}

	public int compareTo(DiffPosition dp) {
		if (!(dp instanceof XPathDiffPosition))
			throw new UnsupportedException("position", dp);

		XPathDiffPosition o = (XPathDiffPosition) dp;
		if (relatedFile.equals(o.relatedFile)) {
			return xPath.compareTo(o.xPath);
		} else {
			return relatedFile.compareTo(o.relatedFile);
		}
	}

	public String getXPath() {
		return xPath;
	}

	@Override
	public String toString() {
		return xPath;
	}
}
