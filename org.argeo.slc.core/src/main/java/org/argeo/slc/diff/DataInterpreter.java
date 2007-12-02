package org.argeo.slc.diff;

/**
 * Converts data into a format better adapted for comparison. It is typically
 * used to convert <code>String</code> into typed format such as
 * <code>BigDecimal</code>
 */
public interface DataInterpreter {
	/**
	 * Converts data
	 * 
	 * @param key
	 *            any object used to differentiate the type of data (e.g.
	 *            column, path)
	 * @param value
	 *            the data to convert
	 * @return the converted object
	 */
	public Object convert(Object key, Object value);
}
