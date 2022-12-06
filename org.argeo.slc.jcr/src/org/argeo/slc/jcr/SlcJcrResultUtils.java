package org.argeo.slc.jcr;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.argeo.cms.jcr.CmsJcrUtils;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;

/**
 * Utilities around the SLC JCR Result model. Note that it relies on fixed base
 * paths (convention over configuration) for optimization purposes.
 */
public class SlcJcrResultUtils {

	/**
	 * Returns the path to the current slc:result node
	 */
	public static String getSlcResultsBasePath(Session session) {
		try {
			Node userHome = CmsJcrUtils.getUserHome(session);
			if (userHome == null)
				throw new SlcException("No user home available for "
						+ session.getUserID());
			return userHome.getPath() + '/' + SlcNames.SLC_SYSTEM + '/'
					+ SlcNames.SLC_RESULTS;
		} catch (RepositoryException re) {
			throw new SlcException(
					"Unexpected error while getting Slc Results Base Path.", re);
		}
	}

	/**
	 * Returns the base node to store SlcResults. If it does not exists, it is
	 * created. If a node already exists at the given path with the wrong type,
	 * it throws an exception.
	 * 
	 * @param session
	 */
	public static Node getSlcResultsParentNode(Session session) {
		try {
			String absPath = getSlcResultsBasePath(session);
			if (session.nodeExists(absPath)) {
				Node currNode = session.getNode(absPath);
				if (currNode.isNodeType(NodeType.NT_UNSTRUCTURED))
					return currNode;
				else
					throw new SlcException(
							"A node already exists at this path : " + absPath
									+ " that has the wrong type. ");
			} else {
				Node slcResParNode = JcrUtils.mkdirs(session, absPath);
				slcResParNode.setPrimaryType(NodeType.NT_UNSTRUCTURED);
				session.save();
				return slcResParNode;
			}
		} catch (RepositoryException re) {
			throw new SlcException(
					"Unexpected error while creating slcResult root parent node.",
					re);
		}
	}

	/**
	 * Returns the path to the current Result UI specific node, depending the
	 * current user
	 */
	public static String getMyResultsBasePath(Session session) {
		try {
			Node userHome = CmsJcrUtils.getUserHome(session);
			if (userHome == null)
				throw new SlcException("No user home available for "
						+ session.getUserID());
			return userHome.getPath() + '/' + SlcNames.SLC_SYSTEM + '/'
					+ SlcNames.SLC_MY_RESULTS;
		} catch (RepositoryException re) {
			throw new SlcException(
					"Unexpected error while getting Slc Results Base Path.", re);
		}
	}

	/**
	 * Creates a new node with type SlcTypes.SLC_MY_RESULT_ROOT_FOLDER at the
	 * given absolute path. If a node already exists at the given path, returns
	 * that node if it has the correct type and throws an exception otherwise.
	 * 
	 * @param session
	 */
	public static Node getMyResultParentNode(Session session) {
		try {
			String absPath = getMyResultsBasePath(session);
			if (session.nodeExists(absPath)) {
				Node currNode = session.getNode(absPath);
				if (currNode.isNodeType(SlcTypes.SLC_MY_RESULT_ROOT_FOLDER))
					return currNode;
				else
					throw new SlcException(
							"A node already exists at this path : " + absPath
									+ " that has the wrong type. ");
			} else {
				Node myResParNode = JcrUtils.mkdirs(session, absPath);
				myResParNode.setPrimaryType(SlcTypes.SLC_MY_RESULT_ROOT_FOLDER);
				session.save();
				return myResParNode;
			}
		} catch (RepositoryException re) {
			throw new SlcException(
					"Unexpected error while creating user MyResult base node.",
					re);
		}
	}

	/**
	 * Creates a new node with type SlcTypes.SLC_RESULT_FOLDER at the given
	 * absolute path. If a node already exists at the given path, returns that
	 * node if it has the correct type and throws an exception otherwise.
	 * 
	 * @param session
	 * @param absPath
	 */
	public static synchronized Node createResultFolderNode(Session session,
			String absPath) {
		try {
			if (session.nodeExists(absPath)) {
				// Sanity check
				Node currNode = session.getNode(absPath);
				if (currNode.isNodeType(SlcTypes.SLC_RESULT_FOLDER))
					return currNode;
				else
					throw new SlcException(
							"A node already exists at this path : " + absPath
									+ " that has the wrong type. ");
			}
			Node rfNode = JcrUtils.mkdirs(session, absPath);
			rfNode.setPrimaryType(SlcTypes.SLC_RESULT_FOLDER);
			Node statusNode = rfNode.addNode(SlcNames.SLC_AGGREGATED_STATUS,
					SlcTypes.SLC_CHECK);
			statusNode.setProperty(SlcNames.SLC_SUCCESS, true);
			session.save();
			return rfNode;
		} catch (RepositoryException re) {
			throw new SlcException(
					"Unexpected error while creating Result Folder node.", re);
		}
	}
}