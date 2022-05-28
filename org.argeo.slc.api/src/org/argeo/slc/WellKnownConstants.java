package org.argeo.slc;

/**
 * Centralises constant values related to software systems, which are well
 * defined and not expected to change within a major version cycle of SLC, but
 * which are not necessarily defined in core Java.
 */
public interface WellKnownConstants {

	/*
	 * OSGi
	 */
	final static String OSGI_INSTANCE_AREA = "osgi.instance.area";
	final static String OSGI_CONFIGURATION_AREA = "osgi.configuration.area";

	final static String OSGI_HTTP_PORT = "org.osgi.service.http.port";
}
