package org.argeo.slc.detached;

public interface StaticRefProvider {
	/** Returns null if no such ref. */
	public Object getStaticRef(String id);
}
