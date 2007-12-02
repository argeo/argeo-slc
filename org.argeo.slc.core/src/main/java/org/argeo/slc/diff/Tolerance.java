package org.argeo.slc.diff;

/** Compares objects, eventually using tolerance mechanisms. */
public interface Tolerance {
	/**
	 * Compares objects
	 * 
	 * @param key
	 *            any object used to differentiate the type of data (e.g.
	 *            column, path)
	 * @param expected
	 *            the expected value
	 * @param reached
	 *            the reached value
	 * @return the converted object
	 */
	public Boolean compare(Object key, Object expected, Object reached);
}
