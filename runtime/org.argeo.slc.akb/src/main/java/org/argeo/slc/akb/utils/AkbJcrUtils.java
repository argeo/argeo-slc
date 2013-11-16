package org.argeo.slc.akb.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import org.argeo.jcr.JcrUtils;
import org.argeo.jcr.PropertyDiff;
import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbNames;
import org.argeo.slc.akb.AkbTypes;

/** Some static utils methods that might be factorized in a near future */
public class AkbJcrUtils {

	// /////////////////////////
	// SPECIFIC METHOS
	/**
	 * Returns the list of environment templates that are visible for the
	 * current user.
	 */
	public static List<Node> getDefinedTemplate(Session session) {
		try {
			if (session.nodeExists(AkbNames.AKB_TEMPLATES_BASE_PATH)) {
				NodeIterator ni = session.getNode(
						AkbNames.AKB_TEMPLATES_BASE_PATH).getNodes();
				List<Node> templates = new ArrayList<Node>();
				while (ni.hasNext()) {
					Node currN = ni.nextNode();
					if (currN.isNodeType(AkbTypes.AKB_ENV_TEMPLATE))
						templates.add(currN);
				}
				return templates;
			}
			return null;
		} catch (RepositoryException re) {
			throw new AkbException("Unable to list templates", re);
		}
	}

	/**
	 * Returns a template given it's name
	 */
	public static Node getTemplateByName(Session session, String name) {
		try {
			if (name == null)
				return null;
			if (session.nodeExists(AkbNames.AKB_TEMPLATES_BASE_PATH)) {
				NodeIterator ni = session.getNode(
						AkbNames.AKB_TEMPLATES_BASE_PATH).getNodes();
				while (ni.hasNext()) {
					Node currN = ni.nextNode();
					if (name.equals(AkbJcrUtils.get(currN, Property.JCR_TITLE)))
						return currN;
				}
			}
			return null;
		} catch (RepositoryException re) {
			throw new AkbException("Unable to list templates", re);
		}
	}

	/**
	 * Return the type of alias that must be used given current item type
	 */
	public static String getAliasTypeForNode(Node itemTemplate) {
		try {
			if (itemTemplate.isNodeType(AkbTypes.AKB_JDBC_QUERY))
				return AkbTypes.AKB_JDBC_CONNECTOR;
			else if (itemTemplate.isNodeType(AkbTypes.AKB_SSH_COMMAND)
					|| itemTemplate.isNodeType(AkbTypes.AKB_SSH_FILE))
				return AkbTypes.AKB_SSH_CONNECTOR;
			else
				throw new AkbException("No connector type define for node "
						+ itemTemplate);
		} catch (RepositoryException re) {
			throw new AkbException("Unable to login", re);
		}
	}

	/**
	 * Return current template depending on the passed node
	 */
	public static Node getCurrentTemplate(Node akbNode) {
		try {
			if (akbNode.getDepth() == 0)
				// no base path for root node
				return null;
			Node parNode = akbNode.getParent();

			while (parNode != null)
				if (akbNode.isNodeType(AkbTypes.AKB_ENV_TEMPLATE))
					return akbNode;
				else if (parNode.getDepth() == 0)
					// we found not fitting node
					return null;
				else {
					akbNode = parNode;
					parNode = parNode.getParent();
				}
			return null;
		} catch (RepositoryException re) {
			throw new AkbException("Unable to find template for node "
					+ akbNode, re);
		}
	}

	/**
	 * Return the current env base path
	 */
	public static String getCurrentEnvBasePath(Node akbNode) {
		try {
			if (akbNode.getDepth() == 0)
				// no base path for root node
				return null;

			Node parNode = akbNode.getParent();

			while (parNode != null)
				if (akbNode.isNodeType(AkbTypes.AKB_ENV)
						|| akbNode.isNodeType(AkbTypes.AKB_ENV_TEMPLATE))
					return akbNode.getPath();
				else if (parNode.getDepth() == 0)
					// we found not fitting node
					return null;
				else {
					akbNode = parNode;
					parNode = parNode.getParent();
				}
			return null;
		} catch (RepositoryException re) {
			throw new AkbException("Unable to login", re);
		}
	}

	// //////////////////////////////////
	// METHODS THAT CAN BE FACTORIZED
	/**
	 * Call {@link Repository#login()} without exceptions (useful in super
	 * constructors and dependency injection).
	 */
	public static Session login(Repository repository) {
		try {
			return repository.login();
		} catch (RepositoryException re) {
			throw new AkbException("Unable to login", re);
		}
	}

	/**
	 * Convert a {@link rowIterator} to a list of {@link Node} given a selector
	 * name. It relies on the <code>Row.getNode(String selectorName)</code>
	 * method.
	 */
	public static List<Node> rowIteratorToList(RowIterator rowIterator,
			String selectorName) throws RepositoryException {
		List<Node> nodes = new ArrayList<Node>();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.nextRow();
			if (row.getNode(selectorName) != null)
				nodes.add(row.getNode(selectorName));
		}
		return nodes;
	}

	/**
	 * Check if a string is null or an empty string (a string with only spaces
	 * is considered as empty
	 */
	public static boolean isEmptyString(String stringToTest) {
		return stringToTest == null || "".equals(stringToTest.trim());
	}

	/**
	 * Check if a string is null or an empty string (a string with only spaces
	 * is considered as empty
	 */
	public static boolean checkNotEmptyString(String string) {
		return string != null && !"".equals(string.trim());
	}

	/**
	 * Wraps the versionMananger.isCheckedOut(path) method to adapt it to the
	 * current check in / check out policy.
	 * 
	 * TODO : add management of check out by others.
	 */
	public static boolean isNodeCheckedOut(Node node) {
		try {
			return node.getSession().getWorkspace().getVersionManager()
					.isCheckedOut(node.getPath());
		} catch (RepositoryException re) {
			throw new AkbException("Unable to get check out status for node",
					re);
		}
	}

	/**
	 * For the time being, same as isNodeCheckedOut(Node node).
	 * 
	 * TODO : add management of check out by others.
	 */
	public static boolean isNodeCheckedOutByMe(Node node) {
		return isNodeCheckedOut(node);
	}

	/**
	 * Wraps the versionMananger.checkedOut(path) method to adapt it to the
	 * current check in / check out policy.
	 * 
	 * TODO : add management of check out by others.
	 */
	public static void checkout(Node node) {
		try {
			node.getSession().getWorkspace().getVersionManager()
					.checkout(node.getPath());
		} catch (RepositoryException re) {
			throw new AkbException("Unable to check out Node", re);
		}
	}

	/**
	 * Wraps the versionMananger.checkedIn(path) method to adapt it to the
	 * current check in / check out policy.
	 * 
	 * It also checked if the current entity has to be moved or not. TODO : add
	 * management of check out by others.
	 */
	public static void saveAndCheckin(Node node) {
		try {
			JcrUtils.updateLastModified(node);
			node.getSession().save();
			node.getSession().getWorkspace().getVersionManager()
					.checkin(node.getPath());
		} catch (RepositoryException re) {
			throw new AkbException("Unable to save and chek in node", re);
		}
	}

	/**
	 * Wraps the versionMananger.checkedIn(path) method to adapt it to the
	 * current check in / check out policy.
	 * 
	 * TODO : add management of check out by others. TODO : manage usecase where
	 * a node that has never been checked in (draft node) is canceled and thus
	 * must be deleted
	 */
	public static void cancelAndCheckin(Node node) {
		try {
			String path = node.getPath();
			Session session = node.getSession();
			JcrUtils.discardUnderlyingSessionQuietly(node);
			// if the node has never been saved, it does not exist anymore.
			if (session.nodeExists(path))
				session.getWorkspace().getVersionManager().checkin(path);
		} catch (RepositoryException re) {
			throw new AkbException("Unable to save and chek in node", re);
		}
	}

	/**
	 * Concisely get the string value of a property. It returns an empty String
	 * rather than null if this node doesn't have this property or if the
	 * corresponding property is an empty string.
	 */
	public static String get(Node node, String propertyName) {
		try {
			if (!node.hasProperty(propertyName))
				return "";
			else
				return node.getProperty(propertyName).getString();
		} catch (RepositoryException e) {
			throw new AkbException("Cannot get property " + propertyName
					+ " of " + node, e);
		}
	}

	/**
	 * Concisely get the value of a property or null if this node doesn't have
	 * this property
	 */
	public static Boolean getBooleanValue(Node node, String propertyName) {
		try {
			if (!node.hasProperty(propertyName))
				return null;
			else
				return node.getProperty(propertyName).getBoolean();
		} catch (RepositoryException e) {
			throw new AkbException("Cannot get boolean property "
					+ propertyName + " of " + node, e);
		}
	}

	/**
	 * Concisely get the identifier of a node in Ui listener for instance
	 * */
	public static String getIdentifierQuietly(Node node) {
		try {
			return node.getIdentifier();
		} catch (RepositoryException e) {
			throw new AkbException("Cannot get identifier for node " + node, e);
		}
	}

	public static Map<String, PropertyDiff> diffProperties(Node reference,
			Node observed) {
		Map<String, PropertyDiff> diffs = new TreeMap<String, PropertyDiff>();
		diffPropertiesLevel(diffs, null, reference, observed);
		return diffs;
	}

	/**
	 * Compare the properties of two nodes. Extends
	 * <code>JcrUtils.diffPropertiesLevel</code> to also track differences in
	 * multiple value properties and sub graph. No property is skipped (among
	 * other all technical jcr:... properties) to be able to track jcr:title and
	 * description properties, among other. Filtering must be applied afterwards
	 * to only keep relevant properties.
	 */
	static void diffPropertiesLevel(Map<String, PropertyDiff> diffs,
			String baseRelPath, Node reference, Node observed) {
		try {
			// check removed and modified
			PropertyIterator pit = reference.getProperties();
			while (pit.hasNext()) {
				Property p = pit.nextProperty();
				String name = p.getName();
				// if (name.startsWith("jcr:"))
				// continue props;

				if (!observed.hasProperty(name)) {
					String relPath = propertyRelPath(baseRelPath, name);
					PropertyDiff pDiff = new PropertyDiff(PropertyDiff.REMOVED,
							relPath, p.getValue(), null);
					diffs.put(relPath, pDiff);
				} else {
					if (p.isMultiple()) {
						int i = 0;

						Value[] refValues = p.getValues();
						Value[] newValues = observed.getProperty(name)
								.getValues();
						String relPath = propertyRelPath(baseRelPath, name);
						refValues: for (Value refValue : refValues) {
							for (Value newValue : newValues) {
								if (refValue.equals(newValue))
									continue refValues;
							}
							PropertyDiff pDiff = new PropertyDiff(
									PropertyDiff.REMOVED, relPath, refValue,
									null);
							diffs.put(relPath + "_" + i++, pDiff);
						}

						newValues: for (Value newValue : newValues) {
							for (Value refValue : refValues) {
								if (refValue.equals(newValue))
									continue newValues;
							}
							PropertyDiff pDiff = new PropertyDiff(
									PropertyDiff.ADDED, relPath, null, newValue);
							diffs.put(relPath + "_" + i++, pDiff);
						}

					} else {
						Value referenceValue = p.getValue();
						Value newValue = observed.getProperty(name).getValue();
						if (!referenceValue.equals(newValue)) {
							String relPath = propertyRelPath(baseRelPath, name);
							PropertyDiff pDiff = new PropertyDiff(
									PropertyDiff.MODIFIED, relPath,
									referenceValue, newValue);
							diffs.put(relPath, pDiff);
						}
					}
				}
			}
			// check added
			pit = observed.getProperties();
			// props:
			while (pit.hasNext()) {
				Property p = pit.nextProperty();
				String name = p.getName();
				// if (name.startsWith("jcr:"))
				// continue props;
				if (!reference.hasProperty(name)) {
					String relPath = propertyRelPath(baseRelPath, name);
					if (p.isMultiple()) {
						Value[] newValues = observed.getProperty(name)
								.getValues();
						int i = 0;
						for (Value newValue : newValues) {
							PropertyDiff pDiff = new PropertyDiff(
									PropertyDiff.ADDED, relPath, null, newValue);
							diffs.put(relPath + "_" + i++, pDiff);
						}
					} else {
						PropertyDiff pDiff = new PropertyDiff(
								PropertyDiff.ADDED, relPath, null, p.getValue());
						diffs.put(relPath, pDiff);
					}
				}
			}
		} catch (RepositoryException e) {
			throw new AkbException("Cannot diff " + reference + " and "
					+ observed, e);
		}
	}

	/** Builds a property relPath to be used in the diff. */
	private static String propertyRelPath(String baseRelPath,
			String propertyName) {
		if (baseRelPath == null)
			return propertyName;
		else
			return baseRelPath + '/' + propertyName;
	}

	/** prevent instantiation by others */
	private AkbJcrUtils() {
	}

}