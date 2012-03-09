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
package org.argeo.slc.core.build;

import java.util.StringTokenizer;

/**
 * <p>
 * An implementation of the distribution id using the standard
 * Major.Minor.Release notation. And additional arbitrary string can also be
 * added.
 * </p>
 * 
 * <p>
 * <b>Examples:</b><br>
 * 0.2.6<br>
 * 2.4.12.RC1
 * </p>
 */
public class VersionDistributionId {

	private Integer major;
	private Integer minor;
	private Integer release;
	private String additional;

	/** Parse the provided string in order to set the various components. */
	public void setVersionString(String str) {
		StringTokenizer st = new StringTokenizer(str, ".");
		if (st.hasMoreTokens())
			major = Integer.parseInt(st.nextToken());
		if (st.hasMoreTokens())
			minor = Integer.parseInt(st.nextToken());
		if (st.hasMoreTokens())
			release = Integer.parseInt(st.nextToken());
		if (st.hasMoreTokens())
			additional = st.nextToken();
	}

	public Integer getMajor() {
		return major;
	}

	public void setMajor(Integer major) {
		this.major = major;
	}

	public Integer getMinor() {
		return minor;
	}

	public void setMinor(Integer minor) {
		this.minor = minor;
	}

	public Integer getRelease() {
		return release;
	}

	public void setRelease(Integer release) {
		this.release = release;
	}

	public String getAdditional() {
		return additional;
	}

	public void setAdditional(String additional) {
		this.additional = additional;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return major + "." + minor + "." + release
				+ (additional != null ? "." + additional : "");
	}

}
