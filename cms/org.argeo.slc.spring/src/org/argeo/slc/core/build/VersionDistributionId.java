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
