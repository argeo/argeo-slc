package org.argeo.slc.ui.gis.wizards;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;

import org.argeo.jcr.JcrUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.gpx.TrackDao;
import org.eclipse.jface.wizard.Wizard;

public class ImportGpxWizard extends Wizard {
	private Node baseNode;
	private TrackDao trackDao;

	public ImportGpxWizard(TrackDao trackDao, Node baseNode) {
		super();
		this.trackDao = trackDao;
		this.baseNode = baseNode;
	}

	@Override
	public boolean performFinish() {
		Binary binary = null;
		try {
			for (NodeIterator children = baseNode.getNodes(); children
					.hasNext();) {
				Node child = children.nextNode();
				if (child.isNodeType(NodeType.NT_FILE)
						&& child.getName().endsWith(".gpx")) {
					binary = child.getNode(Property.JCR_CONTENT)
							.getProperty(Property.JCR_DATA).getBinary();
					trackDao.importTrackPoints(child.getPath(), "dan",
							binary.getStream());
					JcrUtils.closeQuietly(binary);
				}
			}
		} catch (RepositoryException e) {
			throw new SlcException("Cannot import GPS from " + baseNode, e);
		} finally {
			JcrUtils.closeQuietly(binary);
		}
		return false;
	}

}
