package org.argeo.slc.akb.core;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Map;

import javax.annotation.Resource;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelConstants;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.Selector;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.ArgeoNames;
import org.argeo.jcr.JcrUtils;
import org.argeo.jcr.UserJcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbNames;
import org.argeo.slc.akb.AkbService;
import org.argeo.slc.akb.AkbTypes;
import org.argeo.slc.jsch.SimpleUserInfo;
import org.argeo.util.security.Keyring;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;

/**
 * Concrete access to akb services. It provides among other an initialized
 * environment
 */
public class AkbServiceImpl implements AkbService, AkbNames {
	private final static Log log = LogFactory.getLog(AkbServiceImpl.class);

	/* DEPENDENCY INJECTION */
	private Repository repository;

	private Keyring keyring;

	// Populate the repository in a demo context.
	private Map<String, Resource> demoData = null;

	/* Life cycle management */
	/**
	 * Call by each startup in order to make sure the backend is ready to
	 * receive/provide data.
	 */
	public void init() {
		// JDBC drivers
		// TODO make it configurable
		initJdbcDriver("org.postgresql.Driver");

		Session adminSession = null;
		try {
			adminSession = repository.login();

			// Initialization of the model
			if (!adminSession.nodeExists(AKB_TEMPLATES_BASE_PATH)) {
				JcrUtils.mkdirs(adminSession, AKB_TEMPLATES_BASE_PATH);
				JcrUtils.mkdirs(adminSession, AKB_ENVIRONMENTS_BASE_PATH);
				adminSession.save();
				log.info("Repository has been initialized "
						+ "with AKB's model");
			}

			// Fill the repository in a demo context
			if (demoData != null) {
				// Dev only force reload at each start
				// if (true) {
				// if (!projectsPar.hasNodes()) {
			}
		} catch (Exception e) {
			throw new AkbException("Cannot initialize backend", e);
		} finally {
			JcrUtils.logoutQuietly(adminSession);
		}
	}

	protected Boolean initJdbcDriver(String driver) {
		try {
			Class.forName(driver);
			return true;
		} catch (ClassNotFoundException e) {
			if (log.isDebugEnabled())
				log.debug("Cannot load JDBC driver : " + driver + ", "
						+ e.getMessage());
			return false;
		}
	}

	/** Clean shutdown of the backend. */
	public void destroy() {
	}

	@Override
	public Node createAkbTemplate(Node parentNode, String name)
			throws RepositoryException {
		String connectorParentName = "Connectors";

		Node newTemplate = parentNode.addNode(name, AkbTypes.AKB_ENV_TEMPLATE);
		newTemplate.setProperty(Property.JCR_TITLE, name);

		Node connectorParent = newTemplate.addNode(
				AkbTypes.AKB_CONNECTOR_FOLDER, AkbTypes.AKB_CONNECTOR_FOLDER);
		connectorParent.setProperty(Property.JCR_TITLE, connectorParentName);

		return newTemplate;
	}

	// ///////////////////////////////////////
	// / CONNECTORS

	@Override
	public Node createConnectorAlias(Node templateNode, String name,
			String connectorType) throws RepositoryException {
		Node parent = JcrUtils.mkdirs(templateNode,
				AkbTypes.AKB_CONNECTOR_FOLDER, AkbTypes.AKB_CONNECTOR_FOLDER);
		Node newConnector = parent.addNode(name, AkbTypes.AKB_CONNECTOR_ALIAS);
		newConnector.setProperty(Property.JCR_TITLE, name);
		newConnector.setProperty(AkbNames.AKB_CONNECTOR_TYPE, connectorType);

		// Node defaultConnector =
		Node defaultConn = newConnector.addNode(
				AkbNames.AKB_DEFAULT_TEST_CONNECTOR, connectorType);
		defaultConn.setProperty(AkbNames.AKB_CONNECTOR_ALIAS_NAME, name);
		return newConnector;
	}

	@Override
	public NodeIterator getDefinedAliases(Node itemTemplate,
			String connectorType) throws RepositoryException {
		try {
			Session session = itemTemplate.getSession();
			QueryManager queryManager = session.getWorkspace()
					.getQueryManager();
			QueryObjectModelFactory factory = queryManager.getQOMFactory();

			Selector source = factory.selector(AkbTypes.AKB_CONNECTOR_ALIAS,
					AkbTypes.AKB_CONNECTOR_ALIAS);
			Constraint defaultC = factory.descendantNode(
					source.getSelectorName(), itemTemplate.getPath());

			if (connectorType != null) {
				Constraint connType = factory.comparison(factory.propertyValue(
						source.getSelectorName(), AkbNames.AKB_CONNECTOR_TYPE),
						QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO,
						factory.literal(session.getValueFactory().createValue(
								connectorType)));
				defaultC = factory.and(defaultC, connType);
			}

			// Order by default by JCR TITLE
			// TODO check if node definition has MIX_TITLE mixin
			// TODO Apparently case insensitive ordering is not implemented in
			// current used JCR implementation
			Ordering order = factory
					.ascending(factory.upperCase(factory.propertyValue(
							source.getSelectorName(), Property.JCR_TITLE)));
			QueryObjectModel query;
			query = factory.createQuery(source, defaultC,
					new Ordering[] { order }, null);
			QueryResult result = query.execute();
			return result.getNodes();
		} catch (RepositoryException e) {
			throw new AkbException("Unable to list connector", e);
		}
	}

	@Override
	public Node getConnectorByAlias(Node envNode, String aliasName)
			throws RepositoryException {
		try {
			Session session = envNode.getSession();
			QueryManager queryManager = session.getWorkspace()
					.getQueryManager();
			QueryObjectModelFactory factory = queryManager.getQOMFactory();

			Selector source = factory.selector(AkbTypes.AKB_CONNECTOR,
					AkbTypes.AKB_CONNECTOR);
			Constraint defaultC = factory.descendantNode(
					source.getSelectorName(), envNode.getPath());

			Constraint connType = factory.comparison(
					factory.propertyValue(source.getSelectorName(),
							AkbNames.AKB_CONNECTOR_ALIAS_NAME),
					QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO, factory
							.literal(session.getValueFactory().createValue(
									aliasName)));
			defaultC = factory.and(defaultC, connType);

			QueryObjectModel query;
			query = factory.createQuery(source, defaultC, null, null);
			QueryResult result = query.execute();
			NodeIterator ni = result.getNodes();

			if (!ni.hasNext())
				return null;
			else {
				Node connector = ni.nextNode();
				if (ni.hasNext())
					throw new AkbException("More than  one alias with name "
							+ aliasName + " has been defined for environment "
							+ envNode);
				else
					return connector;
			}
		} catch (RepositoryException e) {
			throw new AkbException("Unable to get connector " + aliasName
					+ " in " + envNode, e);
		}
	}

	@Override
	public boolean testConnector(Node connectorNode) {
		try {
			if (connectorNode.isNodeType(AkbTypes.AKB_JDBC_CONNECTOR)) {
				String connectorUrl = connectorNode.getProperty(
						AKB_CONNECTOR_URL).getString();
				String connectorUser = connectorNode.getProperty(
						AKB_CONNECTOR_USER).getString();

				String pwdPath = getPasswordPath(connectorNode);
				char[] pwd = keyring.getAsChars(pwdPath);
				DriverManager.getConnection(connectorUrl, connectorUser,
						new String(pwd));
				savePassword(connectorNode.getSession(), pwdPath, pwd);
				return true;
			} else if (connectorNode.isNodeType(AkbTypes.AKB_SSH_CONNECTOR)) {
				String connectorUrl = connectorNode.getProperty(
						AKB_CONNECTOR_URL).getString();
				String connectorUser = connectorNode.getProperty(
						AKB_CONNECTOR_USER).getString();
				String pwdPath = getPasswordPath(connectorNode);
				char[] pwd = keyring.getAsChars(pwdPath);

				URI url = new URI(connectorUrl);
				String host = url.getHost();
				int port = url.getPort();
				if (port == -1)
					port = 22;
				JSch jsch = new JSch();
				com.jcraft.jsch.Session sess = jsch.getSession(connectorUser,
						host, port);
				SimpleUserInfo userInfo = new SimpleUserInfo();
				userInfo.setPassword(new String(pwd));
				sess.setUserInfo(userInfo);
				sess.connect();
				sess.disconnect();

				savePassword(connectorNode.getSession(), pwdPath, pwd);
				return true;
			} else {
				throw new SlcException("Unsupported connector " + connectorNode);
			}
		} catch (Exception e) {
			throw new SlcException("Cannot test connection", e);
		}
	}

	/**
	 * Opens a new connection each time. All resources must be cleaned by
	 * caller.
	 */
	public PreparedStatement prepareJdbcQuery(Node activeEnv, Node node) {
		PreparedStatement statement = null;
		try {
			
			if (node.isNodeType(AkbTypes.AKB_JDBC_QUERY)) {
				String connectorPath = node.getProperty(AKB_USED_CONNECTOR)
						.getString();
				Node connectorNode = node.getSession().getNode(connectorPath);
				
				if (activeEnv != null){
					String aliasName = connectorNode.getProperty(Property.JCR_TITLE).getString();
					connectorNode = getConnectorByAlias(activeEnv, aliasName); 
				}

				String sqlQuery = node.getProperty(AKB_QUERY_TEXT).getString();

				String connectorUrl = connectorNode.getProperty(
						AKB_CONNECTOR_URL).getString();
				String connectorUser = connectorNode.getProperty(
						AKB_CONNECTOR_USER).getString();

				String pwdPath = getPasswordPath(connectorNode);
				// String pwdPath = connectorNode.getPath() + '/'
				// + ArgeoNames.ARGEO_PASSWORD;
				char[] pwd = keyring.getAsChars(pwdPath);
				Connection connection = DriverManager.getConnection(
						connectorUrl, connectorUser, new String(pwd));
				try {
					statement = connection.prepareStatement(sqlQuery,
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
				} catch (SQLFeatureNotSupportedException e) {
					log.warn("Scroll not supported for " + connectorUrl);
					statement = connection.prepareStatement(sqlQuery);
				}
			} else {
				throw new SlcException("Unsupported node " + node);
			}
			return statement;
		} catch (Exception e) {
			throw new SlcException("Cannot execute test JDBC query on " + node,
					e);
		}
	}

	public String executeCommand(Node activeEnv, Node node) {
		try {
			String command = node.getProperty(AkbNames.AKB_COMMAND_TEXT)
					.getString();

			String connectorPath = node.getProperty(AKB_USED_CONNECTOR)
					.getString();
			Node connectorNode = node.getSession().getNode(connectorPath);
			String connectorUrl = connectorNode.getProperty(AKB_CONNECTOR_URL)
					.getString();
			String connectorUser = connectorNode
					.getProperty(AKB_CONNECTOR_USER).getString();
			String pwdPath = getPasswordPath(connectorNode);
			char[] pwd = keyring.getAsChars(pwdPath);

			URI url = new URI(connectorUrl);
			String host = url.getHost();
			int port = url.getPort();
			if (port == -1)
				port = 22;
			JSch jsch = new JSch();
			com.jcraft.jsch.Session sess = jsch.getSession(connectorUser, host,
					port);
			SimpleUserInfo userInfo = new SimpleUserInfo();
			userInfo.setPassword(new String(pwd));
			sess.setUserInfo(userInfo);
			sess.connect();

			sess.openChannel("exec");
			final ChannelExec channel = (ChannelExec) sess.openChannel("exec");
			channel.setCommand(command);

			channel.setInputStream(null);
			channel.setXForwarding(false);
			channel.setAgentForwarding(false);
			channel.setErrStream(null);

			channel.connect();

			String output = IOUtils.toString(channel.getInputStream());
			channel.disconnect();

			sess.disconnect();

			return output;
		} catch (Exception e) {
			throw new SlcException("Cannot execute command", e);
		}

	}

	public String retrieveFile(Node activeEnv, Node node) {
		try {
			String filePath = node.getProperty(AkbNames.AKB_FILE_PATH)
					.getString();
			String command = "cat " + filePath;

			// TODO do a proper scp
			String connectorPath = node.getProperty(AKB_USED_CONNECTOR)
					.getString();
			Node connectorNode = node.getSession().getNode(connectorPath);
			String connectorUrl = connectorNode.getProperty(AKB_CONNECTOR_URL)
					.getString();
			String connectorUser = connectorNode
					.getProperty(AKB_CONNECTOR_USER).getString();
			String pwdPath = getPasswordPath(connectorNode);
			char[] pwd = keyring.getAsChars(pwdPath);

			URI url = new URI(connectorUrl);
			String host = url.getHost();
			int port = url.getPort();
			if (port == -1)
				port = 22;
			JSch jsch = new JSch();
			com.jcraft.jsch.Session sess = jsch.getSession(connectorUser, host,
					port);
			SimpleUserInfo userInfo = new SimpleUserInfo();
			userInfo.setPassword(new String(pwd));
			sess.setUserInfo(userInfo);
			sess.connect();

			sess.openChannel("exec");
			final ChannelExec channel = (ChannelExec) sess.openChannel("exec");
			channel.setCommand(command);

			channel.setInputStream(null);
			channel.setXForwarding(false);
			channel.setAgentForwarding(false);
			channel.setErrStream(null);

			channel.connect();

			String output = IOUtils.toString(channel.getInputStream());
			channel.disconnect();

			sess.disconnect();

			return output;
		} catch (Exception e) {
			throw new SlcException("Cannot execute command", e);
		}

	}

	protected String getPasswordPath(Node node) throws RepositoryException {
		Node home = UserJcrUtils.getUserHome(node.getSession());
		if (node.getPath().startsWith(home.getPath()))
			return node.getPath() + '/' + ArgeoNames.ARGEO_PASSWORD;
		else
			return home.getPath() + node.getPath() + '/'
					+ ArgeoNames.ARGEO_PASSWORD;
	}

	private void savePassword(Session session, String pwdPath, char[] pwd)
			throws RepositoryException {
		if (!session.itemExists(pwdPath)) {
			JcrUtils.mkdirs(session, JcrUtils.parentPath(pwdPath));
			session.save();
			keyring.set(pwdPath, pwd);
		}

	}

	// /** Expose injected repository */
	// public Repository getRepository() {
	// return repository;
	// }

	/* DEPENDENCY INJECTION */
	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public void setDemoData(Map<String, Resource> demoData) {
		this.demoData = demoData;
	}

	public void setKeyring(Keyring keyring) {
		this.keyring = keyring;
	}

}