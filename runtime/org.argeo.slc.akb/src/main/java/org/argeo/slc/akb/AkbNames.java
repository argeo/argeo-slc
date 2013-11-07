package org.argeo.slc.akb;

/** Maps AKB specific JCR Property names with java constants */
public interface AkbNames {

	public final static String AKB_NAMESPACE = "http://www.argeo.org/ns/akb";

	/* DEFAULT BASE PATHS */
	public final static String AKB_BASE_PATH = "/akb:system";
	public final static String AKB_TEMPLATES_BASE_PATH = AKB_BASE_PATH + "/"
			+ "akb:templates";

	public final static String AKB_ENVIRONMENTS_BASE_PATH = AKB_BASE_PATH + "/"
			+ "akb:environments";

	/* ENVIRONMENT PROPERTIES */

	/* CONNECTOR PROPERTIES */
	public final static String AKB_CONNECTOR_URL = "akb:connectorUrl";

	/* ITEMS PROPERTIES */

}