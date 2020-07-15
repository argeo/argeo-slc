package org.argeo.slc.diff;

/**
 * Object able to uniquely identify an atomic diff part. Used to identify
 * missings and left-overs.
 */
public interface DiffKey {
	public String toString();
}
