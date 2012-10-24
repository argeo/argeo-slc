package org.argeo.slc.client.ui.dist;

import javax.jcr.Credentials;
import javax.jcr.GuestCredentials;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.SimpleCredentials;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.ArgeoException;
import org.argeo.jcr.ArgeoJcrUtils;
import org.argeo.jcr.ArgeoNames;
import org.argeo.jcr.ArgeoTypes;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.jcr.SlcNames;
import org.argeo.slc.jcr.SlcTypes;
import org.argeo.util.security.Keyring;

/** Static utilities */
public class DistUiUtils implements ArgeoNames, SlcNames {
	private final static Log log = LogFactory.getLog(DistUiUtils.class);

	/** Retrieve repository based on information in the repo node */
	public static Repository getRepository(RepositoryFactory repositoryFactory,
			Keyring keyring, Node repoNode) {
		try {
			Repository repository;
			if (repoNode.isNodeType(ArgeoTypes.ARGEO_REMOTE_REPOSITORY)) {
				String uri = repoNode.getProperty(ARGEO_URI).getString();
				if (uri.startsWith("http")) {// http, https
					repository = ArgeoJcrUtils.getRepositoryByUri(
							repositoryFactory, uri);
				} else {// alias
					String alias = uri;
					repository = ArgeoJcrUtils.getRepositoryByAlias(
							repositoryFactory, alias);
				}
				return repository;
			} else {
				throw new SlcException("Unsupported node type " + repoNode);
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot connect to repository " + repoNode,
					e);
		}

	}

	/**
	 * Reads credentials from node, using keyring if there is a password. Cann
	 * return null if no credentials needed (local repo) at all, but returns
	 * {@link GuestCredentials} if user id is 'anonymous' .
	 */
	public static Credentials getRepositoryCredentials(Keyring keyring,
			Node repoNode) {
		try {
			if (repoNode.isNodeType(ArgeoTypes.ARGEO_REMOTE_REPOSITORY)) {
				if (!repoNode.hasProperty(ARGEO_USER_ID))
					return null;

				String userId = repoNode.getProperty(ARGEO_USER_ID).getString();
				if (userId.equals("anonymous"))// FIXME hardcoded userId
					return new GuestCredentials();
				char[] password = keyring.getAsChars(repoNode.getPath() + '/'
						+ ARGEO_PASSWORD);
				Credentials credentials = new SimpleCredentials(userId,
						password);
				return credentials;
			} else {
				throw new SlcException("Unsupported node type " + repoNode);
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot connect to repository " + repoNode,
					e);
		}
	}

	/**
	 * Custom copy since the one in commons does not fit the needs when copying
	 * a workspace completely.
	 */
	public static void copy(Node fromNode, Node toNode) {
		try {
			if (log.isDebugEnabled())
				log.debug("copy node :" + fromNode.getPath());

			// FIXME : small hack to enable specific workspace copy
			if (fromNode.isNodeType("rep:ACL")
					|| fromNode.isNodeType("rep:system")) {
				if (log.isTraceEnabled())
					log.trace("node " + fromNode + " skipped");
				return;
			}

			// add mixins
			for (NodeType mixinType : fromNode.getMixinNodeTypes()) {
				toNode.addMixin(mixinType.getName());
			}

			// Double check
			for (NodeType mixinType : toNode.getMixinNodeTypes()) {
				if (log.isDebugEnabled())
					log.debug(mixinType.getName());
			}

			// process properties
			PropertyIterator pit = fromNode.getProperties();
			properties: while (pit.hasNext()) {
				Property fromProperty = pit.nextProperty();
				String propName = fromProperty.getName();
				try {
					String propertyName = fromProperty.getName();
					if (toNode.hasProperty(propertyName)
							&& toNode.getProperty(propertyName).getDefinition()
									.isProtected())
						continue properties;

					if (fromProperty.getDefinition().isProtected())
						continue properties;

					if (propertyName.equals("jcr:created")
							|| propertyName.equals("jcr:createdBy")
							|| propertyName.equals("jcr:lastModified")
							|| propertyName.equals("jcr:lastModifiedBy"))
						continue properties;

					if (fromProperty.isMultiple()) {
						toNode.setProperty(propertyName,
								fromProperty.getValues());
					} else {
						toNode.setProperty(propertyName,
								fromProperty.getValue());
					}
				} catch (RepositoryException e) {
					throw new ArgeoException("Cannot property " + propName, e);
				}
			}

			// recursively process children nodes
			NodeIterator nit = fromNode.getNodes();
			while (nit.hasNext()) {
				Node fromChild = nit.nextNode();
				Integer index = fromChild.getIndex();
				String nodeRelPath = fromChild.getName() + "[" + index + "]";
				Node toChild;
				if (toNode.hasNode(nodeRelPath))
					toChild = toNode.getNode(nodeRelPath);
				else
					toChild = toNode.addNode(fromChild.getName(), fromChild
							.getPrimaryNodeType().getName());
				copy(fromChild, toChild);
			}

			// update jcr:lastModified and jcr:lastModifiedBy in toNode in
			// case
			// they existed
			if (!toNode.getDefinition().isProtected()
					&& toNode.isNodeType(NodeType.MIX_LAST_MODIFIED))
				JcrUtils.updateLastModified(toNode);

			// Workaround to reduce session size: artifact is a saveable
			// unity
			if (toNode.isNodeType(SlcTypes.SLC_ARTIFACT))
				toNode.getSession().save();

		} catch (RepositoryException e) {
			throw new ArgeoException("Cannot copy " + fromNode + " to "
					+ toNode, e);
		}
	}

	private DistUiUtils() {

	}
}
