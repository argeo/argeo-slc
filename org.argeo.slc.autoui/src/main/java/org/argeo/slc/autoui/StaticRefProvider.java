package org.argeo.slc.autoui;

public interface StaticRefProvider {
	/** Returns null if no such ref. */
	public Object getStaticRef(String id);
}
