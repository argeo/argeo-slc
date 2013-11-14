package org.argeo.slc.akb;

import java.sql.PreparedStatement;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

/** Provides method interfaces to manage an AKB repository */
public interface AkbService {

	/** Creates a pre-configured AKB Template */
	public Node createAkbTemplate(Node parent, String name)
			throws RepositoryException;

	/** Creates a new pre-configured AKB connector Alias for the given template */
	public Node createConnectorAlias(Node templateNode, String name,
			String connectorType) throws RepositoryException;

	/**
	 * @param templateNode
	 * @param connectorType
	 *            if null, returns all defined connector for this template
	 * @return
	 * @throws RepositoryException
	 */
	public NodeIterator getDefinedAliases(Node templateNode,
			String connectorType) throws RepositoryException;

	/**
	 * @param envNode
	 *            an environment or a template
	 * @param aliasName
	 *            the alias of the node to get
	 * @return
	 * @throws RepositoryException
	 */
	public Node getConnectorByAlias(Node envNode, String aliasName)
			throws RepositoryException;

	/**
	 * Shortcut to perform whatever test on a given connector only to check if
	 * URL is correctly defined, if the target system is there and if the
	 * current user has the sufficient credentials to connect
	 * 
	 * If no active environment is defined, try to
	 */
	public boolean testConnector(Node connector);

	/**
	 * If no active environment is defined, tries to execute query with default
	 * connector defined for the template
	 */
	public PreparedStatement prepareJdbcQuery(Node activeEnvironment, Node node);

	public String executeCommand(Node activeEnvironment, Node node);

	public String retrieveFile(Node activeEnvironment, Node node);
}