package org.argeo.slc.akb.ui;

import org.argeo.slc.akb.AkbTypes;
import org.eclipse.swt.graphics.Image;

/** Shared icons. */
public class AkbImages {

	public final static Image LOGO_SMALL = AkbUiPlugin.getImageDescriptor(
			"icons/smallerOrnamentLogo.png").createImage();

	public final static Image TEMPLATE = AkbUiPlugin.getImageDescriptor(
			"icons/template.gif").createImage();
	public final static Image ACTIVE_ENV = AkbUiPlugin.getImageDescriptor(
			"icons/environment.png").createImage();
	
	public final static Image CONNECTOR_FOLDER = AkbUiPlugin
			.getImageDescriptor("icons/connectors.gif").createImage();

	public final static Image ITEM_FOLDER = AkbUiPlugin.getImageDescriptor(
			"icons/itemFolder.gif").createImage();
	
	public final static Image CONNECTOR_ALIAS = AkbUiPlugin.getImageDescriptor(
			"icons/addConnector.gif").createImage();

	public final static Image DEFAULT_CONNECTOR = AkbUiPlugin.getImageDescriptor(
			"icons/addConnector.gif").createImage();
	
	public final static Image JDBC_CONNECTOR = AkbUiPlugin.getImageDescriptor(
			"icons/jdbcConnector.gif").createImage();
	public final static Image JDBC_QUERY = AkbUiPlugin.getImageDescriptor(
			"icons/jdbcQuery.gif").createImage();

	public final static Image SSH_CONNECTOR = AkbUiPlugin.getImageDescriptor(
			"icons/sshConnector.png").createImage();
	public final static Image SSH_COMMAND = AkbUiPlugin.getImageDescriptor(
			"icons/sshCommand.png").createImage();
	public final static Image SSH_FILE = AkbUiPlugin.getImageDescriptor(
			"icons/sshFile.gif").createImage();

	public static Image getImageForAkbNodeType(String nodeType) {
		if (AkbTypes.AKB_JDBC_CONNECTOR.equals(nodeType))
			return JDBC_CONNECTOR;
		else if (AkbTypes.AKB_SSH_CONNECTOR.equals(nodeType))
			return SSH_CONNECTOR;
		else
			return null;
	}
}