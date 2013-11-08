package org.argeo.slc.akb.core;

import java.util.Map;

import javax.annotation.Resource;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.jcr.JcrUtils;
import org.argeo.slc.akb.AkbException;
import org.argeo.slc.akb.AkbNames;
import org.argeo.slc.akb.AkbService;
import org.argeo.slc.akb.AkbTypes;

/**
 * Concrete access to akb services. It provides among other an initialized
 * environment
 */
public class AkbServiceImpl implements AkbService, AkbNames {
	private final static Log log = LogFactory.getLog(AkbServiceImpl.class);

	/* DEPENDENCY INJECTION */
	private Repository repository;

	// Populate the repository in a demo context.
	private Map<String, Resource> demoData = null;

	/* Life cycle management */
	/**
	 * Call by each startup in order to make sure the backend is ready to
	 * receive/provide data.
	 */
	public void init() {
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

	/** Clean shutdown of the backend. */
	public void destroy() {
	}

	@Override
	public Node createAkbTemplate(Node parentNode, String name)
			throws RepositoryException {
		String connectorParentName = "Connectors";
		String itemsParentName = "Items";

		Node newTemplate = parentNode.addNode(name, AkbTypes.AKB_ENV_TEMPLATE);
		newTemplate.setProperty(Property.JCR_TITLE, name);

		Node connectorParent = newTemplate.addNode(connectorParentName,
				NodeType.NT_UNSTRUCTURED);
		connectorParent.addMixin(NodeType.MIX_TITLE);
		connectorParent.setProperty(Property.JCR_TITLE, connectorParentName);

		Node itemsParent = newTemplate.addNode(itemsParentName,
				NodeType.NT_UNSTRUCTURED);
		itemsParent.addMixin(NodeType.MIX_TITLE);
		itemsParent.setProperty(Property.JCR_TITLE, itemsParentName);

		return newTemplate;
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
}