package org.argeo.internal.cms.dbus;

import org.freedesktop.dbus.annotations.DBusInterfaceName;
import org.freedesktop.dbus.annotations.DBusMemberName;
import org.freedesktop.dbus.interfaces.DBusInterface;

/**
 * The org.freedesktop.Application interface.
 */
@DBusInterfaceName("org.freedesktop.Application")
public interface FreeDesktopApplicationInterface extends DBusInterface {

	@DBusMemberName(value = "Activate")
	void activate();

	@DBusMemberName(value = "Open")
	void open(String[] uri);

	@DBusMemberName(value = "ActivateAction")
	void activateAction(String actionName, String[] parameters);

}