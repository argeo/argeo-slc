package org.argeo.slc.akb;

/** Maps AKB specific JCR node types with java constants  */
public interface AkbTypes {

	public final static String ARGEO_NOTE = "argeo:note";

	
	// Env and templates 
	public final static String AKB_ENV_TEMPLATE = "akb:envTemplate";
	public final static String AKB_ENV= "akb:env";

	// Connectors
	public final static String AKB_CONNECTOR = "akb:connector";  

	// Item tree 
	public final static String AKB_ITEM_FOLDER = "akb:itemsFolder";
	public final static String AKB_ITEM = "akb:item";
	
	// Various items types
	public final static String AKB_SSH_FILE = "akb:sshFile";
	public final static String AKB_SSH_COMMAND = "akb:sshCommand";
	public final static String AKB_JDBC_QUERY = "akb:jdbcQuery";
	public final static String AKB_NOTE = "akb:note";


}