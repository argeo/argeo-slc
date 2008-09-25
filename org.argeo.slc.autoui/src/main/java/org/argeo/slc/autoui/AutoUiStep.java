package org.argeo.slc.autoui;

public interface AutoUiStep {
	/** Runs synchronously. */
	public void run();

	/** Closes and releases the associated resources. */
	public void close();

	/**
	 * Sets an input parameter. Value can be various Java type and implementors
	 * are supposed to document which data types are supported.
	 */
	public void setInputParameter(Object key, Object value);

	/**
	 * Gets an output parameter. Implementors are supposed to convert the
	 * returned value to the appropriate data type.
	 */
	public Object getOutputParameter(Object key);

}
