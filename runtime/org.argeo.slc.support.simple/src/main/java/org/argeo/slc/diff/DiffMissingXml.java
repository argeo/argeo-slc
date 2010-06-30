/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

/**
 * <code>DiffMissing</code> using the XPath of the position as
 * <code>DiffKey</code>
 */
public class DiffMissingXml extends DiffMissing {

	public DiffMissingXml(XPathDiffPosition position) {
		super(position, new DiffKeyXml(position.getXPath()));
	}

	/** Implementation of <code>DiffKey</code> based on an XPath string. */
	protected static class DiffKeyXml implements DiffKey {
		private final String xPath;

		public DiffKeyXml(String xPath) {
			this.xPath = xPath;
		}

		public String getXPath() {
			return xPath;
		}

		@Override
		public String toString() {
			return xPath;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof DiffKeyXml))
				return false;
			return xPath.equals(((DiffKeyXml) obj).xPath);
		}

		@Override
		public int hashCode() {
			return xPath.hashCode();
		}

	}
}
