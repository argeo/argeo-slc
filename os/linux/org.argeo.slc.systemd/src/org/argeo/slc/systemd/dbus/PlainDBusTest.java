package org.argeo.slc.systemd.dbus;

import java.util.Map;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.interfaces.Introspectable;
import org.freedesktop.dbus.interfaces.Properties;
import org.freedesktop.dbus.types.Variant;

public class PlainDBusTest {
	final static String SYSTEMD_SERVICE = "org.freedesktop.systemd1.Service";

	public static void main(String[] args) throws Exception {
//		try (DBusConnection dBusConnection = DBusConnectionBuilder.forSystemBus().build()) {

		try (DBusConnection dBusConnection = DBusConnection.getConnection(DBusConnection.DBusBusType.SYSTEM)) {

			String source = "org.freedesktop.systemd1";
			String objectPath = "/org/freedesktop/systemd1/unit/ipsec_2eservice";
//		String objectPath = "/org/freedesktop/systemd1";
			DBusInterface object = dBusConnection.getExportedObject(source, objectPath);
			System.out.println(object);
//
//			Introspectable introspectable = dBusConnection.getExportedObject(source, objectPath, Introspectable.class);
//			System.out.println(introspectable.Introspect());
//
			Properties props = dBusConnection.getExportedObject(source, objectPath, Properties.class);
//		System.out.println(props);

			System.out.println(props.Get(SYSTEMD_SERVICE, "CPUUsageNSec").toString());

			Map<String, Variant<?>> values = props.GetAll(SYSTEMD_SERVICE);
			for (String key : values.keySet()) {
				Variant<?> value = values.get(key);
				System.out.println(key + "=" + value.getValue() + " (" + value.getType() + ") " + value.getSig());
			}
		}
	}

}
