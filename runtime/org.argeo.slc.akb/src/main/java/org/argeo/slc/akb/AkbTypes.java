package org.argeo.slc.akb;

/** Maps AKB specific JCR node types with java constants */
public interface AkbTypes {

	public final static String ARGEO_NOTE = "argeo:note";

	// Env and templates
	public final static String AKB_ENV_TEMPLATE = "akb:envTemplate";
	public final static String AKB_ENV = "akb:env";

	// Connectors

	public final static String AKB_CONNECTOR_FOLDER = "akb:connectorFolder";
	public final static String AKB_CONNECTOR = "akb:connector";
	public final static String AKB_CONNECTOR_ALIAS = "akb:connectorAlias";

	// Various connector mixin types
	public final static String AKB_SSH_CONNECTOR = "akb:sshConnector";
	public final static String AKB_JDBC_CONNECTOR = "akb:jdbcConnector";
	public final static String AKB_JCR_CONNECTOR = "akb:jcrConnector";

	// Item tree
	public final static String AKB_ITEM_FOLDER = "akb:itemsFolder";
	public final static String AKB_ITEM = "akb:item";

	// Various items types
	public final static String AKB_SSH_FILE = "akb:sshFile";
	public final static String AKB_SSH_COMMAND = "akb:sshCommand";
	public final static String AKB_JDBC_QUERY = "akb:jdbcQuery";
	public final static String AKB_NOTE = "akb:note";

}