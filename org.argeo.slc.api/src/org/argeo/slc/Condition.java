package org.argeo.slc;

/** Binary check on an arbitrary object. */
public interface Condition<T> {
	/**
	 * Checks the condition.
	 * 
	 * @return true, if the condition is verified, false if not.
	 */
	public Boolean check(T obj);
}
