package org.argeo.slc.jemmy;

public interface PopupMenuActuator extends Accessor {
	/**
	 * Popup the context menu and optionally select an item.
	 * @param menu - A list of items to choose on each menu level, separated by '|'
	 */
	void select(String menu);
}
